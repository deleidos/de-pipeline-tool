package com.deleidos.analytics.kafka.client;

/**
 * Factory for getting queue config instances.
 * 
 * @author vernona
 */
public class QueueConfigFactory {

	private static final QueueConfigFactory instance = new QueueConfigFactory();

	private QueueConfig envConfig = null;

	protected static final String kafkaHostnameEnv = "KAFKA_HOSTNAME";

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static QueueConfigFactory getInstance() {
		return instance;
	}

	/**
	 * Private constructor enforces the singleton pattern.
	 */
	private QueueConfigFactory() {
		String[] hostnames = { System.getenv(kafkaHostnameEnv) };
		envConfig = new QueueConfig(hostnames);
	}

	public QueueConfig getEnvQueueConfig() {
		return envConfig;
	}
}
