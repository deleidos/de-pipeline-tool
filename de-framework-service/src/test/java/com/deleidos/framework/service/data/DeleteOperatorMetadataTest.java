package com.deleidos.framework.service.data;

import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.deleidos.framework.model.system.OperatorMetadata;

/**
 * Delete an operator metadata record.
 * 
 * @author vernona
 */
public class DeleteOperatorMetadataTest extends DataTestBase {

	@Test
	public void testDeleteOperatorMetadata() {
		String id = "88c2b01d-d578-4465-971d-b3228daf7982";
		manager.deleteOperatorMetadata(id);
		List<OperatorMetadata> metadataList = manager.getOperatorMetadataList();
		OperatorMetadata metadata = null;
		for (OperatorMetadata opMetadata : metadataList) {
			if (opMetadata.get_id().equals(id)) {
				metadata = opMetadata;
			}
		}
		assertNull(metadata);
	}
}
