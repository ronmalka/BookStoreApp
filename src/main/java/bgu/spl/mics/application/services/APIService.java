package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.Terminate;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderId;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:

 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	private Customer customer;
	private CountDownLatch latch;
	private OrderId orderId;
	private int currentTick;
	public APIService(Customer customer,CountDownLatch latch,OrderId orderId, int microServiceId ) {
		super("Api Service: " + microServiceId);
		this.customer = customer;
		this.latch = latch ;
		this.orderId = orderId;
		this.currentTick = 0;
	}

	@Override
	protected void initialize() {
		messageBus.register(this);
		//Send order event in right tick
		subscribeBroadcast(TickBroadcast.class, (TickBroadcast)->{
			this.currentTick = TickBroadcast.getTick();
			if (customer.getOrderSchedule().get(TickBroadcast.getTick())  != null){
				LinkedList<Future<OrderReceipt>> futureList = new LinkedList<>();
				for(String book: customer.getOrderSchedule().get(TickBroadcast.getTick())){
					futureList.addFirst(sendEvent(new BookOrderEvent(customer, book,TickBroadcast.getTick(),orderId)));
				}
			}
		});
		subscribeBroadcast(Terminate.class, (terminate)->{
			terminate();
		});
		latch.countDown();
	}

}
