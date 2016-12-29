package com.deleidos.framework.operators.dimensional_enrichment;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.datatorrent.api.Attribute;
import com.datatorrent.api.Context;
import com.datatorrent.api.Context.OperatorContext;

import com.datatorrent.lib.testbench.CollectorTestSink;
import com.deleidos.analytics.common.util.FileUtil;
import com.deleidos.analytics.config.AnalyticsConfig;
import com.deleidos.framework.operators.common.TupleUtil;
import com.google.gson.Gson;

/**
 * Unit test.
 * 
 * @author vernona
 */
public class SalesCustomerDataEnrichmentTest {

	private static final String filename = "test_sales_line_item_record.json";

	@Test
	public void testFlightDataEnrichment() throws Exception {
		File file = new File(this.getClass().getClassLoader().getResource(filename).getFile());
		String record = FileUtil.getFileContentsAsString(file);
		System.out.println(record);
		Map<String, Object> map = TupleUtil.jsonToTupleMap(record);

		RedisDimensionalEnrichmentOperator operator = new RedisDimensionalEnrichmentOperator();
		operator.setNamespace("sales_customer");
		operator.setKeyField("CustomerName");
		operator.setDataField("customer_data");
		operator.setCacheHostname(null); // TODO

		CollectorTestSink<Object> mapSink = new CollectorTestSink<Object>();
		operator.outputPort.setSink(mapSink);
		
		Attribute.AttributeMap attributeMap = new Attribute.AttributeMap.DefaultAttributeMap();
	    attributeMap.put(Context.DAGContext.APPLICATION_NAME, "TestApp");
	    TestOperatorContext operatorContext = new TestOperatorContext(2, attributeMap);
		
		operator.setup(operatorContext);
		operator.beginWindow(0);
		operator.input.process(map);
		operator.endWindow();
		operator.teardown();

		assertEquals(1, mapSink.collectedTuples.size());

		List<Object> tuples = mapSink.collectedTuples;
		Gson gson = new Gson();
		for (Object o : tuples) {
			@SuppressWarnings("unchecked")
			Map<String, String> tuple = (Map<String, String>) o;
			System.out.println(gson.toJson(tuple));
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
