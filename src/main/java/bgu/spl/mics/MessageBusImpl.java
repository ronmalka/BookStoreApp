package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	final private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> messageQueueMap;
	final private ConcurrentHashMap<Class<? extends Event>, Queue<MicroService>> eventQueueMap;
	final private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastQueueMap;
	final private ConcurrentHashMap<Event,Future> eventFutureMap ;

	private static class SingletonHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	private MessageBusImpl(){
		messageQueueMap = new ConcurrentHashMap<>();
		eventQueueMap = new ConcurrentHashMap<>();
		broadcastQueueMap = new ConcurrentHashMap<>();
		eventFutureMap = new ConcurrentHashMap<>();

	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (eventQueueMap) {
			while(messageQueueMap.get(m) == null);
			if(!eventQueueMap.containsKey(type)) {
				Queue<MicroService> list = new ConcurrentLinkedQueue<>();
				eventQueueMap.put(type, list);
			}
			Queue<MicroService> list = eventQueueMap.get(type);
			list.add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (broadcastQueueMap){
			while(messageQueueMap.get(m) == null);
			if(!broadcastQueueMap.containsKey(type)) {
				ConcurrentLinkedQueue<MicroService> list = new ConcurrentLinkedQueue<>();
				broadcastQueueMap.put(type, list);
			}
			ConcurrentLinkedQueue<MicroService> list = broadcastQueueMap.get(type);
			list.add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		synchronized (eventFutureMap){
			if(eventFutureMap.get(e) != null) {
				((Future<T>)eventFutureMap.get(e)).resolve(result);
			}
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (broadcastQueueMap){
			synchronized (messageQueueMap){
				if(broadcastQueueMap.containsKey(b.getClass())){
					for(MicroService microService: broadcastQueueMap.get(b.getClass())){
						ConcurrentLinkedQueue<Message> broadcastList = messageQueueMap.get(microService);
						if(broadcastList != null){
							broadcastList.add(b);
						}
					}
				}
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {

		if (eventQueueMap.containsKey(e.getClass()) && eventQueueMap.get(e.getClass()) !=null && !eventQueueMap.get(e.getClass()).isEmpty()) {
			MicroService microService = eventQueueMap.get(e.getClass()).remove();
			if (microService != null && messageQueueMap.get(microService) !=null) {
				synchronized (messageQueueMap.get(microService)) {
					ConcurrentLinkedQueue<Message> serviceQueue = messageQueueMap.get(microService);
					if (serviceQueue != null) {
						serviceQueue.add(e);
						Future<T> future = new Future<>();
						synchronized (eventFutureMap) {
							eventFutureMap.put(e, future);
							eventQueueMap.get(e.getClass()).add(microService);
							return future;
						}
					}
				}
			}
		}


		return null;
	}


	@Override
	public void register(MicroService m) {
		this.messageQueueMap.put(m, new ConcurrentLinkedQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		Iterator it = this.eventQueueMap.entrySet().iterator();
		while(it.hasNext()){
			ConcurrentHashMap.Entry pair = (ConcurrentHashMap.Entry)it.next();
			((ConcurrentLinkedQueue)pair.getValue()).remove(m);
		}
		Iterator<java.util.Map.Entry<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>>> it2 = this.broadcastQueueMap.entrySet().iterator();
		while(it2.hasNext()){
			java.util.Map.Entry<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> pair = it2.next();
			(pair.getValue()).remove(m);
		}
		for(Message message: messageQueueMap.get(m)){
			if(eventFutureMap.get(message) != null){
				eventFutureMap.get(message).resolve(null);
			}
		}
		this.messageQueueMap.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) {
		if (messageQueueMap.get(m) != null) {
				while (messageQueueMap.get(m).isEmpty()) ;
				return messageQueueMap.get(m).poll();
		}
		return null;

	}

	public static MessageBusImpl getInstance(){
		return SingletonHolder.instance;
	}

	

}
