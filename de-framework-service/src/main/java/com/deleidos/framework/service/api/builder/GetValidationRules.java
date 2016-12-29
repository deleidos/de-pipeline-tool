package com.deleidos.framework.service.api.builder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.service.data.SystemDataManager;

/**
 * Get operator metadata property validation rules.
 * 
 * @author vernona
 */
public class GetValidationRules extends BaseWebSocketMessage {

	private String request;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	@Override
	@Path("/getValidationRules")
	@GET
	public void processMessage() throws Exception {
		sendResponse(SystemDataManager.getInstance().getValidationRules());
	}
}
