package com.deleidos.framework.service.data;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.framework.model.system.SystemDescriptor;

/**
 * Validate that converting from objects to maps and back preserves the structure and values.
 * 
 * @author vernona
 */
public class ObjectToMapTest extends DataTestBase {

	@Test
	public void testObjectToMap() throws Exception {
		Map<String, Object> map = manager.objectToMap(exampleSystem);

		System.out.println("system:");
		String systemJson = JsonUtil.toJsonString(exampleSystem);
		System.out.println(systemJson);

		System.out.println("system to map:");
		String mapJson = JsonUtil.toJsonString(map);
		System.out.println(mapJson);

		System.out.println("map json to system:");
		exampleSystem = JsonUtil.fromJsonString(mapJson, SystemDescriptor.class);
		String systemFromMapJson = JsonUtil.toJsonString(exampleSystem);
		System.out.println(systemFromMapJson);

		assertEquals(systemJson, mapJson);
		assertEquals(systemJson, systemFromMapJson);
	}
}
