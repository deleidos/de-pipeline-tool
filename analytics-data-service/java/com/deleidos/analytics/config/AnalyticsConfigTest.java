package com.deleidos.analytics.config;

import org.junit.Test;

import com.deleidos.analytics.data.service.app.AnalyticsConfig;

/**
 * Test loading of the analytics config file.
 * 
 * @author vernona
 */
public class AnalyticsConfigTest {

	@Test
	public void testDataServiceConfiguration() throws Exception {
		System.out.println(AnalyticsConfig.getInstance());
	}
}
