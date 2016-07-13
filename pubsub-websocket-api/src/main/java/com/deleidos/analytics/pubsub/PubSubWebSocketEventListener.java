package com.deleidos.analytics.pubsub;

import com.deleidos.analytics.websocket.api.WebSocketEventListener;

public class PubSubWebSocketEventListener implements WebSocketEventListener{

	@Override
	public void onWebSocketClose(String webSocketId) {
		PubSubManager.getInstance().removePublisherSubscriber(webSocketId);
		
	}

	@Override
	public void onWebSocketConnect(String webSocketId) {
		// noop		
	}


}
