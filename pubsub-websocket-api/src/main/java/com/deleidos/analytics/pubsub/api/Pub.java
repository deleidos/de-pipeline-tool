package com.deleidos.analytics.pubsub.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.datetime.TimeWindow;
import com.deleidos.analytics.pubsub.PubSubManager;
import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;

/**
 * Part of the pub-sub endpoint, this message allows the publisher to indicate its intention to be a publisher of data
 * on the specified topic (pub variable)
 */
public class Pub extends BaseWebSocketMessage {

	private static final Logger logger = Logger.getLogger(Pub.class);

	public String pub;

	public Pub() {}

	public Pub(String pub) {
		this.pub = pub;
	}

	public String getPub() {
		return pub;
	}

	public void setPub(String pub) {
		this.pub = pub;
	}

	@Override
	@Path("/pub")
	@GET
	public void processMessage() throws Exception {
		logger.info("pub message received:" + pub);
		PubSubManager.getInstance().addPublisher(webSocketId, pub);
		// setting default window for this topic - this is temporary until subscribers set their own epoch/window
		PubSubManager.getInstance().setPublisherEpoch(webSocketId, TimeWindow.DAY);
		webSocketServer.send(new SessionId(webSocketId), webSocketId);
	}
}
