package com.deleidos.analytics.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.ConsoleAppender;

/**
 * Simple class to log JVM heap memory usage and log warnings if usage falls below a certain threshold.
 * 
 * @author adamv
 */
public class MemoryMonitor {

	private static final MemoryMonitor instance = new MemoryMonitor();
	private Logger logger = Logger.getLogger(MemoryMonitor.class);
	private Timer timer = new Timer(true);
	private boolean started = false;

	private static final float MB = 1024 * 1024;
	private static final int intervalMinutes = 5;
	private static final int intervalMillis = 1000 * 60 * intervalMinutes;
	private static final int availableMemoryWarningThreshold = 90; // %
	private static final String availableMemoryWarningMessage = "Available memory above threshold of "
			+ availableMemoryWarningThreshold + "%";

	/**
	 * Private constructor enforces the singleton pattern.
	 */
	private MemoryMonitor() {
	}

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static MemoryMonitor getInstance() {
		return instance;
	}

	public void setDebugMode() {
		logger.setLevel(Level.TRACE);
		logger.addAppender(new ConsoleAppender(new PatternLayout("[%p] %m%n"), "System.out"));
	}

	/**
	 * Start the memory monitor. Will only start it once no matter how many times it is called.
	 */
	public synchronized void start() {
		if (!started) {
			logMemoryUsage();
			timer.scheduleAtFixedRate(new MemoryMonitorTask(), 0, intervalMillis);
			started = true;
		}
	}

	public void logMemoryUsage() {
		System.gc();
		int heapSizeMB = (int) (((float) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()))
				/ MB);
		int heapMaxSizeMB = (int) ((float) Runtime.getRuntime().maxMemory() / MB);
		int heapTotalMB = (int) ((float) Runtime.getRuntime().totalMemory() / MB);
		// int percentAvailable = (int) ((float) Runtime.getRuntime().freeMemory() * 100f
		// / (float) Runtime.getRuntime().maxMemory());
		int percentUsed = (int) (heapSizeMB * 100f / heapMaxSizeMB);

		String message = "Current heap usage MB = " + heapSizeMB + "MB out of " + heapMaxSizeMB
				+ "MB max, percent used = " + percentUsed + "%. Total currently allocated memory is " + heapTotalMB
				+ "MB.";
		if (percentUsed >= availableMemoryWarningThreshold) {
			logger.warn(availableMemoryWarningMessage + ": " + message);
		}
		else {
			logger.info(message);
		}
	}

	/**
	 * Get the size of an object in MB. May not be 100% accurate, but it should be in the ballpark. Only use this for
	 * temporary debugging. Do NOT leave calls to this method in production code unless debugging an issue.
	 * 
	 * @return
	 */
	public float getObjectSizeMB(Serializable object) {
		float size = 0;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			oos.close();
			size = (((float) baos.size()) / (1024f * 1024f));
		}
		catch (IOException e) {
		}
		return size;
	}

	private class MemoryMonitorTask extends TimerTask {

		@Override
		public void run() {
			logMemoryUsage();
		}

	}
}
