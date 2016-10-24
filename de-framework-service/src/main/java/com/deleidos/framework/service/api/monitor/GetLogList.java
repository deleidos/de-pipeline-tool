package com.deleidos.framework.service.api.monitor;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.monitoring.LogUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author mollotb
 */
public class GetLogList extends BaseWebSocketMessage {

	private String request, id;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	@Override
	@Path("/getLogList")
	@GET
	public void processMessage() throws Exception {
		try {
			if (id != null && !id.equals("")) {
				sendResponse(LogUtil.listLogs(id).toArray());
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
