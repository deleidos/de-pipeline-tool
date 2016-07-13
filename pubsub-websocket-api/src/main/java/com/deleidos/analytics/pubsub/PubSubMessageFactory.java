package com.deleidos.analytics.pubsub;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.analytics.pubsub.api.Content;
import com.deleidos.analytics.pubsub.api.Epoch;
import com.deleidos.analytics.pubsub.api.GetPubSubTopics;
import com.deleidos.analytics.pubsub.api.Pub;
import com.deleidos.analytics.pubsub.api.Sub;
import com.deleidos.analytics.pubsub.api.TimeStamp;
import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.analytics.websocket.api.WebSocketMessage;
import com.deleidos.analytics.websocket.api.WebSocketMessageFactory;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * A factory class for building concrete implementations of BaseMessage from JSON strings. 
 */
public class PubSubMessageFactory implements WebSocketMessageFactory {

	private static final Logger logger = Logger.getLogger(PubSubMessageFactory.class);

	@SuppressWarnings("rawtypes")
	private static final HashMap<String, Class> classLookup = new HashMap<String, Class>();
	@SuppressWarnings("rawtypes")
	private static final HashMap<String, Class> requestLookup = new HashMap<String, Class>();

	static {
		classLookup.put("pub", Pub.class);
		classLookup.put("sub", Sub.class);
		classLookup.put("time_stamp", TimeStamp.class);
		classLookup.put("content", Content.class);
		classLookup.put("epoch", Epoch.class);

		requestLookup.put("getPubSubTopics", GetPubSubTopics.class);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public WebSocketMessage buildMessage(String message, String webSocketId) {
		try {
			JsonNode rootNode = JsonUtil.parseJson(message);

			JsonNode node = rootNode.get("request");
			if (node != null) {
				Class clazz = requestLookup.get(node.textValue());
				if (clazz != null) {
					return (BaseWebSocketMessage) JsonUtil.fromJsonString(message, clazz);
				}
			}
			else {
				for (Map.Entry<String, Class> entry : classLookup.entrySet()) {
					node = rootNode.get(entry.getKey());
					if (node != null) {
						return (BaseWebSocketMessage) JsonUtil.fromJsonString(message, entry.getValue());
					}
				}
			}
		}
		catch (Exception e) {
			logger.error("Message: " + message + " failed to parse with error: " + e.toString());
		}
		return null;
	}

}
