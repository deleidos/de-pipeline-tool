package com.deleidos.analytics.kafka.client;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.map.PassiveExpiringMap;

/**
 * Stream messages to a Kafka queue. Messages are cached for a configurable period of time during which duplicates are
 * discarded.
 * 
 * @author vernona
 */
public class CacheStreamer {
	private PassiveExpiringMap<String, String> cache;
	private MessageProducer producer;

	/**
	 * Constructor.
	 * 
	 * @param queueConfig
	 * @param expirationTimeSeconds
	 *            how long to cache a record
	 * @param topic
	 *            the name of the kafka topic to which to stream
	 */
	public CacheStreamer(QueueConfig queueConfig, int expirationTimeSeconds, String topic) {
		cache = new PassiveExpiringMap<>(expirationTimeSeconds, TimeUnit.SECONDS, new HashMap<String, String>());
		producer = new MessageProducer(queueConfig, topic);
	}

	/**
	 * Stream messages. If the key has not expired, the message will not be sent. If the key is not found/expired, the
	 * message will be sent and the key will be cached.
	 * 
	 * @param key
	 * @param message
	 */
	public void stream(String key, String message) throws Exception {
		if (!cache.containsKey(key)) {
			producer.produce(message);
			cache.put(key, null); // Don't actually need a value, just key tracking and expiration.
		}
	}
}
