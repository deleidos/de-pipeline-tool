package com.deleidos.analytics.websocket.api;

import java.util.List;

/**
 * Interface to implement for data specific API providers (social, transportation, etc.)
 * to configure the WebSocketServer to handle data specific messages. 
 */
public interface WebSocketApiPlugin {

	public List<WebSocketEventListener> getWebSocketEventListeners();
	public List<WebSocketMessageFactory> getWebSocketMessageFactories();
	public List<String> getResourcePackages();
	
}
