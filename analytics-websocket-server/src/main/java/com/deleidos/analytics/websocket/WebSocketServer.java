package com.deleidos.analytics.websocket;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.analytics.websocket.api.WebSocketApiPlugin;
import com.deleidos.analytics.websocket.api.WebSocketEventListener;
import com.deleidos.analytics.websocket.api.WebSocketMessage;
import com.deleidos.analytics.websocket.api.WebSocketMessageFactory;

/**
 * The WebSocket Server implementation for the Analytics Data Service service.  Processes stream,
 * request-response (req-resp), and pub-sub messages.  Mainly responsible for keeping track of all
 * WebSocket client connections and client connection state (is the connection a publisher, subscriber,
 * etc.)
 */
public class WebSocketServer {

	private static final Logger logger = Logger.getLogger(WebSocketServer.class);
	
	// webSocketId|WebSocketServerEndpoint
	private final Map<String, WebSocketServerEndpoint> webSocketMap = 
		new ConcurrentHashMap<String, WebSocketServerEndpoint>(16, 0.9f, 1);
	private final CopyOnWriteArrayList<WebSocketEventListener> eventListenerList =
			new CopyOnWriteArrayList<WebSocketEventListener>();
	
	private final int cores = Runtime.getRuntime().availableProcessors();
	private final ExecutorService executorService = Executors.newFixedThreadPool(cores * 4);
	private final CopyOnWriteArrayList<WebSocketMessageFactory> messageFactoryList = 
			new CopyOnWriteArrayList<WebSocketMessageFactory>();
	
	private WebSocketServer() {};
	
	private static class SingletonHolder {
		private static WebSocketServer INSTANCE = new WebSocketServer();
	}

	public static WebSocketServer getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public void addWebSocket(WebSocketServerEndpoint webSocket) {
		String webSocketId = webSocket.getId();
		webSocketMap.put(webSocketId, webSocket);
		for (WebSocketEventListener eventListener: eventListenerList) {
			eventListener.onWebSocketConnect(webSocketId);
		}
		logger.info("Added WebSocket: " + webSocketId + ", total count is: " + webSocketMap.size());
	}
	
	public void removeWebSocket(WebSocketServerEndpoint webSocket) {
		String webSocketId = webSocket.getId();
		webSocketMap.remove(webSocketId);
		for (WebSocketEventListener eventListener: eventListenerList) {
			eventListener.onWebSocketClose(webSocketId);
		}
		logger.info("Removed WebSocket: " + webSocketId + ", total count is: " + webSocketMap.size());
	}
	

	public void send(Object obj, String webSocketId) throws Exception {
		String message = JsonUtil.toJsonString(obj);
		if (message != null) {
			send(message, webSocketId);
		}
	}
	
	public void send(String message, String webSocketId) {
		WebSocketServerEndpoint ws = webSocketMap.get(webSocketId);
		if (ws != null) {
			ws.sendString(message);
		}
	}
	
	public void send(String message, List<String> webSocketIds) {
		for (String webSocketId: webSocketIds) {
			send(message, webSocketId);
		}
	}
	
	public void processMessage(String message, String webSocketId) {
		for (WebSocketMessageFactory factory: messageFactoryList) {
			WebSocketMessage wsMessage = null;
			try {
				wsMessage = factory.buildMessage(message, webSocketId);
			} catch (Exception e) {
				logger.warn("Message factory buildMessage failed: " + e);
			}
			if (wsMessage != null) {
				wsMessage.setWebSocketId(webSocketId);
				executorService.execute(wsMessage);
			}
		}
	}
	
	public void registerPlugin(WebSocketApiPlugin plugin) {
		if (plugin.getWebSocketEventListeners() != null) {
			for (WebSocketEventListener listener: plugin.getWebSocketEventListeners()) {
				logger.info("Registering WebSocketEventListener: " + listener.getClass().getSimpleName());
				eventListenerList.add(listener);
			}
		}
		
		if (plugin.getWebSocketMessageFactories() != null) {
			for (WebSocketMessageFactory factory: plugin.getWebSocketMessageFactories()) {
				logger.info("Registering MessageFactory: " + factory.getClass().getSimpleName());
				messageFactoryList.add(factory);
			}
		}
	}
	
}
