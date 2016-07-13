package com.deleidos.framework.service;

import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.analytics.websocket.api.WebSocketMessage;
import com.deleidos.analytics.websocket.api.WebSocketMessageFactory;
import com.deleidos.framework.service.api.builder.GetOperatorMetadata;
import com.deleidos.framework.service.api.builder.GetSystemDescriptor;
import com.deleidos.framework.service.api.builder.GetSystemDescriptors;
import com.deleidos.framework.service.api.builder.SaveSystemDescriptor;
import com.deleidos.framework.service.api.manager.DeleteSystem;
import com.deleidos.framework.service.api.manager.DeploySystem;
import com.deleidos.framework.service.api.manager.KillSystem;
import com.deleidos.framework.service.api.manager.StopSystem;
import com.deleidos.framework.service.api.monitor.GetAppCpuUsage;
import com.deleidos.framework.service.api.monitor.GetAppDetails;
import com.deleidos.framework.service.api.monitor.GetAppList;
import com.deleidos.framework.service.api.monitor.GetAppSummary;
import com.deleidos.framework.service.api.monitor.GetCpuUsage;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * A factory class for building concrete implementations of BaseMessage from JSON strings.
 * 
 * @author vernona
 */
public class FrameworkServiceMessageFactory implements WebSocketMessageFactory {

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
		}
		return wsMessage;
	}
}
