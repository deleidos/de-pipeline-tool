package com.deleidos.framework.model.event;

/**
 * Simple class to periodically generate generic deployment complete notification for testing.
 * 
 * TODO remove this class
 * 
 * @author vernona
 */
public class EventGenerator implements Runnable {

	public EventGenerator() {
		(new Thread(this)).start();
	}

	@Override
	public void run() {
		while (true) {
			System.out.println("posting deployment complete notification");
			SystemEventBus.getInstance().deploymentComplete("systemId." + System.currentTimeMillis());
			try {
				Thread.sleep(5000);
			}
			catch (InterruptedException e) {
			}
		}
	}

}
