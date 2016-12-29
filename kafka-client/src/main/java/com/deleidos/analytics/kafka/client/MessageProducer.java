package com.deleidos.analytics.kafka.client;

import java.io.Serializable;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * Queue message producer implementation using Kafka.
 * 
 * @author vernona
 *
 */
@SuppressWarnings("serial")
public class MessageProducer extends QueueBase implements Serializable {

	private Producer<String, String> producer;

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
	 * Put a message in the queue.
	 * 
	 * @param message
	 */
	@SuppressWarnings("unused")
	public void produce(String message) throws Exception {
		Future<RecordMetadata> future = producer.send(new ProducerRecord<String, String>(topic, UUID.randomUUID().toString(), message));
		try {
			RecordMetadata metadata = future.get();
		}
		catch(Exception e) {
			e.printStackTrace(System.out);
			throw e;
		}
	}

	/**
	 * Close the producer.
	 */
	public void close() {
		producer.close();
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
		producer = new KafkaProducer<String, String>(buildProperties());
	}
}
