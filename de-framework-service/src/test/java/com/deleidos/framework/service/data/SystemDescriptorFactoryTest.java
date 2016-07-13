package com.deleidos.framework.service.data;

import org.junit.Test;

import com.deleidos.analytics.common.util.JsonUtil;

/**
 * Test getting example system descriptors.
 * 
 * @author vernona
 */
public class SystemDescriptorFactoryTest {

	@Test
	public void testGetExampleSystemDescriptor() throws Exception {
		System.out.println(JsonUtil.toJsonString(SystemDescriptorFactory.getInstance().getExampleSystemDescriptor()));
	}
}
