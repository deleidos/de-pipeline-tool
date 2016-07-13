package com.deleidos.framework.service.data;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.deleidos.framework.model.system.OperatorMetadata;
import com.google.gson.Gson;

/**
 * Test querying operator metadata from MongoDB.
 * 
 * @author vernona
 */
public class GetOperatorMetadataTest extends DataTestBase {

	@Test
	public void testGetOperatorMetadata() {
		List<OperatorMetadata> metadata = manager.getOperatorMetadata();
		assertNotNull(metadata);
		assertFalse(metadata.isEmpty());
		System.out.println((new Gson()).toJson(metadata));
	}
}
