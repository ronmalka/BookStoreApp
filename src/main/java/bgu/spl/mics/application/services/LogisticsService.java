package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@linkResourcesHolder}, {@linkMoneyRegister}, {@linkInventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private CountDownLatch latch;
	private int currentTick = 0;
	public LogisticsService(CountDownLatch latch, int microServiceId) {
		super("LogisticsService: " + microServiceId );
		this.latch = latch;
	}

	@Override
	protected void initialize() {
		messageBus.register(this);
		subscribeEvent(DeliveryEvent.class,(DeliveryEvent)->{
			//Trying to get a vehicle
			Future<Future<DeliveryVehicle>> vehicleFuture = sendEvent(new GetVehicle());
			if(vehicleFuture != null){
				Future<DeliveryVehicle> vehicle = vehicleFuture.get();
				if(vehicle != null){
					DeliveryVehicle car = vehicle.get();
					car.deliver(DeliveryEvent.getCustomer().getAddress(),DeliveryEvent.getCustomer().getDistance());
					sendEvent(new ReleaseVehicle(car));
				}
			}
			complete(DeliveryEvent, null);
		});
		subscribeBroadcast(TickBroadcast.class, (tickBroadcast) -> {
			this.currentTick = tickBroadcast.getTick();
		});
		subscribeBroadcast(Terminate.class, (terminate)->{
			terminate();

		});
		latch.countDown();
	}

}
