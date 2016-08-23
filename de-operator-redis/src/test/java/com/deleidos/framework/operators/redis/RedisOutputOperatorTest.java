package com.deleidos.framework.operators.redis;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.datatorrent.api.Attribute;
import com.datatorrent.api.Context;
import com.datatorrent.api.Context.OperatorContext;
import com.deleidos.analytics.common.util.FileUtil;
import com.deleidos.analytics.config.AnalyticsConfig;
import com.deleidos.analytics.redis.client.RedisClient;
import com.deleidos.framework.operators.common.TupleUtil;

/**
 * Unit test.
 * 
 * @author vernona
 */
public class RedisOutputOperatorTest {

	private static final String filename = "test_sales_line_item_record.json";
	private static final String namespace = "test" + System.currentTimeMillis();
	private static final String keyField = "OrderKey";
	private static final String keyValue = "1282";
	
	protected RedisClient client = null;

	@Before
	public void doBefore() {
		client = new RedisClient(AnalyticsConfig.getInstance().getRedisHostname());
	}

	@After
	public void doAfter() {
		if (client != null) {
			client.delete(namespace, keyValue);
			client.close();
		}
	}
	
	@Test
	public void testRedisOutputOperator() throws Exception {
		System.out.println(AnalyticsConfig.getInstance().getRedisHostname());
		File file = new File(this.getClass().getClassLoader().getResource(filename).getFile());
		String record = FileUtil.getFileContentsAsString(file);
		System.out.println(record);
		Map<String, Object> map = TupleUtil.jsonToTupleMap(record);

		RedisOutputOperator operator = new RedisOutputOperator();
		operator.setHostname(AnalyticsConfig.getInstance().getRedisHostname());
		operator.setNamespace(namespace);
		operator.setKeyField(keyField);

		Attribute.AttributeMap attributeMap = new Attribute.AttributeMap.DefaultAttributeMap();
	    attributeMap.put(Context.DAGContext.APPLICATION_NAME, "TestApp");
	    TestOperatorContext operatorContext = new TestOperatorContext(2, attributeMap);
		
		operator.setup(operatorContext);
		operator.beginWindow(0);
		operator.input.process(map);
		operator.endWindow();
		operator.teardown();

		String json = client.getValue(namespace, keyValue);
		System.out.println(json);
		assertNotNull(json);
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
