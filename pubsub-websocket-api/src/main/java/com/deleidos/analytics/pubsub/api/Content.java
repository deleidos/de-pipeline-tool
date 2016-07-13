package com.deleidos.analytics.pubsub.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.deleidos.analytics.pubsub.PubSubManager;
import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;

/**
 * Part of the pub-sub endpoint, this message allows the publisher to define
 * filtering criteria (content) for a topic
 */
public class Content extends BaseWebSocketMessage {

	private static final Logger logger = Logger.getLogger(Content.class);
	
	public String content;

	public Content() {
	}
	
	public Content(String content) {
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	@Path("/content")
	@GET
	public void processMessage() {
		logger.info("received content message:" + content);
		PubSubManager.getInstance().setPublisherContent(webSocketId, content);
	}
}
