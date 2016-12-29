package com.deleidos.analytics.kafka.client;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Test;

import com.deleidos.analytics.kafka.client.MessageConsumer;
import com.deleidos.analytics.kafka.client.MessageHandler;
import com.deleidos.analytics.kafka.client.QueueConfig;
import com.deleidos.analytics.kafka.client.QueueConfigFactory;

/**
 * Queue message consumer unit test. Use with the producer unit test.
 * 
 * Start the consumer before running the producer. Consumer will have to be killed.
 * 
 * @author vernona
 */
public class MessageConsumerTest {

	@Test
	public void testMessageConsumer() throws Exception {
		ConsoleAppender console = new ConsoleAppender(); // create appender
		// configure the appender
		String PATTERN = "%d [%p|%c|%C{1}] %m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.TRACE);
		console.activateOptions();
		// add appender to any Logger (here is root)
		Logger.getRootLogger().addAppender(console);

		QueueConfig config = QueueConfigFactory.getInstance().getEnvQueueConfig();
		String topic = "test";
		MessageConsumer consumer = new MessageConsumer(config, "unitTestGroup", topic, new TestMessageHandler());
		consumer.consume();
	}

	public class TestMessageHandler implements MessageHandler {
		@Override
		public void handleMessage(String message) {
			System.out.println("handled message: " + message);
		}
	}
}
