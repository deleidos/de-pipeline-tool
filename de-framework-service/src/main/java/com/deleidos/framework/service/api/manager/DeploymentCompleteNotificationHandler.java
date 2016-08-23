package com.deleidos.framework.service.api.manager;

import java.util.List;

import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.analytics.stream.StreamManager;
import com.deleidos.analytics.websocket.WebSocketServer;
import com.deleidos.framework.model.event.DeploymentCompleteEvent;
import com.deleidos.framework.model.event.SystemEventBus;
import com.deleidos.framework.service.FrameworkServiceMessageFactory;
import com.google.common.eventbus.Subscribe;

/**
 * Handle deployment complete notification events and notify consumers.
 * 
 * @author vernona
 */
public class DeploymentCompleteNotificationHandler {

	/**
	 * Constructor. Register as a handler with the event bus.
	 */
	public DeploymentCompleteNotificationHandler() {
		SystemEventBus.getInstance().registerHandler(this);;
	}
	
	/**
	 * Handle deployment complete events.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@Subscribe
	public void handleDeploymentCompleteEvent(DeploymentCompleteEvent event) throws Exception {
		notifyDeploymentComplete(event.getId());
	}

	/**
	 * Notify that a deployment is complete.
	 * 
	 * @param id
	 */
	public void notifyDeploymentComplete(String id) throws Exception {
		if (id != null && !id.isEmpty()) {
			List<String> consumerIds = StreamManager.getInstance()
					.getConsumersIds(FrameworkServiceMessageFactory.deploymentCompleteStreamWebSocketId);
			if (consumerIds != null && !consumerIds.isEmpty()) {
				WebSocketServer.getInstance().send(JsonUtil.toJsonString(new Id(id)), consumerIds);
			}
		}
	}

	/**
	 * Simple ID class for JSON serialization into an object.
	 */
	public static class Id {
		private String id;

		public Id() {
		}

		public Id(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}
}
