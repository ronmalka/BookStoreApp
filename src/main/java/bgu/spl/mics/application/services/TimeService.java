package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;


public class TimeService extends MicroService{
	private int speed;
	private int duration;
	private int currentTick;
	private CountDownLatch latch;

	public TimeService(int speed, int duration, CountDownLatch latch, int microServiceId) {
		super("Time Service: " + microServiceId);
		this.speed = speed;
		this.duration = duration;
		this.currentTick = 1;
		this.latch = latch;

	}

	@Override
	protected void initialize() {
		messageBus.register(this);
		try {
			latch.await(); // wait until the other service initialize
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				sendBroadcast(new TickBroadcast(currentTick));
				currentTick++;
				if(currentTick > duration){
					timer.cancel();
					timer.purge();
					sendBroadcast(new Terminate());
				}
			}
		};
		timer.schedule(task, 0, speed);
		terminate();

	}

}
