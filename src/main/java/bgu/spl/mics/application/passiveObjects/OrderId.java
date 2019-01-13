package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicInteger;

public class OrderId {
    private AtomicInteger orderId ;
    public OrderId(){
        this.orderId = new AtomicInteger(0);
    }
    public synchronized void Increase (){
        orderId.compareAndSet(orderId.intValue(), orderId.intValue()+1);
    }
    public int getId(){
        return orderId.intValue();
    }
}
