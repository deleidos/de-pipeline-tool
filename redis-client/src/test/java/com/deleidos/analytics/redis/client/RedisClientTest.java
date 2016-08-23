package com.deleidos.analytics.redis.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Simple redis client unit test.
 * 
 * @author vernona
 */
public class RedisClientTest extends RedisClientTestBase {

	private static final String namespace = "RedisClientTest";
	private static final String key = "foo";
	private static final String value = "bar";

	/**
	 * Test set, get and delete.
	 */
	@Test
	public void test() {
		client.setValue(namespace, key, value);
		String value2 = client.getValue(namespace, key);
		System.out.println(value2);
		assertEquals(value, value2);
		client.delete(namespace, key);
		value2 = client.getValue(namespace, key);
		assertNull(value2);
	}
}
