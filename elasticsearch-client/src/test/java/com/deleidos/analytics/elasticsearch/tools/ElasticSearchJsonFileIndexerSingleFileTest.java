package com.deleidos.analytics.elasticsearch.tools;

import org.junit.Test;

import com.deleidos.analytics.elasticsearch.client.ElasticsearchClientTestBase;

/**
 * An executable wrapper around the ElasticsearchJsonFileIndexer for running ad hoc data indexing on an elasticsearch
 * cluster from a single json file in the local file system.
 * 
 * @author vernona
 */
public class ElasticSearchJsonFileIndexerSingleFileTest extends ElasticsearchClientTestBase {
	// Set these variables for your local environment before running.
	private static final String filePath = "C:\\dev\\data\\SampleTweets.txt";

	private static final String indexName = "twitter"; // "rss"
	private static final String typeName = "tweet"; // "news"

	@Test
	public void testIndexTweetData() throws Exception {
		ElasticsearchJsonFileIndexer indexer = new ElasticsearchJsonFileIndexer(config, indexName, typeName);
		indexer.indexFile(filePath);
	}
}
