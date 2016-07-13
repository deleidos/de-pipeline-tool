package com.deleidos.framework.service.data;

import org.junit.Test;

/**
 * Unit tests for the SystemDataManager.
 * 
 * @author vernona
 */
public class SystemInsertTest extends DataTestBase {

	@Test
	public void testInsertSystemDescriptor() throws Exception {
		manager.insertSystemDescriptor(exampleSystem);
	}

}
