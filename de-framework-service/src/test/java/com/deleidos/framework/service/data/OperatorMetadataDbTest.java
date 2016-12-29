package com.deleidos.framework.service.data;

import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.deleidos.framework.model.system.OperatorMetadata;
import com.deleidos.framework.model.system.OperatorProperty;

/**
 * Test operator metadata DB operations.
 * 
 * @author vernona
 */
public class OperatorMetadataDbTest extends DataTestBase {

	@Test
	public void testOperatorMetadataDb() {
		List<OperatorProperty> properties = new ArrayList<OperatorProperty>();
		properties.add(new OperatorProperty("testProperty", "Test Property", "String", null, true,
				"Test property description."));
		properties.add(new OperatorProperty("anotherTestProperty", "Another Test Property", "String", null, true,
				"Another test property description."));
		OperatorMetadata metadata = new OperatorMetadata(UUID.randomUUID().toString(), "UnitTestOperator",
				"com.deleidos.framework.operators.test.NonExistentTestOperator", "TestOp",
				"NonExistentTestOperator,jar", "Input", properties);
		manager.insertOperatorMetadata(metadata);

		List<OperatorMetadata> metadataList = manager.getOperatorMetadataList();
		for (OperatorMetadata opMetadata : metadataList) {
			if (opMetadata.getName().equals("UnitTestOperator")) {
				metadata = opMetadata;
				System.out.println(gson.toJson(opMetadata));
			}
		}

		metadata.setDisplayName("UpdatedUnitTestOperator");
		for (OperatorProperty property : metadata.getProperties()) {
			if (property.getName().equals("testProperty")) {
				property.setDisplayName("Updated Test Property");
			}
		}
		manager.updateOperatorMetadata(metadata);

		metadataList = manager.getOperatorMetadataList();
		for (OperatorMetadata opMetadata : metadataList) {
			if (opMetadata.getName().equals("UnitTestOperator")) {
				System.out.println(gson.toJson(opMetadata));
			}
		}

		manager.deleteOperatorMetadata(metadata.get_id());
		metadataList = manager.getOperatorMetadataList();
		metadata = null;
		for (OperatorMetadata opMetadata : metadataList) {
			if (opMetadata.getName().equals("UnitTestOperator")) {
				metadata = opMetadata;
			}
		}

		assertNull(metadata);
	}

}
