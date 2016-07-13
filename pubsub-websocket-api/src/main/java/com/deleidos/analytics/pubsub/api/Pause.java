package com.deleidos.analytics.pubsub.api;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;

/**
 * Part of the pub-sub endpoint, this message tells the publisher (controller)
 * to pause the sending of TimeStamp messages
 */
public class Pause extends BaseWebSocketMessage {
	
	public String pause;
	
	public Pause() {
	}
	
	public Pause(String pause) {
		this.pause = pause;
	}
	
	public String getPause() {
		return pause;
	}

	public void setPause(String pause) {
		this.pause = pause;
	}

	@Override
	public void processMessage() {
		// noop
	}
}
