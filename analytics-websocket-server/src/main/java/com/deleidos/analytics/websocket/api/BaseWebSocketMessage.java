package com.deleidos.analytics.websocket.api;

import org.apache.log4j.Logger;

import com.deleidos.analytics.websocket.WebSocketServer;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract base class for all messages that this WebSocket server can process. All extending classes must implement the
 * processMessage method to do the real work. All messages are run from a Thread pool.
 */
public abstract class BaseWebSocketMessage implements WebSocketMessage {

	protected static transient final Logger logger = Logger.getLogger(BaseWebSocketMessage.class);

	protected WebSocketServer webSocketServer;

	public String webSocketId;

	@JsonIgnore
	public String getWebSocketId() {
		return webSocketId;
	}

	@JsonIgnore
	public void setWebSocketId(String webSocketId) {
		this.webSocketId = webSocketId;
	}

	@Override
	public void run() {
		try {
			webSocketServer = WebSocketServer.getInstance();
			processMessage();
		}
		catch (Exception e) {
			logger.error("Unexpected error processing request: ", e);
		}
	}

	public void sendResponse(String s) throws Exception {
		webSocketServer.send(s, webSocketId);
	}
	
	public void sendResponse(Object obj) throws Exception {
		webSocketServer.send(obj, webSocketId);
	}

	public void sendResponse(Object obj, String sessionId) throws Exception {
		webSocketServer.send(obj, sessionId);
	}

	public abstract void processMessage() throws Exception;
}
