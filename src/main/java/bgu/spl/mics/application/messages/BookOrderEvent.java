package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderId;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event<OrderReceipt> {
    private Customer customer;
    private String bookName;
    private int orderTick;
    private OrderId orderId;
    public BookOrderEvent(Customer customer, String bookName, int orderTick,OrderId orderId) {
        this.customer = customer;
        this.bookName = bookName;
        this.orderTick = orderTick;
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }


    public String getBookName() {
        return bookName;
    }

    public int getOrderTick() {
        return orderTick;
    }

    public OrderId getOrderId() {
        return orderId;
    }
}
