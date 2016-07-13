package com.deleidos.analytics.elasticsearch.client;

import org.elasticsearch.common.logging.ESLoggerFactory;
import org.junit.After;
import org.junit.Before;

import com.deleidos.analytics.elasticsearch.client.config.ElasticsearchClientConfig;
import com.deleidos.analytics.elasticsearch.client.config.ElasticsearchClientConfigFactory;

/**
 * Base class for elasticsearch unit tests. Handles configuration set up and shutdown of client.
 * 
 * @author vernona
 */
public abstract class ElasticsearchClientTestBase {

	protected ElasticsearchClientConfig config;
	protected ElasticsearchClient client;

	/**
	 * Initialize the client. Note that sub-classes using @Before methods must have a different name from this method or
	 * it will shadow this method and this method will not run.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUpBase() throws Exception {
		// Enable elasticsearch logging.
		ESLoggerFactory.getRootLogger().setLevel("TRACE");

		config = ElasticsearchClientConfigFactory.getInstance().getDefaultElasticsearchClientConfig();

		// Build the client.
		client = new ElasticsearchClient(config);
		client.setDebugMode();
	}

	@After
	public void tearDown() throws Exception {
		client.shutdown();
	}
}
