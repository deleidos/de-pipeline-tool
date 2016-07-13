package com.deleidos.analytics.pubsub.api;

import java.util.Arrays;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.datetime.TimeWindow;
import com.deleidos.analytics.pubsub.PubSubManager;
import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;

/**
 * Part of the pub-sub endpoint, this message allows the publisher/subscriber to define
 * epoch (TimeWindow) that data should be published to all subscribers.
 */
public class Epoch extends BaseWebSocketMessage {

	private static final Logger logger = Logger.getLogger(Epoch.class);
	
	public String epoch;
	
	public Epoch() {
	}

	public Epoch(String epoch) {
		this.epoch = epoch;
	}

	public String getEpoch() {
		return epoch;
	}
	public void setEpoch(String epoch) {
		this.epoch = epoch;
	}

	@Override
	@Path("/epoch")
	@GET
	public void processMessage() {
		TimeWindow window = null;
		try {
			window = TimeWindow.valueOf(epoch.toUpperCase());
		} catch (IllegalArgumentException e) {
			logger.error("Epoch value: " + epoch + " is not one of the valid values: " + 
					Arrays.asList(TimeWindow.values()));
		}
		if (window != null) {
			logger.info("Setting epoch to: " + epoch);
			PubSubManager.getInstance().setPublisherEpoch(webSocketId, window);
		}
	}
}
