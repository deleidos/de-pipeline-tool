package com.deleidos.framework.service.api.builder;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.service.data.SystemDataManager;

/**
 * Delete a validation rule.
 * 
 * @author vernona
 */
public class DeleteValidationRule extends BaseWebSocketMessage {

	private String request;
	private String id;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	@Override
	public void processMessage() throws Exception {
		SystemDataManager.getInstance().deleteValidationRule(id);
		sendResponse("Deleted " + id);
	}
}
