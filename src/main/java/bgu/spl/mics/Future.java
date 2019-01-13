package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class  Future<T> {
	private T result;
	private boolean isDone;

	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		result = null;
		isDone = false;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */
	synchronized public T get() {
		if(!isDone){
			do{
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (!isDone);
		}
		return result;
	}
	
	/**
     * Resolves the result of this Future object.
     */
	synchronized public void resolve (T result) {
		this.result=result;
		isDone = true;
		this.notifyAll();
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isDone() {
		return isDone;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @paramtimout 	the maximal amount of time units to wait for the result.
     * @paramunit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public T get(long timeout, TimeUnit unit) {
		long milliSecond = TimeUnit.MILLISECONDS.convert(timeout, unit);
        if(!isDone){
            try {
                Thread.currentThread().sleep(milliSecond);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
	}

}
