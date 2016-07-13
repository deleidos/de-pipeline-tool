package com.deleidos.analytics.stream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.analytics.stream.api.Consume;
import com.deleidos.analytics.stream.api.GetStreamTopics;
import com.deleidos.analytics.stream.api.Stream;
import com.deleidos.analytics.websocket.WebSocketServer;
import com.deleidos.analytics.websocket.api.WebSocketMessage;
import com.deleidos.analytics.websocket.api.WebSocketMessageFactory;
import com.fasterxml.jackson.databind.JsonNode;

public class StreamMessageFactory implements WebSocketMessageFactory {

	private static final Logger logger = Logger.getLogger(StreamMessageFactory.class);

	@SuppressWarnings("rawtypes")
	private static final HashMap<String, Class> classLookup = new HashMap<String, Class>();
	@SuppressWarnings("rawtypes")
	private static final HashMap<String, Class> requestLookup = new HashMap<String, Class>();
	
	private StreamManager streamManager = StreamManager.getInstance();
	private WebSocketServer webSocketServer = WebSocketServer.getInstance();

	static {
		classLookup.put("stream", Stream.class);
		classLookup.put("consume", Consume.class);
		requestLookup.put("getStreamTopics", GetStreamTopics.class);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public WebSocketMessage buildMessage(String message, String webSocketId) {
		try {
			if (streamManager.isStreamer(webSocketId)) {
				List<String> consumerIds = streamManager.getConsumersIds(webSocketId);
				if (consumerIds != null) {
					message = streamManager.filterMessage(message, webSocketId);
					if (message != null) {
						String messageWithTopic = streamManager.addTopicToMessage(message, webSocketId);
						webSocketServer.send(messageWithTopic, consumerIds);
					}
				}
				return null;
			} else {
				JsonNode rootNode = JsonUtil.parseJson(message);

				JsonNode node = rootNode.get("request");
				if (node != null) {
					Class clazz = requestLookup.get(node.textValue());
					if (clazz != null) {
						return (WebSocketMessage) JsonUtil.fromJsonString(message, clazz);
					}
				}
				else {
					for (Map.Entry<String, Class> entry : classLookup.entrySet()) {
						node = rootNode.get(entry.getKey());
						if (node != null) {
							return (WebSocketMessage) JsonUtil.fromJsonString(message, entry.getValue());
						}
					}
				}
			}
		}
		catch (Exception e) {
			logger.error("Message: " + message + " failed to parse with error: " + e.toString(), e);
		}
		return null;
	}
}
