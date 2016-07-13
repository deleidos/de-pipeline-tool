package com.deleidos.framework.service.api.builder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.service.tools.OperatorMetadataFactory;

/**
 * Get operator metadata.
 * 
 * @author vernona
 */
public class GetOperatorMetadata extends BaseWebSocketMessage {

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(GetOperatorMetadata.class);

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
		sendResponse(OperatorMetadataFactory.getInstance().getOperatorMetadata());
		// TODO query from mongodb
	}
}
