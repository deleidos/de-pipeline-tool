package com.deleidos.analytics.stream;

import com.deleidos.analytics.websocket.api.WebSocketEventListener;

public class StreamWebSocketEventListener implements WebSocketEventListener{

	@Override
	public void onWebSocketClose(String webSocketId) {
		StreamManager.getInstance().removeProducerConsumer(webSocketId);
		
	}

	@Override
	public void onWebSocketConnect(String webSocketId) {
		// noop		
	}

}
