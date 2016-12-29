package com.deleidos.framework.service.api.builder;

import java.util.UUID;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.model.system.ValidationRule;
import com.deleidos.framework.service.data.SystemDataManager;

/**
 * Save a validation rule in the database.
 * 
 * @author vernona
 */
public class SaveValidationRule extends BaseWebSocketMessage {

	private String request;
	private ValidationRule validationRule;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public ValidationRule getValidationRule() {
		return validationRule;
	}

	public void setValidationRule(ValidationRule validationRule) {
		this.validationRule = validationRule;
	}

	@Override
	@Path("/saveValidationRule")
	@POST
	public void processMessage() throws Exception {
		if (validationRule.get_id() == null) {
			validationRule.set_id(UUID.randomUUID().toString());
			SystemDataManager.getInstance().insertValidationRule(validationRule);
		}
		else {
			SystemDataManager.getInstance().updateValidationRule(validationRule);
		}

		sendResponse(validationRule);
	}

}
