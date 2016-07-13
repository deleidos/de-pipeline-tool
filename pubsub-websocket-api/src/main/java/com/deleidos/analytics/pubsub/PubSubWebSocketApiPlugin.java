package com.deleidos.analytics.pubsub;

import java.util.Arrays;
import java.util.List;

import com.deleidos.analytics.pubsub.api.Pub;
import com.deleidos.analytics.websocket.api.WebSocketApiPlugin;
import com.deleidos.analytics.websocket.api.WebSocketEventListener;
import com.deleidos.analytics.websocket.api.WebSocketMessageFactory;

public class PubSubWebSocketApiPlugin implements WebSocketApiPlugin {

	// WebSocketMessageFactories
	PubSubMessageFactory pubSubMessageFactory = new PubSubMessageFactory();
	
	// WebSocketEventListeners
	PubSubWebSocketEventListener pubSubWebSocketEventListener = new PubSubWebSocketEventListener();
	
	@Override
	public List<WebSocketEventListener> getWebSocketEventListeners() {
		return Arrays.asList(pubSubWebSocketEventListener);
	}

	@Override
	public List<WebSocketMessageFactory> getWebSocketMessageFactories() {
		return Arrays.asList(pubSubMessageFactory);
	}

	@Override
	public List<String> getResourcePackages() {
		return Arrays.asList(Pub.class.getPackage().getName());
	}

}
