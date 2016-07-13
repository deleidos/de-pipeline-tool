package com.deleidos.framework.service.data;

import org.junit.Test;

import com.deleidos.framework.model.system.ApplicationDescriptor;

/**
 * Test loading example application files.
 * 
 * @author vernona
 */
public class ApplicationDescriptorFactoryTest {

	@Test
	public void testGetExampleApplicationDescriptor() throws Exception {
		ApplicationDescriptorFactory factory = ApplicationDescriptorFactory.getInstance();
		ApplicationDescriptor system = factory.getExampleApplicationDescriptor();
		System.out.println(system.getApexAppJson());
	}
}
