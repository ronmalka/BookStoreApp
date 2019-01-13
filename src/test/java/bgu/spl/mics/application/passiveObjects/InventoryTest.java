package bgu.spl.mics.application.passiveObjects;

import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {
    Inventory inventory = Inventory.getInstance();

    /**
     *POST: Check that there can be only one of instance of Inventory
     **/
    @Test
    public void getInstance() {
       //singalton check
        Inventory inv2 = Inventory.getInstance();
        assertEquals(inventory, inv2);
    }

    /**
     *PRE: Check that an empty book list was initionlized with constructor
     *POST: After loading some books to the list, checking that books were loaded correctly
     **/
    @Test
    public void load() {

    }

    /**
     *PRE: Checking that a list of books is initionlized (and therefore not being null)
     *POST: Seeing that a book is we get an inform if not in the inentory,
     * checking that the quantity of the book decreases after each action and that
     * after quantity is 0 we get an inform that its not in stock
     **/
    @Test
    public void take() {

    }

    /**
     *PRE: Checking that a list of books is initionlized (and therefore not being null)
     *POST: Seeing that a book is we get an inform if not in the inentory,
     * and that we get the price if it is in the inventory
     **/
    @Test
    public void checkAvailabiltyAndGetPrice() {

    }

    @Test
    public void printInventoryToFile() {
    }
}
