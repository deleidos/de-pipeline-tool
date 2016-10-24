package com.deleidos.framework.service.api.logging;

import java.util.List;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.udp.UdpListener;
import com.deleidos.analytics.common.udp.UdpMessageHandler;
import com.deleidos.analytics.stream.StreamManager;
import com.deleidos.analytics.websocket.WebSocketServer;
import com.deleidos.framework.service.FrameworkServiceMessageFactory;

/**
 * Stream log messages to consumers.
 * 
 * @author vernona
 */
public class LogMessageStreamer implements UdpMessageHandler {
	private static final Logger log = Logger.getLogger(LogMessageStreamer.class);

	private static LogMessageStreamer instance = new LogMessageStreamer();

	private UdpListener udpListener;
	
	

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static LogMessageStreamer getInstance() {
		return instance;
	}

	/**
	 * Private constructor enforces the singleton pattern.
	 */
	private LogMessageStreamer() {
	}

	public void init(int port) {
		try {
			udpListener = new UdpListener(port, this);
			udpListener.start();
		}
		catch (Throwable e) {
			log.error("Error initializing UDP listener", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Stream a message.
	 * 
	 * @param message
	 */
	public void streamMessage(String message) {
		List<String> consumerIds = StreamManager.getInstance()
				.getConsumersIds(FrameworkServiceMessageFactory.logMessageStreamWebSocketId);
		if (consumerIds != null && !consumerIds.isEmpty()) {
			if (message == null || message.isEmpty()) {
				message = "{}"; // Return an empty object if there is no data.
			}

			WebSocketServer.getInstance().send(message, consumerIds);
		}
	}

	/**
	 * UdpMessageHandler interface implementation.
	 * 
	 * Stream the handled message to consumers.
	 * 
	 * @param message
	 */
	@Override
	public void handleMessage(byte[] message) {
		streamMessage(new String(message));
	}

}
