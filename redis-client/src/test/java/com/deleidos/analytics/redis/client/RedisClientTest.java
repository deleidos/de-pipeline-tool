package com.deleidos.analytics.redis.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.deleidos.analytics.config.AnalyticsConfig;

/**
 * Simple redis client unit test.
 * 
 * @author vernona
 */
public class RedisClientTest {

	/**
	 * Test set, get and delete.
	 */
	@Test
	public void test() {
		RedisClient client = new RedisClient(AnalyticsConfig.getInstance().getRedisHostname());
		String key = "foo";
		String value = "bar";
		client.setValue(key, value);
		String value2 = client.getValue(key);
		System.out.println(value2);
		assertEquals(value, value2);
		client.delete(key);
		value2 = client.getValue(key);
		assertNull(value2);
		client.close();
	}
}
