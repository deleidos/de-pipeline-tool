package com.deleidos.framework.service.tools;

import java.util.List;

import org.junit.Test;

import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.framework.model.system.OperatorMetadata;
import com.deleidos.framework.service.tools.OperatorMetadataFactory;

/**
 * Test getting operator metadata.
 * 
 * @author vernona
 */
public class OperatorMetadataFactoryTest {

	@Test
	public void testGetOperatorMetadata() throws Exception {
		List<OperatorMetadata> metadataList = OperatorMetadataFactory.getInstance().getOperatorMetadata();
		System.out.println(JsonUtil.toJsonString(metadataList));
	}
}
