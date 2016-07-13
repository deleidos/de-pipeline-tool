package com.deleidos.analytics.stream.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.analytics.pubsub.PubSubManager;
import com.deleidos.analytics.pubsub.PubSubWebSocketApiPlugin;
import com.deleidos.analytics.pubsub.query.TestPubSubQueryCallable;
import com.deleidos.analytics.websocket.WebSocketClient;
import com.deleidos.analytics.websocket.WebSocketServer;
import com.deleidos.analytics.websocket.api.plugin.WebSocketService;

public class TestWebSocketClient extends WebSocketClient {

	public static final String WS_ENDPOINT = "ws://localhost:8080/analytics";

	private LinkedBlockingQueue<String> receivedMessages = new LinkedBlockingQueue<String>();
	private String sessionId;

	private static WebSocketService server;

	static {
		WebSocketServer.getInstance().registerPlugin(new PubSubWebSocketApiPlugin());
		PubSubManager.getInstance().registerPubSubQueryCallable(new TestPubSubQueryCallable());
		server = new WebSocketService(8080);
		server.start();
	}

	public static void initServer() {
		// inits the server in the static block above
	}

	public TestWebSocketClient(String endPoint, Object initialMessage) throws Exception {
		super(endPoint);
		this.setMessageReceiveQueue(receivedMessages);
		if (initialMessage != null) {
			this.setInitRequest(JsonUtil.toJsonString(initialMessage));
		}
	}

	public Queue<String> getReceivedMessages() {
		return receivedMessages;
	}

	public List<String> getReceivedMessagesAsList() {
		List<String> messages = new ArrayList<String>();
		String message = null;
		while ((message = receivedMessages.poll()) != null) {
			messages.add(message);
		}
		return messages;
	}

	public void sendMessages(List<String> messages) {
		for (String message : messages) {
			sendString(message);
		}
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
