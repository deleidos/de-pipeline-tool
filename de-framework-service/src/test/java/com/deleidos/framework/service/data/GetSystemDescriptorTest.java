package com.deleidos.framework.service.data;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Test querying a single system descriptors from the database by ID.
 * 
 * @author vernona
 */
public class GetSystemDescriptorTest extends DataTestBase {

	@Test
	public void testGetSystemDecriptorMissing() throws Exception {
		assertNull(manager.getSystemDecriptor("x"));
	}
	
	@Test
	public void testGetSystemDecriptor() throws Exception {
		assertNotNull(manager.getSystemDecriptor("38cf08b0-ed66-472e-9bc4-6d11827203cb"));
	}
}
