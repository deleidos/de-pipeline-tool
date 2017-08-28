package com.deleidos.analytics.kafka.client;

import java.io.Serializable;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * Queue message producer implementation using Kafka.
 * 
 * @author vernona
 *
 */
@SuppressWarnings("serial")
public class MessageProducer extends QueueBase implements Serializable {

	private Producer<String, String> stringProducer;
	private Producer<String, byte[]> byteProducer;

	/**
	 * Initialize a queue message producer.
	 * 
	 * @param config
	 * @param topic
	 */
	public MessageProducer(QueueConfig config, String topic) {
		super(config, topic);
		init();
	}

	/**
	 * Put a string message in the queue.
	 * 
	 * @param message
	 */
	public void produce(String message) throws Exception {
		stringProducer.send(new ProducerRecord<String, String>(topic, UUID.randomUUID().toString(), message));
	}

	/**
	 * Put a byte[] message in the queue.
	 * 
	 * @param message
	 */
	public void produce(byte[] message) throws Exception {
		byteProducer.send(new ProducerRecord<String, byte[]>(topic, UUID.randomUUID().toString(), message));
	}

	/**
	 * Close the producer.
	 */
	public void close() {
		stringProducer.close();
		byteProducer.close();
	}

	/**
	 * Initialize configuration properties.
	 */
	private Properties buildProperties() {
		Properties props = new Properties();
		props.put("bootstrap.servers", config.getBootstrapServers());
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		return props;
	}

	/**
	 * Initialize the producer.
	 */
	private void init() {
		Properties properties = buildProperties();
		stringProducer = new KafkaProducer<String, String>(properties);
		byteProducer = new KafkaProducer<String, byte[]>(properties);
	}
}
