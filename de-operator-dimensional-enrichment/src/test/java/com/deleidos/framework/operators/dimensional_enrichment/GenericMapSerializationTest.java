package com.deleidos.framework.operators.dimensional_enrichment;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.Map;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GenericMapSerializationTest {

	private Gson gson = new Gson();
	
	private String record = "{" + "\"type\":\"position\"," + "\"ident\":\"UAL1639\"," + "\"lat\":\"32.30241\","
			+ "\"lon\":\"-106.37210\"," + "\"clock\":\"1442195082\"," + "\"id\":\"UAL1639-1442016360-schedule-0000\","
			+ "\"rowKey\":\"uuid\"," + "\"tableName\":\"FD_Position\"," + "\"updateType\":\"A\","
			+ "\"air_ground\":\"A\"," + "\"facility_hash\":\"cf9e2b26c5d18f968621dbaff80f2a6349995202\","
			+ "\"facility_name\":\"FlightAware ADS-B\"," + "\"alt\":\"34000\"," + "\"gs\":\"432\","
			+ "\"heading\":\"282\"," + "\"hexid\":\"A8D4F6\"," + "\"reg\":\"N66848\"," + "\"location\":{"
			+ "\"lat\":\"32.30241\"," + "\"lon\":\"-106.37210\"" + "}," + "\"Airports\":{"
			+ "\"id\":\"UAL1639-1442016360-schedule-0000\"," + "\"dest\":\"KLAX\"," + "\"orig\":\"KIAH\"" + "},"
			+ "\"n_number\":\"66848\"" + "}";

	@Test
	public void test() {
		System.out.println(record);
		Map<String, Object> map = objectToMap(record);
		String json = gson.toJson(map);
		System.out.println(json);
		assertEquals(record, json);
	}

	protected Map<String, Object> objectToMap(String json) {
		Type type = new TypeToken<Map<String, Object>>() {}.getType();
		return gson.fromJson(json, type);
	}
}
