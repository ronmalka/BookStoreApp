package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	MoneyRegister moneyRegister;
	private CountDownLatch latch;
	private int currentTick;

	public SellingService(CountDownLatch latch, int microServiceId) {
		super("Selling Service: " +  microServiceId);
		moneyRegister = MoneyRegister.getInstance();
		this.latch = latch;
		currentTick = 0;

	}

	@Override
	protected void initialize() {

		messageBus.register(this);
		subscribeEvent(BookOrderEvent.class, (bookOrderEvent)-> {
			OrderReceipt order = null;
			int processTick = currentTick;
				//Check if there is enough money to the customer to order the book
				Future<Integer> getPrice = sendEvent(new GetBookPrice(bookOrderEvent.getBookName()));
				int price;
				if (getPrice != null && getPrice.get() != null) {
					price = getPrice.get();
				} else {
					price = -1;
				}

			if (price >= 0) {

				synchronized (bookOrderEvent.getCustomer()) {
					if (price <= bookOrderEvent.getCustomer().getAvailableCreditAmount()) {
						//Check if the book is in the inventory
						Future<OrderResult> getBook = sendEvent(new GetBookEvent(bookOrderEvent.getBookName()));
						OrderResult result;
						if (getBook != null) {
							result = getBook.get();
						} else {
							result = OrderResult.NOT_IN_STOCK;
						}
						if (result == OrderResult.SUCCESSFULLY_TAKEN) {
							moneyRegister.chargeCreditCard(bookOrderEvent.getCustomer(), price);
							order = new OrderReceipt(bookOrderEvent.getOrderId().getId(), bookOrderEvent.getCustomer().getId(), price, bookOrderEvent.getBookName(), getName());
							bookOrderEvent.getOrderId().Increase();
							order.setOrderTick(bookOrderEvent.getOrderTick());
							order.setProcessTick(processTick);
							order.setIssuedTick(currentTick);
							complete(bookOrderEvent, order);
						} else {
							complete(bookOrderEvent, order);
						}
					} else {
						complete(bookOrderEvent, order);
					}
				}
			}
				//Deliver if the order SUCCESSFULLY_TAKEN
				if (order != null) {
					sendEvent(new DeliveryEvent(bookOrderEvent.getCustomer()));
					bookOrderEvent.getCustomer().addOrderReceipt(order);
					moneyRegister.file(order);

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
