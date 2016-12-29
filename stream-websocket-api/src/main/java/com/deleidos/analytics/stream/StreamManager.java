package com.deleidos.analytics.stream;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.analytics.websocket.event.ServiceEventBus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Helper class for the WebSocket Server used to track stream and consume (ws-stream) connections.
 */
public class StreamManager {

	private static final Logger logger = Logger.getLogger(StreamManager.class);

	private StreamManager() {};

	private static class SingletonHolder {
		private static final StreamManager INSTANCE = new StreamManager();
	}

	// topic|Consumer List (webSocketId)
	private final Map<String, CopyOnWriteArrayList<String>> consumerMap = new ConcurrentHashMap<String, CopyOnWriteArrayList<String>>(
			16, 0.9f, 1);

	// producer(webSocketId)|topic
	private final Map<String, String> producerMap = new ConcurrentHashMap<String, String>(16, 0.9f, 1);

	// producer(webSocketId)|BlockingQueue<Messages>
	// private final Map<String, LinkedBlockingQueue<String>> producerMessageBufferMap =
	// new ConcurrentHashMap<String, LinkedBlockingQueue<String>>(16, 0.9f, 1);

	// producer(webSocketId)|StreamFilter
	private final Map<String, StreamFilter> streamFilterMap = new ConcurrentHashMap<String, StreamFilter>(16, 0.9f, 1);

	public static StreamManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public void addProducer(String webSocketId, String topic) {
		if (consumerMap.containsKey(topic)) {
			logger.warn("Topic: " + topic + " already exists, dropping stream request");
			return;
		}
		if (!producerMap.containsKey(webSocketId)) {
			producerMap.put(webSocketId, topic);
			logger.info("Topic: " + topic + ", added streamer: " + webSocketId);
		}
		if (!consumerMap.containsKey(topic)) {
			consumerMap.put(topic, new CopyOnWriteArrayList<String>());
		}
	}

	public void addConsumer(String webSocketId, String topic) {
		if (consumerMap.containsKey(topic)) {
			CopyOnWriteArrayList<String> consumerList = consumerMap.get(topic);
			if (!consumerList.contains(webSocketId)) {
				consumerList.add(webSocketId);
				ServiceEventBus.getInstance().newConsumer(webSocketId, topic);
				logger.info("Topic: " + topic + ", added consumer: " + webSocketId);
			}
		}
		else {
			logger.warn("Consumer request to non-existing topic: " + topic);
		}
	}

	public void removeProducerConsumer(String webSocketId) {
		if (producerMap.containsKey(webSocketId)) {
			String topic = producerMap.remove(webSocketId);
			logger.info("Topic: " + topic + ", removed streamer: " + webSocketId);
			consumerMap.remove(topic);
		}
		else {
			for (Map.Entry<String, CopyOnWriteArrayList<String>> entry : consumerMap.entrySet()) {
				entry.getValue().remove(webSocketId);
				logger.info("Topic: " + entry.getKey() + ", removed consumer: " + webSocketId);
			}
		}
		// if (producerMessageBufferMap.containsKey(webSocketId)) {
		// producerMessageBufferMap.remove(webSocketId);
		// }
	}

	public boolean isStreamer(String webSocketId) {
		return producerMap.containsKey(webSocketId);
	}

	public List<String> getConsumersIds(String webSocketId) {
		List<String> consumerIds = null;
		String topic = producerMap.get(webSocketId);
		if (topic != null) {
			consumerIds = consumerMap.get(topic);
		}
		return consumerIds;
	}

	public String addTopicToMessage(String message, String webSocketId) throws Exception {
		if (message != null) {
			String topic = producerMap.get(webSocketId);
			JsonNode rootNode = JsonUtil.parseJson(message);
			if (rootNode != null && rootNode instanceof ObjectNode) {
				((ObjectNode) rootNode).put("topic", topic);
				return JsonUtil.toJsonString(rootNode);
			}
			else {
				return message;
			}
		}
		else {
			return "";
		}
	}

	public Set<String> getTopics() {
		return new HashSet<String>(producerMap.values());
	}

	public void addStreamFilter(StreamFilter filter) {
		if (filter != null) {
			// TODO handle multiple filters for 1 topic
			streamFilterMap.put(filter.getTopic(), filter);
		}
	}

	public void removeStreamFilter(StreamFilter filter) {
		if (filter != null) {
			streamFilterMap.remove(filter.getTopic());
		}
	}

	public String filterMessage(String message, String webSocketId) {
		String topic = producerMap.get(webSocketId);
		if (topic != null) {
			StreamFilter filter = streamFilterMap.get(topic);
			if (filter != null) {
				message = filter.filterMessage(message);
				if (message == null && logger.isDebugEnabled()) {
					logger.debug("Filtered message: " + message + " for topic: " + topic);
				}
				return message;
			}
		}
		return message;
	}

	// public boolean bufferMessage(String message, String webSocketId) {
	// LinkedBlockingQueue<String> buffer = producerMessageBufferMap.get(webSocketId);
	// if (buffer != null) {
	// buffer.offer(message);
	// return true;
	// }
	// return false;
	// }
	//
	// private class BufferDrainer extends TimerTask {
	// @Override
	// public void run() {
	// for (Map.Entry<String, LinkedBlockingQueue<String>> entry: producerMessageBufferMap.entrySet()) {
	// List<String> consumerIds = getConsumersIds(entry.getKey());
	// if (consumerIds != null) {
	// String message = entry.getValue().poll();
	// if (message != null) {
	// WebSocketServer.getInstance().getEndpointManager().
	// send(message, consumerIds);
	// }
	// }
	// }
	// }
	// }

}
