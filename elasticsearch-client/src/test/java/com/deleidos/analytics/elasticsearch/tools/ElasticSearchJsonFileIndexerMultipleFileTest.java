package com.deleidos.analytics.elasticsearch.tools;

import org.junit.Test;

import com.deleidos.analytics.elasticsearch.client.ElasticsearchClientTestBase;
import com.deleidos.analytics.elasticsearch.tools.ElasticsearchJsonFileIndexer;

/**
 * An executable wrapper around the ElasticsearchJsonFileIndexer for running ad hoc data indexing on an elasticsearch
 * cluster from json files in the local file system.
 * 
 * @author vernona
 */
public class ElasticSearchJsonFileIndexerMultipleFileTest extends ElasticsearchClientTestBase {

	// Set these variables for your local environment before running.
	private static final String jsonExtension = "json";
	private static final String baseDir = "C:\\dev\\data\\lastfm_subset";

	private static final String indexName = "lastfm";
	private static final String typeName = "song";

	@Test
	public void testIndexLastFMData() throws Exception {
		ElasticsearchJsonFileIndexer indexer = new ElasticsearchJsonFileIndexer(config, indexName, typeName);
		indexer.indexFiles(baseDir, jsonExtension, true);
	}
}
