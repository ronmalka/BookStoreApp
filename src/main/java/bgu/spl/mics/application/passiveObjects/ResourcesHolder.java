package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	ConcurrentLinkedQueue<DeliveryVehicle> deliveryVehicles;
	Semaphore semaphore;
	Queue<Future<DeliveryVehicle>> vehicleFutureNotResolve ;

	private static class SingletonHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}
	private ResourcesHolder() {
		deliveryVehicles = new ConcurrentLinkedQueue<>();
		vehicleFutureNotResolve = new LinkedList<>();
	}
	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		return SingletonHolder.instance;
	}

	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> vehicleFuture = new Future<>();
		if(semaphore.tryAcquire()){
			vehicleFuture.resolve(deliveryVehicles.remove());
		}
		else {
			vehicleFutureNotResolve.add(vehicleFuture);
		}
		return vehicleFuture;

	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		if(!vehicleFutureNotResolve.isEmpty()){
			Future<DeliveryVehicle> car = vehicleFutureNotResolve.remove();
			car.resolve(vehicle);
		} else {
			deliveryVehicles.add(vehicle);
			semaphore.release();
		}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		semaphore = new Semaphore(vehicles.length);
		for(DeliveryVehicle vehicle : vehicles){
			this.deliveryVehicles.add(vehicle);
		}
	}
}
