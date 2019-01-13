package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class GetBookPrice implements Event<Integer> {
    private String bookName;

    public GetBookPrice(String bookName) {
        this.bookName = bookName;
    }

    public String getBook() {
        return bookName;
    }
}
