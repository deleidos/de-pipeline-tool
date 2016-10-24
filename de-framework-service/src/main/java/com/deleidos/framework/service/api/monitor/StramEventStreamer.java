package com.deleidos.framework.service.api.monitor;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.monitoring.EventLogger;
import com.deleidos.framework.monitoring.LogUtil;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Created by mollotb on 7/22/16.
 */
public class StramEventStreamer {

	private static transient Logger logger = Logger.getLogger(StramEventStreamer.class);

	public static StramEventStreamer instance = new StramEventStreamer();

	private static final long UPDATE_TIME = 10000;
	// <Web Socket Id, <App Id, Update thread>>
	private static Map<String, Map<String, Thread>> threads = new HashMap<String, Map<String, Thread>>();

	public static void startStream(String appId, String webSocketId) {

		if (!threads.containsKey(webSocketId) || threads.get(webSocketId).containsKey(appId)) {
			if (!threads.containsKey(webSocketId))
				threads.put(webSocketId, new HashMap<String, Thread>());
			threads.get(webSocketId).put(appId, new Thread(() -> {
				try {
					EventLogger el = LogUtil.getLogger(appId);
					// Wait for the event logger to be ready (takes around 30 seconds!)
					while (!Thread.interrupted()) {
						if (el.hasNext()) {
							String msg = "";
							while (el.hasNext() && !Thread.interrupted()) {
								msg += el.next();
							}
							BaseWebSocketMessage wsMessage = (BaseWebSocketMessage) new SendStramEvent(msg);
							wsMessage.setWebSocketId(webSocketId);
							wsMessage.run();
						}

						Thread.sleep(UPDATE_TIME);
					}

				}
				catch (Exception e) {
					logger.debug(e.getMessage(), e);
				}
			}));
			threads.get(webSocketId).get(appId).start();
		}
		// Ignore request if already streaming this app's logs to this client
	}

	public static void stopStream(String appId, String webSocketId) {
		if (threads.containsKey(webSocketId) && threads.get(webSocketId).containsKey(appId)) {
			threads.get(webSocketId).get(appId).interrupt();
		}
	}
}
