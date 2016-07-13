package com.deleidos.analytics.elasticsearch.client;

import org.junit.Test;

public class ElasticsearchClientIndexSingleRecordTest extends ElasticsearchClientTestBase {
	
	@Test
	public void test() {
		ElasticsearchData data = new ElasticsearchData("indextest", "indextesttype", "1", "{\"name\": \"value\"}");
		client.indexData(data);
	}
}
