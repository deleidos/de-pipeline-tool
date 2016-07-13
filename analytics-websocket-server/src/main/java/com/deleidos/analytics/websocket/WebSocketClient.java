package com.deleidos.analytics.websocket;

import java.net.URI;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;

import com.deleidos.analytics.common.util.JsonUtil;

/**
 * A simple auto reconnecting WebSocket client.
 */
public class WebSocketClient {

	private static final Logger logger = Logger.getLogger(WebSocketClient.class);
	private static final String DEFAULT_URI = "ws://localhost:8080/analytics";

	private String serverUri;
	private WebSocketClientEndpoint clientEndPoint;
	private org.eclipse.jetty.websocket.client.WebSocketClient client;
	private String initRequest;
	private LinkedBlockingQueue<String> queue;

	public WebSocketClient() {
		this.serverUri = DEFAULT_URI;
		queue = new LinkedBlockingQueue<String>();
	}

	public WebSocketClient(String serverUri) {
		this.serverUri = serverUri;
		queue = new LinkedBlockingQueue<String>();
	}

	public void setMessageReceiveQueue(LinkedBlockingQueue<String> queue) {
		this.queue = queue;
	}

	public void connect() {
		while (!isConnected()) {
			try {
				clientEndPoint = new WebSocketClientEndpoint();
				if (queue != null) {
					clientEndPoint.setMessageReceiveQueue(queue);
				}
				client = new org.eclipse.jetty.websocket.client.WebSocketClient();

				client.start();
				URI uri = new URI(serverUri);
				ClientUpgradeRequest request = new ClientUpgradeRequest();
				client.connect(clientEndPoint, uri, request).get();
				if (initRequest != null && !initRequest.isEmpty()) {
					sendString(initRequest);
				}
			}
			catch (Exception e) {
				logger.error("WebSocket client failed to connect to: " + serverUri);
				logger.error(e.toString());
				try {
					Thread.sleep(5000);
				}
				catch (InterruptedException e1) {}
			}
		}
	}

	public void close() {
		try {
			client.stop();
			client = null;
			clientEndPoint = null;
		}
		catch (Exception e) {
			logger.error("WebSocket client failed to close to: " + serverUri);
			logger.error(e.toString());
		}
	}

	public void sendString(String message) {
		if (!isConnected()) {
			connect();
		}
		clientEndPoint.sendString(message);
	}

	public void sendObject(Object message) throws Exception {
		if (!isConnected()) {
			connect();
		}
		clientEndPoint.sendString(JsonUtil.toJsonString(message));
	}

	public Future<Void> sendStringByFuture(String message) {
		if (!isConnected()) {
			connect();
		}
		return clientEndPoint.sendStringByFuture(message);
	}

	private boolean isConnected() {
		return (clientEndPoint != null && clientEndPoint.isConnected());
	}

	public void setInitRequest(String request) {
		this.initRequest = request;
	}

	public String getNextReceivedMessage() {
		return queue.poll();
	}
}
