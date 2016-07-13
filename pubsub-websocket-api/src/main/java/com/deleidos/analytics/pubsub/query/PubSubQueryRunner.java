package com.deleidos.analytics.pubsub.query;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.deleidos.analytics.pubsub.PubSubManager;
import com.deleidos.analytics.websocket.WebSocketServer;

public class PubSubQueryRunner {

	protected static final Logger logger = Logger.getLogger(PubSubQueryRunner.class);
	
	private final ExecutorService es = Executors
		.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	
	private final CompletionService<PubSubResult> ecs =
    	new ExecutorCompletionService<PubSubResult>(es);
	
	private boolean keepRunning = true;
	
	public PubSubQueryRunner() {
		Thread t = new Thread(new ResultChecker());
		t.setDaemon(true);
		t.start();
	}
	
	public void run(PubSubQueryCallable query) {
		ecs.submit(query);
	}
	
	public void stop() {
		keepRunning = false;
		es.shutdown();
	}
	
	private class ResultChecker implements Runnable {
		public void run() {
			while (keepRunning) {
				try {
					Future<PubSubResult> future = ecs.poll(1, TimeUnit.SECONDS);
					if (future != null) {
						PubSubResult result = future.get();
						if (result != null) {
							List<String> subscriberIds = PubSubManager.getInstance().getSubscriberIds(
									result.getSessionId(), result.getTopic(), result.getWindow());
							if (subscriberIds != null) {
								logger.info("Sending to pubsub topic:" + result.getTopic() + " window:" + 
									result.getWindow() + " message:" + result.getMessage());
								WebSocketServer.getInstance().send(result.getMessage(), subscriberIds);
							}
						}
					}
				} catch (Exception e) {
					logger.error("Unexpected exception: " + e.toString());
				}
			}
		}
	}
}
