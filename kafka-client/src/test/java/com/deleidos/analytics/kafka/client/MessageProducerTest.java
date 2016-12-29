package com.deleidos.analytics.kafka.client;

import org.junit.Test;

import com.deleidos.analytics.kafka.client.MessageProducer;
import com.deleidos.analytics.kafka.client.QueueConfig;
import com.deleidos.analytics.kafka.client.QueueConfigFactory;

/**
 * Queue message producer unit test. Use with the consumer unit test.
 * 
 * @author vernona
 */
public class MessageProducerTest {

	@Test
	public void testMessageProducer() throws Exception {
		QueueConfig config = QueueConfigFactory.getInstance().getEnvQueueConfig();
		String topic = "test";
		MessageProducer producer = new MessageProducer(config, topic);
		Thread.sleep(3000);
		
		// Send some test messages to the queue.
		System.out.println("Producing messages...");
		producer.produce("test1");
		producer.produce("test2");
		producer.produce("test3");
		producer.produce("test4");
		producer.produce("test5");
		System.out.println("Done.");
		
		producer.close();
	}
	
}
