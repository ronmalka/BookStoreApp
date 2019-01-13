package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.GetVehicle;
import bgu.spl.mics.application.messages.ReleaseVehicle;
import bgu.spl.mics.application.messages.Terminate;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@linkResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@linkMoneyRegister}, {@linkInventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	private ResourcesHolder resourcesHolder;
	private CountDownLatch latch;
	private int currentTick = 0 ;
	public ResourceService(CountDownLatch latch, int microServiceId) {
		super("ResourceService: " + microServiceId);
		resourcesHolder = ResourcesHolder.getInstance();
		this.latch = latch;
	}

	@Override
	protected void initialize() {
		messageBus.register(this);
		//Check if there is available vehicle
		subscribeEvent(GetVehicle.class,(GetVehicle)-> {
			Future<DeliveryVehicle> vehicleFuture = resourcesHolder.acquireVehicle();
			complete(GetVehicle,vehicleFuture);
		});
		//Release a vehicle that done delivery
		subscribeEvent(ReleaseVehicle.class,(ReleaseVehicle)->{
			resourcesHolder.releaseVehicle(ReleaseVehicle.getVehicle());
			complete(ReleaseVehicle, null);
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
