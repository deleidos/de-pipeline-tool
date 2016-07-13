package com.deleidos.analytics.websocket.api;

/**
 * An interface for building WebSocketMessages from JSON strings. Implementors should return null if JSON message is
 * something they are not interested in.
 */
public interface WebSocketMessageFactory {

	public WebSocketMessage buildMessage(String message, String webSocketId) throws Exception;
}
