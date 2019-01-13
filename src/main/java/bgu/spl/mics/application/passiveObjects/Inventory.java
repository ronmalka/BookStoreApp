package bgu.spl.mics.application.passiveObjects;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
	ConcurrentHashMap<String, BookInventoryInfo> books;
	private static class SingletonHolder {
		private static Inventory instance = new Inventory();
	}
	private Inventory() {
		books = new ConcurrentHashMap<>();
	}
	/**
	 * Retrieves the single instance of this class.
	 */
	public static Inventory getInstance() {
		return Inventory.SingletonHolder.instance;
	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[] inventory ) {
		for(int i = 0; i < inventory.length; i++){
			books.put(inventory[i].getBookTitle(), inventory[i]);
		}
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) {
		if(books.get(book) != null && books.get(book).getAmountInInventory() > 0){
			books.get(book).setAmount(books.get(book).getAmountInInventory() -1);
			return OrderResult.SUCCESSFULLY_TAKEN;
		} else {
			return OrderResult.NOT_IN_STOCK;
		}
	}
	
	
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		int output= -1;
		if(books.get(book) != null){
			output = books.get(book).getPrice();
		}
		return output;
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object ConcurrentHashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename){
		FileOutputStream fileOut = null;
		ObjectOutputStream out = null;
		try {
			Iterator it = books.entrySet().iterator();
			HashMap<String, Integer> output = new HashMap<>();
			while(it.hasNext()){
				ConcurrentHashMap.Entry pair = (ConcurrentHashMap.Entry)it.next();
				output.put((String) pair.getKey(), ((BookInventoryInfo)pair.getValue()).getAmountInInventory());
			}
			fileOut = new FileOutputStream(filename);
			out = new ObjectOutputStream(fileOut);
			out.writeObject(output);
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
