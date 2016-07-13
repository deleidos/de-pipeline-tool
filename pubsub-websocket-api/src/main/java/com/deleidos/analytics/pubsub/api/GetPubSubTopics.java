package com.deleidos.analytics.pubsub.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.deleidos.analytics.pubsub.PubSubManager;
import com.deleidos.analytics.pubsub.PubSubSession;
import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;

/**
 * Part of the req-resp endpoint, this message retrieves the current list of PubSub Topics.
 */
public class GetPubSubTopics extends BaseWebSocketMessage {

	public String request;
	public String sessionId;
	
	public GetPubSubTopics() {
	}
	
	public GetPubSubTopics(String request, String sessionId) {
		this.request = request;
		this.sessionId = sessionId;
	}
	
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	@Override
    @Path("/getPubSubTopics")
    @GET
	public void processMessage() throws Exception {
		if (sessionId == null) {
			sessionId = PubSubManager.getInstance().getFirstSessionId();
		}
		List<String> topics = new ArrayList<String>();
		if (sessionId != null) {
			PubSubSession session = PubSubManager.getInstance().getPubSubSession(sessionId);
			if (session != null) {
				topics = session.getPublisher().getTopics();
			}
		}
		sendResponse(new TopicsWrapper(topics));
	}
}
