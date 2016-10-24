package com.deleidos.framework.service.api.monitor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.monitoring.MonitoringUtil;

/**
 * Get a list of apps on the system as an array of strings
 * 
 * @author mollotb
 */
public class GetAppList extends BaseWebSocketMessage {

	private String request;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	@Override
	@Path("/getAppList")
	@GET
	public void processMessage() throws Exception {
		try {
			sendResponse(MonitoringUtil.getAppList());
		}
		catch (Throwable e) {
			logger.debug(e.getMessage(), e);
		}
	}
}
