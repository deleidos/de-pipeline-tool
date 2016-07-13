package com.deleidos.analytics.redis.client;

import org.junit.Test;

import com.deleidos.analytics.config.AnalyticsConfig;

public class RedisGetTest {

	/**
	 * Test set, get and delete.
	 */
	@Test
	public void test() {
		RedisClient client = new RedisClient(AnalyticsConfig.getInstance().getRedisHostname());
		String key = "N66848";
		System.out.println(client.getValue(key));
	}
}
