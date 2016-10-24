package com.deleidos.framework.service.api.monitor;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.monitoring.LogUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author mollotb
 */
public class GetLog extends BaseWebSocketMessage {

	private String request, id, log;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	@Override
	@Path("/getLog")
	@GET
	public void processMessage() throws Exception {
		try {
			if (id != null && !id.equals("")) {
				sendResponse(LogUtil.getLog(id, log));
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

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}
}
