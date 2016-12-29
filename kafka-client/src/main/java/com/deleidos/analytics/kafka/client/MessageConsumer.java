package com.deleidos.analytics.kafka.client;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;

/**
 * Queue message consumer implementation using Kafka.
 * 
 * @author vernona
 *
 */
public class MessageConsumer extends QueueBase {

	private final Logger logger = Logger.getLogger(MessageConsumer.class);

	private ConsumerThread consumerThread;
	private KafkaConsumer<String, String> consumer;
	private MessageHandler messageHandler;
	private String consumerGroup;
	private boolean closed = false;

	private static final int pollTimeoutMillis = 2000;

	/**
	 * Initialize a queue message consumer. Call consume method to actually start consuming.
	 * 
	 * @param config
	 * @param consumerGroup
	 * @param topic
	 * @param messageHandler
	 */
	public MessageConsumer(QueueConfig config, String consumerGroup, String topic, MessageHandler messageHandler) {
		super(config, topic);
		this.messageHandler = messageHandler;
		this.consumerGroup = consumerGroup;
		init();
	}

	/**
	 * Initialize configuration properties.
	 */
	protected Properties buildProperties() {
		Properties props = new Properties();
		props.put("bootstrap.servers", config.getBootstrapServers());
		props.put("group.id", consumerGroup);
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		return props;
	}

	/**
	 * Initialize the consumer.
	 */
	private void init() {
		logger.info("kafka bootstrap servers: " + config.getBootstrapServers());
		consumer = new KafkaConsumer<String, String>(buildProperties());
		consumer.subscribe(Arrays.asList(topic));
	}

	/**
	 * Start consuming messages. Calling more than once does nothing. Does nothing after close method is called.
	 */
	public synchronized void consume() {
		// We only want one consumer thread running...
		if (consumerThread == null) {
			consumerThread = new ConsumerThread();
			(new Thread(consumerThread)).start();
		}
	}

	/**
	 * Stop consuming message and close the consumer. This consumer cannot be used again after closing. It should be
	 * allowed to be garbage collected after this method is called.
	 */
	public synchronized void close() {
		System.out.println("Closing consumer.");
		closed = true;
		consumer.close();
	}

	/**
	 * Inner class to encapsulate running the consumer in a thread.
	 * 
	 * @author vernona
	 */
	private final class ConsumerThread implements Runnable {

		@Override
		public void run() {
			while (!closed) {
				logger.debug("polling kafka topic " + topic + " with timeout " + pollTimeoutMillis);
				ConsumerRecords<String, String> records = consumer.poll(pollTimeoutMillis);
				logger.debug("polled kafka topic " + topic + ", records found="
						+ (records == null ? null : records.count()));
				for (ConsumerRecord<String, String> record : records) {
					String message = record.value();
					logger.debug("MessageConsumer consumed message:" + message);
					try {
						messageHandler.handleMessage(message);
					}
					catch (Exception e) {
						logger.error("Unexpected error in message handler ", e);
					}
				}
			}
		}

	}
}
