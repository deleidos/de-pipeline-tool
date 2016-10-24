package com.deleidos.framework.operators.csv.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.datatorrent.api.Attribute;
import com.datatorrent.api.Context;
import com.datatorrent.api.Context.OperatorContext;
import com.deleidos.framework.operators.common.InputTuple;

/**
 * Unit test for CSV parser operator.
 * 
 * @author vernona
 */
public class CsvParserOperatorTest {

	@Test
	public void testCsvParserOperator() {
		CsvParserOperator operator = new CsvParserOperator();
		Attribute.AttributeMap attributeMap = new Attribute.AttributeMap.DefaultAttributeMap();
		attributeMap.put(Context.DAGContext.APPLICATION_NAME, "TestApp");
		TestOperatorContext operatorContext = new TestOperatorContext(2, attributeMap);

		operator.setDelimiter(",");
		operator.setSystemName("TestSystemName");

		List<String> headers = new ArrayList<String>();
		headers.add("ID,NAME");
		InputTuple inputTuple = new InputTuple(headers, "1,Adam");

		operator.setup(operatorContext);
		operator.beginWindow(0);
		operator.input.process(inputTuple);
		operator.endWindow();
		operator.teardown();
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
