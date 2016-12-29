package com.deleidos.framework.service.config;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.deleidos.analytics.common.logging.LogUtil;

/**
 * Test the service config getting environment variable values.
 * 
 * @author vernona
 */
public class ServiceConfigTest {

	@Before
	public void setup() {
		LogUtil.initializeLog4jConsoleAppender();
	}

	@Test
	public void testServiceConfigEnvVariables() {
		System.out.println(ServiceConfig.getInstance().getMongodbHostname());
		System.out.println(ServiceConfig.getInstance().getManagerServiceHostname());
		System.out.println(ServiceConfig.getInstance().getHadoopNameNodeHostname());

		assertNotNull(ServiceConfig.getInstance().getMongodbHostname());
		assertNotNull(ServiceConfig.getInstance().getManagerServiceHostname());
		assertNotNull(ServiceConfig.getInstance().getHadoopNameNodeHostname());
	}
}
