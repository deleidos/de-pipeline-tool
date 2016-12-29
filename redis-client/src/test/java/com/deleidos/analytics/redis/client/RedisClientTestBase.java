package com.deleidos.analytics.redis.client;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;

/**
 * Redis client test base class responsible for initializing and closing resources.
 * 
 * @author vernona
 */
public abstract class RedisClientTestBase {

	protected RedisClient client = null;

	private static final String redisHostnameEnv = "REDIS_HOSTNAME";
	
	@Before
	public void doBefore() {
		String hostname = System.getenv(redisHostnameEnv);
		assertNotNull(hostname);
		System.out.println(redisHostnameEnv + "=" + hostname);
		client = new RedisClient(hostname);
	}

	@After
	public void doAfter() {
		if (client != null) {
			client.close();
		}
	}
}
