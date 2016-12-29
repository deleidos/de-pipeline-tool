package com.deleidos.framework.service.data;

import org.junit.Test;

import com.deleidos.framework.model.system.SystemDescriptor;

/**
 * Unit tests for the SystemDataManager.
 * 
 * @author vernona
 */
public class SystemInsertTest extends DataTestBase {

	@Test
	public void testInsertSystemDescriptor() throws Exception {
		SystemDescriptor exampleSystem = SystemDescriptorFactory.getInstance().getExampleSystemDescriptor();
		manager.insertSystemDescriptor(exampleSystem);
	}

}
