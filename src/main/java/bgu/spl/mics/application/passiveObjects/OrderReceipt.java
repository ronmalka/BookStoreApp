package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements Serializable {
	private int orderId;
	private int customerId;
	 private int price;
	private String bookTitle;
	private int issuedTick;
	private int orderTick;
	private int processTick;
	private String seller;
	public OrderReceipt(int orderId,int customerId,int price,String bookTitle,String seller	){
	this.orderId = orderId;
	this.customerId = customerId;
	this.price = price;
	this.bookTitle = bookTitle;
	this.seller = seller;
	}

	/**
     * Retrieves the orderId of this receipt.
     */
	public int getOrderId() {
		return orderId;
	}
	
	/**
     * Retrieves the name of the selling service which handled the order.
     */
	public String getSeller() {
		return seller;
	}
	
	/**
     * Retrieves the ID of the customer to which this receipt is issued to.
     * <p>
     * @return the ID of the customer
     */
	public int getCustomerId() {
		return customerId;
	}
	
	/**
     * Retrieves the name of the book which was bought.
     */
	public String getBookTitle() {
		return bookTitle;
	}
	
	/**
     * Retrieves the price the customer paid for the book.
     */
	public int getPrice() {
		return price;
	}
	
	/**
     * Retrieves the tick in which this receipt was issued.
     */
	public int getIssuedTick() {
		return issuedTick;
	}
	
	/**
     * Retrieves the tick in which the customer sent the purchase request.
     */
	public int getOrderTick() {
		return orderTick;
	}
	
	/**
     * Retrieves the tick in which the treating selling service started 
     * processing the order.
     */
	public int getProcessTick() {
		return processTick;
	}

	public void setIssuedTick(int issuedTick) {
		this.issuedTick = issuedTick;
	}

	public void setOrderTick(int orderTick) {
		this.orderTick = orderTick;
	}

	public void setProcessTick(int processTick) {
		this.processTick = processTick;
	}
	@Override
	public String toString() {

		String output = "orderId:" + orderId + "\n" +
				"customerId: " + customerId + "\n" +
				"price: " + price + "\n" +
				"bookTitle: " + bookTitle + "\n" +
				"issuedTick: " + issuedTick + "\n" +
				"orderTick: " + orderTick + "\n" +
				"processTick: " + processTick + "\n" +
				"seller: " + seller + "\n";
		return output;
	}
}



