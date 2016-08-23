package com.deleidos.analytics.redis.client;

import org.junit.After;
import org.junit.Before;

import com.deleidos.analytics.config.AnalyticsConfig;

/**
 * Redis client test base class responsible for initializing and closing resources.
 * 
 * @author vernona
 */
public abstract class RedisClientTestBase {

	protected RedisClient client = null;

	@Before
	public void doBefore() {
		client = new RedisClient(AnalyticsConfig.getInstance().getRedisHostname());
	}

	@After
	public void doAfter() {
		if (client != null) {
			client.close();
		}
	}
}
