package com.deleidos.framework.service.api.monitor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.monitoring.MonitoringUtil;

/**
 * Get a breakdown of how an app's operators are using the CPU
 * 
 * @author mollotb
 */
public class GetAppCpuUsage extends BaseWebSocketMessage {

	private String request, id;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	@Override
	@Path("/getAppCpuUsage")
	@GET
	public void processMessage() throws Exception {
		try {
			if (id != null && !id.equals("")) {
				sendResponse(MonitoringUtil.getAppCpuUsage(id));
			}
		}
		catch (Throwable e) {
			logger.debug(e.getMessage(), e);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
