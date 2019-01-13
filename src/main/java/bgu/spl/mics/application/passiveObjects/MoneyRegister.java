package bgu.spl.mics.application.passiveObjects;


import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {
	private LinkedList <OrderReceipt> orders;
	private static class SingletonHolder {
		private static MoneyRegister instance = new MoneyRegister();
	}
	private MoneyRegister() {
		orders = new LinkedList<>();
	}
	/**
	 * Retrieves the single instance of this class.
	 */
	public static MoneyRegister getInstance() {
		return MoneyRegister.SingletonHolder.instance;
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		orders.add(r);
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		int output = 0;
		for(OrderReceipt order: orders){
			output = output + order.getPrice();
		}
		return output;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		c.setCreditCardAmount(c.getAvailableCreditAmount() -amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		FileOutputStream fileOut = null;
		ObjectOutputStream out = null;
		try {
			fileOut = new FileOutputStream(filename);
			out = new ObjectOutputStream(fileOut);
			out.writeObject(orders);
			out.close();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				out.close();
				fileOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}
