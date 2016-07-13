package com.deleidos.analytics.redis.client;

import redis.clients.jedis.Jedis;

/**
 * Simple Redis client implemenation. Encapsulates underlying client library.
 * 
 * @author vernona
 */
public class RedisClient {
	Jedis jedis = null;

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
	public String getValue(String key) {
		return jedis.get(key);
	}

	/**
	 * Set a value in the cache.
	 * 
	 * @param key
	 * @param value
	 */
	public void setValue(String key, String value) {
		jedis.set(key, value);
	}

	public void delete(String key) {
		jedis.del(key);
	}
	
	/**
	 * Close the redis connection.
f	 */
	public void close() {
		jedis.close();
	}
}
