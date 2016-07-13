package com.deleidos.analytics.pubsub.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.datetime.TimeWindow;
import com.deleidos.analytics.pubsub.PubSubManager;
import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Part of the pub-sub endpoint, this message allows a subscriber to request
 * a subscription to the specified topic (sub variable)
 */
public class Sub extends BaseWebSocketMessage {

	private static final Logger logger = Logger.getLogger(Sub.class);
	
	public String sub;
	//window will eventually be specified by each subscriber, for now it's specified by the controller
	public String window;
	public String sessionId;
	
	public Sub() {
	}
	
	public Sub(String sub, String window, String sessionId) {
		this.sub = sub;
		this.window = window;
		this.sessionId = sessionId;
	}

	public String getSub() {
		return sub;
	}
	public void setSub(String sub) {
		this.sub = sub;
	}
	@JsonIgnore
	public String getWindow() {
		return window;
	}
	@JsonIgnore
	public void setWindow(String window) {
		this.window = window;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	@Path("/sub")
	@GET
	public void processMessage() {
		logger.info("sub message received:" + sub);
		// TODO remove this defaulting of session_id once all subs are converted over
		if (sessionId == null) {
			logger.warn("No session_id passed in Sub message!");
			sessionId = PubSubManager.getInstance().getFirstSessionId();
			logger.warn("Defaulting to first session_id available: " + sessionId);
		}
		if (sessionId != null) {
			// setting window here is temporary until subs start sending their own window
			TimeWindow timeWindow = PubSubManager.getInstance().getPublisherEpoch(sessionId);
			PubSubManager.getInstance().addSubscriber(webSocketId, sub, timeWindow, sessionId);
		} else {
			logger.error("No session_id passed in Sub message, subscription request ignored");
		}
	}
}
