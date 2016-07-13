package com.deleidos.framework.service.data;

import org.junit.Test;

import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.framework.model.system.SystemDescriptor;

/**
 * Test querying all system descriptors from the database.
 * 
 * @author vernona
 */
public class GetSystemDecriptorsTest extends DataTestBase {

	@Test
	public void testGetSystemDecriptors() throws Exception {
		for (SystemDescriptor system : manager.getSystemDecriptors()) {
			System.out.println(JsonUtil.toJsonString(system));
		}
	}
}
