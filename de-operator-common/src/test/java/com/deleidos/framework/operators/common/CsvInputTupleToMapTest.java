package com.deleidos.framework.operators.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * Test converting a CSV input tuple to a Map<String, Object> tuple.
 * 
 * @author vernona
 */
public class CsvInputTupleToMapTest {

	private static final String delimiter = ",";

	private static final String headerNoQuotes = "field1,field2,field3";
	private static final String recordNoQuotes = "test,1,2.5";

	private static final String headerWithQuotes = "\"O_ORDERKEY\",\"O_CUSTKEY\",\"O_ORDERSTATUS\",\"O_TOTALPRICE\",\"O_ORDERDATE\",\"O_ORDERPRIORITY\",\"O_CLERK\",\"O_SHIPPRIORITY\",\"O_COMMENT\"";
	private static final String recordWithQuotes = "\"10279\",\"122258\",\"O\",\"231321.43\",\"1996-09-22\",\"5-LOW\",\"Clerk#000000051\",\"0\",\"nal deposits. fluffily silent ideas are across the\"";

	@Test
	public void testCsvInputTupleToMapNoHeaderQuotes() throws Exception {
		Map<String, Object> map = csvInputTupleToMap(headerNoQuotes, recordNoQuotes);
		
		// Test value existence and types.
		String value1 = (String) map.get("field1");
		assertNotNull(value1);
		Long value2 = (Long) map.get("field2");
		assertNotNull(value2);
		Double value3 = (Double) map.get("field3");
		assertNotNull(value3);
	}

	@Test
	public void testCsvInputTupleToMapWithHeaderQuotes() throws Exception {
		Map<String, Object> map = csvInputTupleToMap(headerWithQuotes, recordWithQuotes);
		
		// Test value existence and types.
		Long value1 = (Long) map.get("O_ORDERKEY");
		assertNotNull(value1);
		String value2 = (String) map.get("O_COMMENT");
		assertNotNull(value2);
	}

	private Map<String, Object> csvInputTupleToMap(String header, String record) throws Exception {
		List<String> headers = new ArrayList<String>();
		headers.add(header);
		InputTuple inputTuple = new InputTuple(headers, record);
		Map<String, Object> map = TupleUtil.csvInputTupleToMap(inputTuple, delimiter);

		String json = TupleUtil.tupleMapToJson(map);
		System.out.println(json);
		return map;
	}
}
