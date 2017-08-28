package com.deleidos.framework.service.api.logging;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.udp.UdpListener;
import com.deleidos.analytics.common.udp.UdpMessageHandler;
import com.deleidos.analytics.common.util.GsonFactory;
import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.analytics.stream.StreamManager;
import com.deleidos.analytics.websocket.WebSocketServer;
import com.deleidos.analytics.websocket.event.NewConsumerEvent;
import com.deleidos.analytics.websocket.event.ServiceEventBus;
import com.deleidos.framework.service.FrameworkServiceMessageFactory;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;

/**
 * Stream log messages to consumers.
 * 
 * @author vernona
 */
public class LogMessageStreamer implements UdpMessageHandler {
	private static final Logger log = Logger.getLogger(LogMessageStreamer.class);
	private static LogMessageStreamer instance = new LogMessageStreamer();
	private UdpListener udpListener;
	private ArrayList<String> messageBacklog = new ArrayList<String>();
	private Gson gson = GsonFactory.getInstance().getGsonWithCollectionDeserializers();

	private static final int backlogCount = 500;

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static LogMessageStreamer getInstance() {
		return instance;
	}

	/**
	 * Private constructor enforces the singleton pattern. Register as a handler with the event bus.
	 */
	private LogMessageStreamer() {
		ServiceEventBus.getInstance().registerHandler(this);
		;
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
		String messageString = new String(message);
		streamMessage(messageString);
		messageBacklog.add(messageString);
		if (messageBacklog.toArray().length > backlogCount) {
			messageBacklog.remove(0);
		}
	}

	@Subscribe
	public void handleNewConsumer(NewConsumerEvent consumerEvent) throws Exception {
		if (consumerEvent.getTopic().equals("log_message")) {
			WebSocketServer.getInstance().send(gson.toJson(messageBacklog), consumerEvent.getId());
		}
	}

}
