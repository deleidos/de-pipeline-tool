package com.deleidos.framework.service.api.manager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.service.data.SystemDataManager;
import com.deleidos.framework.model.system.SystemDescriptor;

/**
 * Delete a system from the database (does not currently delete from cluster).
 * 
 * @author vernona
 */
public class DeleteSystem extends BaseWebSocketMessage {

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(DeleteSystem.class);

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
	@Path("/deleteSystem")
	@GET
	public void processMessage() throws Exception {
		SystemDataManager.getInstance().deleteSystemDescriptor(id);
		// TODO FUTURE delete system in apex
		sendResponse(id); // TODO return value
	}
}
