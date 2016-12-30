package com.deleidos.framework.operators.kafka;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.datatorrent.api.Attribute;
import com.datatorrent.api.Context;
import com.datatorrent.api.Context.OperatorContext;
import com.deleidos.analytics.common.logging.LogUtil;
import com.deleidos.analytics.common.util.FileUtil;
import com.deleidos.analytics.kafka.client.MessageConsumer;
import com.deleidos.analytics.kafka.client.MessageHandler;
import com.deleidos.analytics.kafka.client.QueueConfig;
import com.deleidos.framework.operators.common.TupleUtil;

/**
 * Kafka output operator unit test.
 * 
 * @author vernona
 */
public class KafkaOutputOperatorTest {
	private static final String filename = "tpch_sample.json";
	private static final String topic = "sales_order_line_items";

	private MessageConsumer consumer = null;
	private static boolean messageReceived = false;
	private static final long timeoutMillis = 10000;

	@Before
	public void doBefore() {
		LogUtil.initializeLog4jConsoleAppender();
		consumer = new MessageConsumer(
				new QueueConfig(new String[] { "54.158.132.85" }), "unitTestGroup",
				topic, new TestMessageHandler());
	}

	@After
	public void doAfter() {
		if (consumer != null) {
			consumer.close();
		}
	}

	@Test
	public void testKafkaOutputOperator() throws Exception {
		File file = new File(this.getClass().getClassLoader().getResource(filename).getFile());
		String record = FileUtil.getFileContentsAsString(file);
		System.out.println(record);
		Map<String, Object> map = TupleUtil.jsonToTupleMap(record);

		KafkaOutputOperator operator = new KafkaOutputOperator();
		operator.setHostname("configure kafka hostname for testing"); // TODO
		operator.setTopic(topic);

		Attribute.AttributeMap attributeMap = new Attribute.AttributeMap.DefaultAttributeMap();
		attributeMap.put(Context.DAGContext.APPLICATION_NAME, "TestApp");
		TestOperatorContext operatorContext = new TestOperatorContext(2, attributeMap);

		consumer.consume();

		operator.setup(operatorContext);
		operator.beginWindow(0);
		operator.input.process(map);
		operator.endWindow();
		operator.teardown();

		// Wait for the message to be consumed.
		long endTime = System.currentTimeMillis() + timeoutMillis;
		while (!messageReceived && System.currentTimeMillis() < endTime) {
			Thread.sleep(1000);
		}
		
		assertTrue(messageReceived);
	}

	public static class TestMessageHandler implements MessageHandler {
		@Override
		public void handleMessage(String message) {
			System.out.println("handled message: " + message);
			messageReceived = true;
		}
	}

	public static class TestOperatorContext extends TestContext implements OperatorContext {
		int id;
		com.datatorrent.api.Attribute.AttributeMap attributes;

		public TestOperatorContext(int id) {
			this.id = id;
		}

		public TestOperatorContext(int id, com.datatorrent.api.Attribute.AttributeMap map) {
			this.id = id;
			this.attributes = map;
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public void sendMetrics(Collection<String> metricNames) {
			throw new UnsupportedOperationException("not supported");
		}

		@Override
		public <T> T getValue(Attribute<T> key) {
			T value = attributes.get(key);
			if (value != null) {
				return value;
			}
			return super.getValue(key);
		}

		@Override
		public void setCounters(Object counters) {
		}
	}

	private static class TestContext implements Context {
		@Override
		public com.datatorrent.api.Attribute.AttributeMap getAttributes() {
			return null;
		}

		@Override
		public <T> T getValue(Attribute<T> key) {
			return key.defaultValue;
		}

		@Override
		public void setCounters(Object counters) {
			throw new UnsupportedOperationException("not supported");
		}

		@Override
		public void sendMetrics(Collection<String> metricNames) {
			throw new UnsupportedOperationException("not supported");
		}
	}
}
