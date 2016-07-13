package com.deleidos.analytics.stream;

import java.util.Arrays;
import java.util.List;

import com.deleidos.analytics.common.util.MetricsUtil;
import com.deleidos.analytics.stream.api.Stream;
import com.deleidos.analytics.websocket.api.WebSocketApiPlugin;
import com.deleidos.analytics.websocket.api.WebSocketEventListener;
import com.deleidos.analytics.websocket.api.WebSocketMessageFactory;

public class StreamWebSocketApiPlugin implements WebSocketApiPlugin {

	static {
		MetricsUtil.HEALTH_CHECK_REGISTRY.register("StreamWebSocketApi", new StreamApiHealthCheck());
	}
	
	// WebSocketMessageFactories
	private StreamMessageFactory streamMessageFactory = new StreamMessageFactory();
	
	// WebSocketEventListeners
	private StreamWebSocketEventListener streamWebSocketEventListener = new StreamWebSocketEventListener();
	
	@Override
	public List<WebSocketEventListener> getWebSocketEventListeners() {
		return Arrays.asList(streamWebSocketEventListener);
	}

	@Override
	public List<WebSocketMessageFactory> getWebSocketMessageFactories() {
		return Arrays.asList(streamMessageFactory);
	}

	@Override
	public List<String> getResourcePackages() {
		return Arrays.asList(Stream.class.getPackage().getName());
	}
}
