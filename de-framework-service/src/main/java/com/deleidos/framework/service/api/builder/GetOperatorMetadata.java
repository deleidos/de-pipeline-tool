package com.deleidos.framework.service.api.builder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.service.data.SystemDataManager;

/**
 * Get operator metadata.
 * 
 * @author vernona
 */
public class GetOperatorMetadata extends BaseWebSocketMessage {

	private String request;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	@Override
	@Path("/getOperatorMetadata")
	@GET
	public void processMessage() throws Exception {
		sendResponse(SystemDataManager.getInstance().getOperatorMetadata());
	}
}
