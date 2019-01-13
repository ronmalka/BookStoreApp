package bgu.spl.mics;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;
/**INV: get result of type T if it is available, if not wait until it is available.**/
public class FutureTest <T>{
    Future <T>future = new Future<>();
    T result = future.get();
    /**
     *PRE:future.isDone()
     *POST:if the result it is not available, wait until it is resolve
    **/
    @Test
    public void get() {
        Assert.assertTrue(future.isDone());

    }
    /**
     *PRE:!isDone()
     *POST:isDone()
     **/
    @Test
    public void resolve() {
        Future future = new Future<>();
        Assert.assertFalse(future.isDone());
        future.resolve(result);
        Assert.assertTrue(future.isDone());
    }
    /**
     *PRE:future.get() != null
     *POST:none
     **/
    @Test
    public void isDone() {
        Assert.assertNotNull(result);
    }
    /**
     *PRE:The result is type T and is available
     *POST:if the result it is not available, wait until it is resolve or until the time = timeout
     **/
    @Test
// get(long timeout, TimeUnit unit)
    public void get1() {
        Assert.assertTrue(future.isDone());
    }
}