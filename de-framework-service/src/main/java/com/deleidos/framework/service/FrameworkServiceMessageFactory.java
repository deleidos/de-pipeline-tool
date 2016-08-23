package com.deleidos.framework.service;

import java.util.UUID;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.analytics.stream.api.Stream;
import com.deleidos.analytics.websocket.WebSocketServer;
import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.analytics.websocket.api.WebSocketMessage;
import com.deleidos.analytics.websocket.api.WebSocketMessageFactory;
import com.deleidos.framework.service.api.builder.DecodeBase64;
import com.deleidos.framework.service.api.builder.GetOperatorMetadata;
import com.deleidos.framework.service.api.builder.GetSystemDescriptor;
import com.deleidos.framework.service.api.builder.GetSystemDescriptors;
import com.deleidos.framework.service.api.builder.SaveSystemDescriptor;
import com.deleidos.framework.service.api.manager.DeleteSystem;
import com.deleidos.framework.service.api.manager.DeploySystem;
import com.deleidos.framework.service.api.manager.DeploymentCompleteNotificationHandler;
import com.deleidos.framework.service.api.manager.GetRedisDimensionalEnrichmentNamespaces;
import com.deleidos.framework.service.api.manager.KillSystem;
import com.deleidos.framework.service.api.manager.StopSystem;
import com.deleidos.framework.service.api.monitor.*;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * A factory class for building concrete implementations of BaseMessage from JSON strings.
 * 
 * @author vernona
 */
public class FrameworkServiceMessageFactory implements WebSocketMessageFactory {

	private static final Logger logger = Logger.getLogger(FrameworkServiceMessageFactory.class);

	/** Create a handler for deployment complete notifications to be streamed to consumers. */
	@SuppressWarnings("unused")
	private DeploymentCompleteNotificationHandler deploymentCompleteNotificationHandler = new DeploymentCompleteNotificationHandler();

	/** Initialize a web socket stream for deployment complete notifications. */
	public static final String deploymentCompleteStreamWebSocketId = UUID.randomUUID().toString();
	public static final String deploymentCompleteStream = "deployment_complete_notification";

	static {
		try {
			logger.info("initializing stream managers");
			WebSocketServer.getInstance().processMessage(JsonUtil.toJsonString(new Stream(deploymentCompleteStream)),
					deploymentCompleteStreamWebSocketId);
		}
		catch (Throwable e) {
			logger.info("Error initializing stream managers", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public WebSocketMessage buildMessage(String message, String webSocketId) throws Exception {
		JsonNode rootNode = JsonUtil.parseJson(message);

		JsonNode node = rootNode.get("request");
		WebSocketMessage wsMessage = null;
		if (node != null) {
			if ("getOperatorMetadata".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetOperatorMetadata.class);
			}
			else if ("getSystemDescriptors".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetSystemDescriptors.class);
			}
			else if ("getSystemDescriptor".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetSystemDescriptor.class);
			}
			else if ("saveSystemDescriptor".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, SaveSystemDescriptor.class);
			}
			else if ("deploySystem".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, DeploySystem.class);
			}
			else if ("stopSystem".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, StopSystem.class);
			}
			else if ("deleteSystem".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, DeleteSystem.class);
			}
			else if ("killSystem".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, KillSystem.class);
			}
			else if ("getCpuUsage".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetCpuUsage.class);
			}
			else if ("getAppSummary".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetAppSummary.class);
			}
			else if ("getAppList".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetAppList.class);
			}
			else if ("getAppDetails".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetAppDetails.class);
			}
			else if ("getAppCpuUsage".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetAppCpuUsage.class);
			}
			else if ("getLogList".equals(node.textValue())) {
				logger.info("getLogList recieved");
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetLogList.class);
			}
			else if ("getLog".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetLog.class);
			}
			else if ("subStramEvents".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, SubStramEvents.class);
				wsMessage.setWebSocketId(webSocketId);
			}
			else if ("unsubStramEvents".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, UnsubStramEvents.class);
				wsMessage.setWebSocketId(webSocketId);
			}
			else if ("getRedisDimensionalEnrichmentNamespaces".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetRedisDimensionalEnrichmentNamespaces.class);
				wsMessage.setWebSocketId(webSocketId);
			}
			else if ("getRedisDimensionalEnrichmentNamespaces".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetRedisDimensionalEnrichmentNamespaces.class);
				wsMessage.setWebSocketId(webSocketId);
			}
			else if ("decodeBase64".equals(node.textValue())) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, DecodeBase64.class);
				wsMessage.setWebSocketId(webSocketId);
			}
		}
		return wsMessage;
	}
}
