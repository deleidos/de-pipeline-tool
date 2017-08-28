package com.deleidos.framework.service;

import java.util.Arrays;
import java.util.List;

import com.deleidos.analytics.websocket.api.WebSocketApiPlugin;
import com.deleidos.analytics.websocket.api.WebSocketEventListener;
import com.deleidos.analytics.websocket.api.WebSocketMessageFactory;
import com.deleidos.framework.service.api.builder.GetOperatorMetadata;
import com.deleidos.framework.service.api.monitor.GetCpuUsage;

/**
 * Pipeline framework service data API plugin.
 * 
 * @author vernona
 */
public class FrameworkServiceDataApiPlugin implements WebSocketApiPlugin {

	FrameworkServiceMessageFactory frameworkServiceMessageFactory = new FrameworkServiceMessageFactory();

	@Override
	public List<WebSocketEventListener> getWebSocketEventListeners() {
		return null;
	}

	@Override
	public List<WebSocketMessageFactory> getWebSocketMessageFactories() {
		return Arrays.asList(frameworkServiceMessageFactory);
	}

	@Override
	public List<String> getResourcePackages() {
		return Arrays.asList(GetOperatorMetadata.class.getPackage().getName(),
				GetCpuUsage.class.getPackage().getName());
	}
}
