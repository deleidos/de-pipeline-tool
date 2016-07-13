package com.deleidos.analytics.websocket.api;

/**
 * An interface for API plugins to use to be notified when WebSocket connections are 
 * opened or closed.
 */
public interface WebSocketEventListener {

	public void onWebSocketClose(String webSocketId);
	public void onWebSocketConnect(String webSocketId);
}
