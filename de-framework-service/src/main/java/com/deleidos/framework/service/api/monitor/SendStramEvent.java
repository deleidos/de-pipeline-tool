package com.deleidos.framework.service.api.monitor;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;

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
