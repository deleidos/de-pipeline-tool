package com.deleidos.analytics.websocket.api;

/**
 * The Interface for all messages handled by this WebSocketServer.  Should support getting and
 * setting an Id for the websocket connection and implement run() to process the message. 
 */
public interface WebSocketMessage extends Runnable {

	public String getWebSocketId();
	public void setWebSocketId(String webSocketId);
}
