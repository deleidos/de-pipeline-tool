package com.deleidos.framework.operators.common;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.deleidos.analytics.common.util.GsonFactory;
import com.deleidos.analytics.common.util.NumberUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;

/**
 * Utility methods for converting to and from the internal operator tuple format (Map<String, Object>).
 * 
 * @author vernona
 */
public class TupleUtil {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(TupleUtil.class);

	private static Gson gson = GsonFactory.getInstance().getGson();

	/**
	 * Get a tuple map as a JSON string.
	 * 
	 * @param map
	 * @return
	 */
	public static String tupleMapToJson(Map<String, Object> map) {
		return gson.toJson(map);
	}

	/**
	 * Get a JSON string as a tuple map.
	 * 
	 * @param json
	 * @return
	 */
	public static Map<String, Object> jsonToTupleMap(String json) {
		Type type = new TypeToken<Map<String, Object>>() {
		}.getType();
		return gson.fromJson(json, type);
	}

	/**
	 * Get an object as a tuple map.
	 * 
	 * @param object
	 * @return
	 */
	public static Map<String, Object> objectToTupleMap(Object object) {
		return jsonToTupleMap(gson.toJson(object));
	}

	/**
	 * Convert a tuple map back into an object.
	 * 
	 * @param map
	 * @param classOfT
	 * @return
	 */
	public static <T> T tupleMapToObject(Map<String, Object> map, Class<T> classOfT) {
		return gson.fromJson(tupleMapToJson(map), classOfT);
	}

	/**
	 * Get a tuple map for a CSV InputTuple.
	 * 
	 * @param inputTuple
	 * @param delimiter
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Object> csvInputTupleToMap(InputTuple inputTuple, String delimiter) throws IOException {
		String escapedDelimiter = delimiter;
		if (delimiter.equals("|")) {
			escapedDelimiter = "\\" + delimiter;
		}
		String[] headerFields = inputTuple.getHeader().get(0).split(escapedDelimiter);
		for (int i = 0; i < headerFields.length; i++) {
			String headerField = headerFields[i];
			if (headerField.length() >= 2 && headerField.charAt(0) == '"'
					&& headerField.charAt(headerField.length() - 1) == '"') {
				headerFields[i] = headerField.substring(1, headerField.length() - 1);
			}
		}

		CSVParser parser = CSVParser.parse(inputTuple.getData(),
				CSVFormat.DEFAULT.withHeader(headerFields).withDelimiter(delimiter.toCharArray()[0]));

		CSVRecord record = parser.iterator().next();

		Map<String, Integer> headerMap = parser.getHeaderMap();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (String field : headerMap.keySet()) {
			String value = record.get(field);
			Number num = NumberUtil.parseNumber(value);
			map.put(field, num == null ? value : num);
		}

		return map;
	}
}
