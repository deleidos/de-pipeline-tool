package com.deleidos.framework.service.api.monitor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.monitoring.MonitoringUtil;

/**
 * Get Cpu usage of the system, (TODO) an application, or an operator.
 * 
 * @author mollotb
 */
public class GetCpuUsage extends BaseWebSocketMessage {

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(GetCpuUsage.class);

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
		sendResponse(MonitoringUtil.getCpuUsage());
	}
}
