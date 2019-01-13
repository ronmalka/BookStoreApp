package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.GetBookEvent;
import bgu.spl.mics.application.messages.GetBookPrice;
import bgu.spl.mics.application.messages.Terminate;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;

import java.util.concurrent.CountDownLatch;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:

 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	final private Inventory inventory;
	private CountDownLatch latch;
	private int currentTick;

	public InventoryService(CountDownLatch latch, int microServiceId) {
		super("Inventory Service: " + microServiceId);
		inventory = Inventory.getInstance();
		this.latch = latch;
		currentTick = 0;
	}

	@Override
	protected void initialize() {
		messageBus.register(this);
		//Check if there is enough money to the customer to order the book
		subscribeEvent(GetBookPrice.class, (GetBookPrice)->{
			synchronized (inventory){
				complete(GetBookPrice, inventory.checkAvailabiltyAndGetPrice(GetBookPrice.getBook()));
			}
		});
		//Check if the book is in the inventory
		subscribeEvent(GetBookEvent.class, (GetBookEvent)->{
			synchronized (inventory){
				complete(GetBookEvent, inventory.take(GetBookEvent.getBook()));
			}
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
