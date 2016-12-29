package com.deleidos.framework.service.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.deleidos.framework.model.system.SystemDescriptor;

/**
 * Test deleting a system descriptor.
 * 
 * @author vernona
 */
public class DeleteSystemDescriptorTest extends DataTestBase {

	@Test
	public void testDeleteSystemDescriptor() throws Exception {
		SystemDescriptor exampleSystem = SystemDescriptorFactory.getInstance().getExampleSystemDescriptor();
		String id = exampleSystem.get_id();
		manager.insertSystemDescriptor(exampleSystem);
		SystemDescriptor system = manager.getSystemDecriptor(id);
		assertEquals(id, system.get_id());
		manager.deleteSystemDescriptor(exampleSystem.get_id());
		system = manager.getSystemDecriptor(id);
		assertNull(system);
	}

}
