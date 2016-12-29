package com.deleidos.analytics.elasticsearch.client.config;

/**
 * Factory for getting client config instances for testing.
 * 
 * @author vernona
 */
public class ElasticsearchClientConfigFactory {

	private static final ElasticsearchClientConfigFactory instance = new ElasticsearchClientConfigFactory();

	// TODO - remove config dependency, this is broken for now, but it's not being used. will need a general solution.
	private ElasticsearchClientConfig defaultConfig = new ElasticsearchClientConfig(null, null);
			

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static ElasticsearchClientConfigFactory getInstance() {
		return instance;
	}

	/**
	 * Private constructor enforces the singleton pattern.
	 */
	private ElasticsearchClientConfigFactory() {}

	/**
	 * Get the default elasticsearch client config.
	 * 
	 * @return
	 */
	public ElasticsearchClientConfig getDefaultElasticsearchClientConfig() {
		return defaultConfig;
	}

}
