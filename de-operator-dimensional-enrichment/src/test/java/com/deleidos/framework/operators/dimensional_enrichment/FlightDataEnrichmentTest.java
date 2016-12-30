package com.deleidos.framework.operators.dimensional_enrichment;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.datatorrent.lib.testbench.CollectorTestSink;
import com.deleidos.analytics.common.util.FileUtil;
import com.deleidos.framework.operators.common.TupleUtil;
import com.google.gson.Gson;

/**
 * Unit test.
 * 
 * @author vernona
 */
public class FlightDataEnrichmentTest {

	private static final String filename = "test_flight_position_record.json";

	@Test
	public void testFlightDataEnrichment() throws Exception {
		File file = new File(this.getClass().getClassLoader().getResource(filename).getFile());
		String record = FileUtil.getFileContentsAsString(file);
		System.out.println(record);
		Map<String, Object> map = TupleUtil.jsonToTupleMap(record);

		RedisDimensionalEnrichmentOperator operator = new RedisDimensionalEnrichmentOperator();
		operator.setNamespace("faa_data");
		operator.setKeyField("reg");
		operator.setDataField("FAA_Data");
		operator.setCacheHostname(null); // TODO

		CollectorTestSink<Object> mapSink = new CollectorTestSink<Object>();
		operator.outputPort.setSink(mapSink);

		operator.beginWindow(0);
		operator.input.process(map);
		operator.endWindow();

		assertEquals(1, mapSink.collectedTuples.size());

		List<Object> tuples = mapSink.collectedTuples;
		Gson gson = new Gson();
		for (Object o : tuples) {
			@SuppressWarnings("unchecked")
			Map<String, String> tuple = (Map<String, String>) o;
			System.out.println(gson.toJson(tuple));
		}
	}
}
