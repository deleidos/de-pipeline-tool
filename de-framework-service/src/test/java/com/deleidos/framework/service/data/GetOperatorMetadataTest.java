package com.deleidos.framework.service.data;

import java.util.List;

import org.junit.Test;

import com.deleidos.framework.model.system.OperatorMetadata;

/**
 * Test getting operator metadata.
 * 
 * @author vernona
 */
public class GetOperatorMetadataTest extends DataTestBase {

	@Test
	public void testGetOperatorMetadata() {
		List<OperatorMetadata> metadataList = manager.getOperatorMetadataList();
		for (OperatorMetadata metadata : metadataList) {
			System.out.println(gson.toJson(metadata));
		}
	}
}
