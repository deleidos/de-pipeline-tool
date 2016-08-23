package com.deleidos.framework.service.api.monitor;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.monitoring.MonitoringUtil;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author mollotb
 */
public class SendStramEvent extends BaseWebSocketMessage {

	private String request, line;

	public SendStramEvent(String line) {
		this.line = line;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	@Override
	public void processMessage() throws Exception {
		sendResponse(line);
	}
}
