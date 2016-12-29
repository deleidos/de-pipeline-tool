package com.deleidos.analytics.kafka.client;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Queue config properties.
 * 
 * @author vernona
 */
public class QueueConfig {

	private String[] hostnames;

	/**
	 * Constructor.
	 * 
	 * @param hostnames
	 */
	public QueueConfig(String[] hostnames) {
		this.hostnames = hostnames;
	}

	public String[] getHostnames() {
		return hostnames;
	}

	public void setHostnames(String[] hostnames) {
		this.hostnames = hostnames;
	}

	/**
	 * Build bootstrap.servers config value (comma-separated string with port number included).
	 * 
	 * @return
	 */
	public String getBootstrapServers() {
		String bootstrapServers = "";
		for (int i = 0; i < hostnames.length; i++) {
			String hostname = hostnames[i];
			bootstrapServers += hostname + ":9092" + (i < hostnames.length - 1 ? "," : "");
		}
		return bootstrapServers;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
