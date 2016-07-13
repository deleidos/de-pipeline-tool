package com.deleidos.framework.service.api.builder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.service.data.SystemDataManager;

/**
 * Get a system descriptors by ID.
 * 
 * @author vernona
 */
public class GetSystemDescriptor extends BaseWebSocketMessage {

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(GetSystemDescriptor.class);

	private String request;
	private String id;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	@Path("/getSystemDescriptor")
	@GET
	public void processMessage() throws Exception {
		sendResponse(SystemDataManager.getInstance().getSystemDecriptor(id));
	}
}
