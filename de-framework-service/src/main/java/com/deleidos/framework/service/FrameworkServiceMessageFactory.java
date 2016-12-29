package com.deleidos.framework.service;

import java.util.UUID;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.analytics.common.util.MemoryMonitor;
import com.deleidos.analytics.stream.api.Stream;
import com.deleidos.analytics.websocket.WebSocketServer;
import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.analytics.websocket.api.WebSocketMessage;
import com.deleidos.analytics.websocket.api.WebSocketMessageFactory;
import com.deleidos.framework.service.api.builder.DecodeBase64;
import com.deleidos.framework.service.api.builder.DeleteOperatorMetadata;
import com.deleidos.framework.service.api.builder.DeleteValidationRule;
import com.deleidos.framework.service.api.builder.GetOperatorMetadata;
import com.deleidos.framework.service.api.builder.GetSystemDescriptor;
import com.deleidos.framework.service.api.builder.GetSystemDescriptors;
import com.deleidos.framework.service.api.builder.GetValidationRules;
import com.deleidos.framework.service.api.builder.SaveOperatorMetadata;
import com.deleidos.framework.service.api.builder.SaveSystemDescriptor;
import com.deleidos.framework.service.api.builder.SaveValidationRule;
import com.deleidos.framework.service.api.logging.LogMessageStreamer;
import com.deleidos.framework.service.api.manager.DeleteSystem;
import com.deleidos.framework.service.api.manager.DeploySystem;
import com.deleidos.framework.service.api.manager.DeploymentCompleteNotificationHandler;
import com.deleidos.framework.service.api.manager.GetRedisDimensionalEnrichmentNamespaces;
import com.deleidos.framework.service.api.manager.KillSystem;
import com.deleidos.framework.service.api.manager.StopSystem;
import com.deleidos.framework.service.api.monitor.*;

/**
 * A factory class for building concrete implementations of BaseMessage from JSON strings.
 * 
 * @author vernona
 */
public class FrameworkServiceMessageFactory implements WebSocketMessageFactory {

	private static final Logger log = Logger.getLogger(FrameworkServiceMessageFactory.class);

	/**
	 * Create a handler for deployment complete notifications to be streamed to consumers.
	 */
	@SuppressWarnings("unused")
	private DeploymentCompleteNotificationHandler deploymentCompleteNotificationHandler = new DeploymentCompleteNotificationHandler();

	/** Initialize a web socket stream for deployment complete notifications. */
	public static final String deploymentCompleteStreamWebSocketId = UUID.randomUUID().toString();
	public static final String deploymentCompleteStream = "deployment_complete_notification";

	/** Initialize a web socket stream for log messages. */
	public static final String logMessageStreamWebSocketId = UUID.randomUUID().toString();
	public static final String logMessageStream = "log_message";

	static {
		try {
			log.info("initializing stream managers");
			WebSocketServer.getInstance().processMessage(JsonUtil.toJsonString(new Stream(deploymentCompleteStream)),
					deploymentCompleteStreamWebSocketId);
			WebSocketServer.getInstance().processMessage(JsonUtil.toJsonString(new Stream(logMessageStream)),
					logMessageStreamWebSocketId);

			// Start the UDP log message listener.
			LogMessageStreamer.getInstance().init(1514);

			// Turn on memory monitoring.
			MemoryMonitor.getInstance().start();
		}
		catch (Throwable e) {
			log.info("Error initializing stream managers", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public WebSocketMessage buildMessage(String message, String webSocketId) throws Exception {
		Request requestMessage = JsonUtil.fromJsonString(message, Request.class);
		String request = requestMessage.getRequest();
		WebSocketMessage wsMessage = null;
		if (request != null) {
			log.info("handling request: " + request);
			if ("getOperatorMetadata".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetOperatorMetadata.class);
			}
			else if ("saveOperatorMetadata".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, SaveOperatorMetadata.class);
			}
			else if ("deleteOperatorMetadata".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, DeleteOperatorMetadata.class);
			}
			else if ("getSystemDescriptors".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetSystemDescriptors.class);
			}
			else if ("getSystemDescriptor".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetSystemDescriptor.class);
			}
			else if ("saveSystemDescriptor".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, SaveSystemDescriptor.class);
			}
			else if ("deploySystem".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, DeploySystem.class);
			}
			else if ("stopSystem".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, StopSystem.class);
			}
			else if ("deleteSystem".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, DeleteSystem.class);
			}
			else if ("killSystem".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, KillSystem.class);
			}
			else if ("getCpuUsage".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetCpuUsage.class);
			}
			else if ("getAppSummary".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetAppSummary.class);
			}
			else if ("getAppList".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetAppList.class);
			}
			else if ("getAppDetails".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetAppDetails.class);
			}
			else if ("getAppCpuUsage".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetAppCpuUsage.class);
			}
			else if ("getLogList".equals(request)) {
				log.info("getLogList recieved");
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetLogList.class);
			}
			else if ("getLog".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetLog.class);
			}
			else if ("subStramEvents".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, SubStramEvents.class);
				wsMessage.setWebSocketId(webSocketId);
			}
			else if ("unsubStramEvents".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, UnsubStramEvents.class);
				wsMessage.setWebSocketId(webSocketId);
			}
			else if ("getRedisDimensionalEnrichmentNamespaces".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message,
						GetRedisDimensionalEnrichmentNamespaces.class);
				wsMessage.setWebSocketId(webSocketId);
			}
			else if ("getRedisDimensionalEnrichmentNamespaces".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message,
						GetRedisDimensionalEnrichmentNamespaces.class);
				wsMessage.setWebSocketId(webSocketId);
			}
			else if ("decodeBase64".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, DecodeBase64.class);
				wsMessage.setWebSocketId(webSocketId);
			}
			else if ("getValidationRules".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, GetValidationRules.class);
				wsMessage.setWebSocketId(webSocketId);
			}
			else if ("saveValidationRule".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, SaveValidationRule.class);
				wsMessage.setWebSocketId(webSocketId);
			}
			else if ("deleteValidationRule".equals(request)) {
				wsMessage = (BaseWebSocketMessage) JsonUtil.fromJsonString(message, DeleteValidationRule.class);
				wsMessage.setWebSocketId(webSocketId);
			}
		}
		return wsMessage;
	}
}
