package com.deleidos.framework.service.api.builder;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.util.Base64Util;
import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;

/**
 * Decode a base64 string. 
 * 
 * @author vernona
 */
public class DecodeBase64 extends BaseWebSocketMessage {
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(DecodeBase64.class);

	private String request;
	private String base64;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getBase64() {
		return base64;
	}

	public void setBase64(String base64) {
		this.base64 = base64;
	}

	@Path("/decodeBase64")
	@POST
	@Override
	public void processMessage() throws Exception {
		sendResponse(Base64Util.decodeToString(base64));
	}
}
