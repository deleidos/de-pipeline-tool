package com.deleidos.framework.service.api.builder;

import java.io.File;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.service.data.SystemDataManager;

public class DeleteOperatorMetadata extends BaseWebSocketMessage {

	private String request;
	private String id;
	private Logger logger = Logger.getLogger(DeleteOperatorMetadata.class);

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
	@Path("/deleteOperatorMetadata")
	@POST
	public void processMessage() throws Exception {
		try {
			String fileName = SystemDataManager.getInstance().getOperatorMetadata(id).getClassName() + ".jar";
			SystemDataManager.getInstance().deleteOperatorMetadata(id);
			String filePath = "/opt/apex-deployment/operators/";
			File jar = new File(filePath + fileName);
			jar.delete();
		}
		catch (Exception e) {
			logger.error("Exception in SaveOperatorMetadata: " + e);
		}

		sendResponse("Deleted " + id);

	}
}