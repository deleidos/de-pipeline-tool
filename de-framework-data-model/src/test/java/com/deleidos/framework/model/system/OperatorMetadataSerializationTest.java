package com.deleidos.framework.model.system;

import org.junit.Test;

import com.deleidos.analytics.common.util.FileUtil;
import com.deleidos.analytics.common.util.JsonUtil;

/**
 * Test operator metadata serialization.
 * 
 * @author vernona
 */
public class OperatorMetadataSerializationTest {

	@Test
	public void testOperatorMetadataSerialization() throws Exception {
		String json = FileUtil.getResourceFileContentsAsString("operator_metadata_test.json");
		System.out.println(json);

		SaveOperatorMetadata metadata = JsonUtil.fromJsonString(json, SaveOperatorMetadata.class);
		System.out.println("_id=" + metadata.getMetadata().getMetadata().get_id());
		System.out.println(JsonUtil.toJsonString(metadata));
	}

	public static class SaveOperatorMetadata {
		private OperatorMetadataRequest metadata;

		public SaveOperatorMetadata() {
		}

		public OperatorMetadataRequest getMetadata() {
			return metadata;
		}

		public void setMetadata(OperatorMetadataRequest metadata) {
			this.metadata = metadata;
		}
	}
}
