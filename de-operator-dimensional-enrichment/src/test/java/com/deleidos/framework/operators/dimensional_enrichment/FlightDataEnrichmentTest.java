package com.deleidos.framework.operators.dimensional_enrichment;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.datatorrent.lib.testbench.CollectorTestSink;
import com.deleidos.analytics.common.util.FileUtil;
import com.deleidos.analytics.config.AnalyticsConfig;
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

		RedisDimensionalEnrichmentOperator operator = new RedisDimensionalEnrichmentOperator();
		operator.setKeyField("reg");
		operator.setDataField("FAA_Data");
		operator.setCacheHostname(AnalyticsConfig.getInstance().getRedisHostname());

		CollectorTestSink<Object> mapSink = new CollectorTestSink<Object>();
		operator.output.setSink(mapSink);

		operator.beginWindow(0);
		operator.input.process(record);
		operator.endWindow();

		assertEquals(1, mapSink.collectedTuples.size());

		List<Object> tuples = mapSink.collectedTuples;
		Gson gson = new Gson();
		for (Object o : tuples) {
			String tuple = (String) o;
			System.out.println(gson.toJson(tuple));
		}
	}
}
