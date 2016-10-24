package com.deleidos.framework.service.api.monitor;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Get a breakdown of how an app's operators are using the CPU
 * 
 * @author mollotb
 */
public class UnsubStramEvents extends BaseWebSocketMessage {

	private String request, id;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	@Override
	@Path("/getStramEvents")
	@GET
	public void processMessage() throws Exception {
		try {
			if (id != null && !id.equals("")) {
				StramEventStreamer.stopStream(id, this.getWebSocketId());
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
