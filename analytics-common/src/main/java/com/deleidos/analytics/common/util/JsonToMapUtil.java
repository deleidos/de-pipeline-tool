package com.deleidos.analytics.common.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Legacy DE 2.0 code to convert GSON JsonElements into a map using the proprietary DE 2.0 flattening scheme.
 * 
 * @author vernona
 */
public class JsonToMapUtil {

	private static final String OPEN_ARRAY_CHARACTER = "[";
	private static final String CLOSE_ARRAY_CHARACTER = "]";
	private static final String OBJECT_SEPERATOR = ".";

	/**
	 * Convert a JSON string to the DE 2.0 map representation.
	 * 
	 * @param map
	 * @param json
	 * @param levelName
	 */
	public static void loadJSONFields(Map<String, String> map, String json, String levelName) {
		loadJSONFields(map, new JsonParser().parse(json).getAsJsonObject(), levelName);
	}

	/**
	 * Convert a JsonElement to the DE 2.0 map representation.
	 *
	 * @param map
	 * @param jsonElement
	 * @param levelName
	 */
	public static void loadJSONFields(Map<String, String> map, JsonElement jsonElement, String levelName) {
		if (jsonElement instanceof JsonObject) {
			loadJSONFields(map, (JsonObject) jsonElement, levelName);
		}
		else if (jsonElement instanceof JsonArray) {
			loadJSONFields(map, (JsonArray) jsonElement, levelName);
		}
	}

	/**
	 * Convert a JsonObject to the DE 2.0 map representation.
	 * 
	 * @param map
	 * @param jsonObject
	 * @param levelName
	 */
	public static void loadJSONFields(Map<String, String> map, JsonObject jsonObject, String levelName) {
		String keyName = null;

		Iterator<Entry<String, JsonElement>> parsedDataEntryIterator = jsonObject.entrySet().iterator();
		while (parsedDataEntryIterator.hasNext()) {
			String key = parsedDataEntryIterator.next().getKey();
			JsonElement element = jsonObject.get(key);

			keyName = (levelName != null) ? (levelName + OBJECT_SEPERATOR + key) : (key);

			if (element.isJsonObject()) {
				JsonObject elementJsonObject = element.getAsJsonObject();
				loadJSONFields(map, elementJsonObject, keyName);
			}
			else if (element.isJsonArray()) {
				JsonArray elementJsonArray = element.getAsJsonArray();
				loadJSONFields(map, elementJsonArray, keyName);
			}
			else {
				String value = jsonObject.get(key).toString();
				map.put(keyName, value);
			}
		}
	}

	/**
	 * Convert a JsonArray to the DE 2.0 map representation.
	 * 
	 * @param map
	 * @param jsonArray
	 * @param levelName
	 */
	public static void loadJSONFields(Map<String, String> map, JsonArray jsonArray, String levelName) {
		for (int index = 0; index < jsonArray.size(); index++) {
			String keyName = levelName + OPEN_ARRAY_CHARACTER + index + CLOSE_ARRAY_CHARACTER;

			JsonElement arrayElement = jsonArray.get(index);

			if (arrayElement.isJsonObject()) {
				JsonObject elementJsonObject = arrayElement.getAsJsonObject();
				loadJSONFields(map, elementJsonObject, keyName);
			}
			else if (arrayElement.isJsonArray()) {
				JsonArray elementArrayObject = arrayElement.getAsJsonArray();
				loadJSONFields(map, elementArrayObject, keyName);
			}
			else {
				String value = arrayElement.toString();
				map.put(keyName, value);
			}
		}
	}
}
