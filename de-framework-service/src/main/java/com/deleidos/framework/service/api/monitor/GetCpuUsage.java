package com.deleidos.framework.service.api.monitor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.monitoring.MonitoringUtil;
import com.deleidos.framework.service.config.ServiceConfig;

/**
 * Get Cpu usage of the system, (TODO) an application, or an operator.
 * 
 * @author mollotb
 */
public class GetCpuUsage extends BaseWebSocketMessage {

	private String request;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	@Override
	@Path("/getCpuUsage")
	@GET
	public void processMessage() throws Exception {
		try {
			MonitoringUtil util = new MonitoringUtil(ServiceConfig.getInstance().getHadoopNameNodeHostname());
			sendResponse(util.getCpuUsage());
		}
		catch (Throwable e) {
			logger.debug(e.getMessage(), e);
		}
	}
}
