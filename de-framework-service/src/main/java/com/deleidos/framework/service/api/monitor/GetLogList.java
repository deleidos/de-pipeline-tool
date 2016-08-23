package com.deleidos.framework.service.api.monitor;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.monitoring.LogUtil;
import com.deleidos.framework.monitoring.MonitoringUtil;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author mollotb
 */
public class GetLogList extends BaseWebSocketMessage {

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(GetLogList.class);

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
		if (id != null && !id.equals("")) sendResponse(LogUtil.listLogs(id).toArray());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
