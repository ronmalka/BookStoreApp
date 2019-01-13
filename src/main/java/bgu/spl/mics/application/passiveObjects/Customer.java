package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {
	int id;
	String name;
	String address;
	int distance;
	int creditCardNumber;
	int creditCardAmount;
	ConcurrentHashMap<Integer, LinkedList<String>> orderSchedule;
	List<OrderReceipt> orderReceipts;
	public Customer(int id, String name, String address,int distance,int creditCardNumber,int creditCardAmount){
	this.id= id;
	this.name = name;
	this.address = address;
	this.distance = distance;
	this.creditCardNumber = creditCardNumber;
	this.creditCardAmount = creditCardAmount;
	orderSchedule = new ConcurrentHashMap<>();
	orderReceipts = new LinkedList<>();
	}


	/**
	 * Retrieves the name of the customer.
	 */
	public String getName() {
		return name;
	}
	public void setName(String name){
		this.name = name;
	}


	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id;
	}


	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}


	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return orderReceipts;
	}


	public void addOrderReceipt(OrderReceipt receipt){
		orderReceipts.add(receipt);
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return creditCardAmount;
	}

	public void setCreditCardAmount(int creditCardAmount) {
		this.creditCardAmount = creditCardAmount;
	}


	//orderSchedule
	public ConcurrentHashMap<Integer, LinkedList<String>> getOrderSchedule(){
		return orderSchedule;
	}
	public void addOrderSchedule(String bookTitle, int bookTick){
		if(orderSchedule.get(bookTick) == null){
			orderSchedule.put(bookTick, new LinkedList<>());
		}
		orderSchedule.get(bookTick).add(bookTitle);
		}

}
