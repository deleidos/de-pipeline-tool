package com.deleidos.analytics.kafka.client;

/**
 * Base class for producers/consumers to share common code.
 * 
 * @author vernona
 */
class QueueBase {
	protected QueueConfig config;
	protected String topic;

	/**
	 * Constructor.
	 * 
	 * @param hostnames
	 * @param topic
	 */
	protected QueueBase(QueueConfig config, String topic) {
		this.config = config;
		this.topic = topic;
	}
}
