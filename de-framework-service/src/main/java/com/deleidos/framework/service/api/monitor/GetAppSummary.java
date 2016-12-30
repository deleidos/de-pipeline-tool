package com.deleidos.framework.service.api.monitor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.monitoring.MonitoringUtil;
import com.deleidos.framework.service.config.ServiceConfig;

/**
 * Get basic information about an app given its name
 * 
 * @author mollotb
 */
public class GetAppSummary extends BaseWebSocketMessage {

	private String request, name;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	@Override
	@Path("/getAppSummary")
	@GET
	public void processMessage() throws Exception {
		try {
			MonitoringUtil util = new MonitoringUtil(ServiceConfig.getInstance().getHadoopNameNodeHostname());
			sendResponse(util.getAppSummaryByName(name));
		}
		catch (Throwable e) {
			logger.debug(e.getMessage(), e);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
