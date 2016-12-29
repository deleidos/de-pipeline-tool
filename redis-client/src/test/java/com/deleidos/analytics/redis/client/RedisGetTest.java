package com.deleidos.analytics.redis.client;

import org.junit.Test;

/**
 * Test getting a Redis value.
 * 
 * @author vernona
 */
public class RedisGetTest extends RedisClientTestBase {

	private static final String namespace = "sales_customer";
	private static final String key = "Customer#000098753"; // "N66848";
	
	/**
	 * Test set, get and delete.
	 */
	@Test
	public void test() {
		System.out.println(client.getValue(namespace, key));
	}
}
