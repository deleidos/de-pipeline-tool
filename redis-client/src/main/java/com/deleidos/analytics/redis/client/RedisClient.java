package com.deleidos.analytics.redis.client;

import redis.clients.jedis.Jedis;

/**
 * Simple Redis client implementation with namespaces. Encapsulates underlying client library.
 * 
 * @author vernona
 */
public class RedisClient {
	Jedis jedis = null;

	private static final String namespaceDelimiter = ":";

	/**
	 * Constructor. Uses the default port (6379).
	 * 
	 * @param host
	 */
	public RedisClient(String host) {
		jedis = new Jedis(host);
	}

	/**
	 * Get a value from the cache.
	 * 
	 * @param key
	 * @return
	 */
	public String getValue(String namespace, String key) {
		return jedis.get(buildKey(namespace, key));
	}

	/**
	 * Set a value in the cache.
	 * 
	 * @param key
	 * @param value
	 */
	public void setValue(String namespace, String key, String value) {
		jedis.set(buildKey(namespace, key), value);
	}

	/**
	 * Delete a value from the cache.
	 * 
	 * @param namespace
	 * @param key
	 */
	public void delete(String namespace, String key) {
		jedis.del(buildKey(namespace, key));
	}

	/**
	 * Close the redis connection.
	 */
	public void close() {
		jedis.close();
	}

	//
	// Private methods:
	//

	private String buildKey(String namespace, String key) {
		return namespace == null ? key : namespace + namespaceDelimiter + key;
	}
}
