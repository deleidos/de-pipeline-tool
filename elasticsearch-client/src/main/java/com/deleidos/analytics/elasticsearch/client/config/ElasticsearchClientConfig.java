package com.deleidos.analytics.elasticsearch.client.config;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Client configuration for connecting to an elasticsearch cluster.
 * 
 * @author vernona
 */
public class ElasticsearchClientConfig {

	private String clusterName;
	private String[] clusterHostnames;
	private static final int port = 9300;

	/**
	 * Constructor.
	 * 
	 * @param clusterName
	 * @param clusterHostnames
	 */
	public ElasticsearchClientConfig(String clusterName, String[] clusterHostnames) {
		this.clusterName = clusterName;
		this.clusterHostnames = clusterHostnames;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String[] getClusterHostnames() {
		return clusterHostnames;
	}

	public void setClusterHostnames(String[] clusterHostnames) {
		this.clusterHostnames = clusterHostnames;
	}

	public int getPort() {
		return port;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
