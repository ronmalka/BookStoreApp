package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.OrderResult;

public class GetBookEvent implements Event<OrderResult> {
    private String bookName;
    public GetBookEvent(String bookName){
        this.bookName = bookName;
    }

    public String getBook() {
        return bookName;
    }
}
