package com.deleidos.analytics.pubsub.api;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;

/**
 * The SessionId associated with a given common controller connection.
 */
public class SessionId extends BaseWebSocketMessage {

	public String sessionId;

	public SessionId() {
	}
	
	public SessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public void processMessage() {
		//noop
	}
}
