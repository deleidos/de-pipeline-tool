package com.deleidos.framework.service.data;

import org.junit.Before;

import com.deleidos.framework.service.data.SystemDescriptorFactory;
import com.deleidos.framework.model.system.SystemDescriptor;

/**
 * Base unit test for data access functionality.
 * 
 * @author vernona
 */
public class DataTestBase {

	protected SystemDescriptor exampleSystem;
	protected SystemDataManager manager;

	@Before
	public void setUpBase() throws Exception {
		exampleSystem = SystemDescriptorFactory.getInstance().getExampleSystemDescriptor();
		manager = SystemDataManager.getInstance();
	}
}
