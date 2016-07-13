package com.deleidos.analytics.elasticsearch.client.config;

import com.deleidos.analytics.config.AnalyticsConfig;

/**
 * Factory for getting client config instances for testing.
 * 
 * @author vernona
 */
public class ElasticsearchClientConfigFactory {

	private static final ElasticsearchClientConfigFactory instance = new ElasticsearchClientConfigFactory();

	private ElasticsearchClientConfig defaultConfig = new ElasticsearchClientConfig(
			AnalyticsConfig.getInstance().getElasticsearchClusterName(),
			AnalyticsConfig.getInstance().getElasticsearchHostnames());

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
