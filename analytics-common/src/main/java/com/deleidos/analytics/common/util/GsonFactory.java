package com.deleidos.analytics.common.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

/**
 * Get a Gson object with custom deserializers.
 * 
 * The version with collection deserializer is designed for use with objects of unknown type into Map<String, Object>.
 * Objects are deserialized as LinkedHashMap with order preserved. Arrays are deserialized as ArrayList. Numbers are
 * parsed as Long or Double. Strings are Strings.
 * 
 * This class is useful for generically deserializing objects of unknown structure into consistent types with order
 * preserved, but it may not work in cases where deserializing into known object types. Use the base Gson with no custom
 * deserializers for that scenario (if it works).
 * 
 * @author vernona
 */
public class GsonFactory {
	/** Singleton instance. */
	private static GsonFactory instance = new GsonFactory();

	/** Thread-safe default Gson instance. */
	private static Gson gsonNoDeserializers = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
			.serializeNulls().setPrettyPrinting().create();

	/** Thread-safe Gson instance with primitive and collection deserializers. */
	private static Gson gsonWithCollectionDeserializers = new GsonBuilder()
			.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).registerTypeAdapter(Map.class, new MapDeserializer())
			.registerTypeAdapter(List.class, new ListDeserializer()).serializeNulls().setPrettyPrinting().create();

	/**
	 * Private constructor enforces the singleton pattern.
	 */
	private GsonFactory() {
	}

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static GsonFactory getInstance() {
		return instance;
	}

	/**
	 * Get GSON instance with some basic settings but no custom deserializers.
	 */
	public Gson getGsonWithNoDeserializers() {
		return gsonNoDeserializers;
	}

	/**
	 * Get a Gson instance with a custom primitive and Map<String, Object> collection deserializer.
	 * 
	 * @return
	 */
	public Gson getGsonWithCollectionDeserializers() {
		return gsonWithCollectionDeserializers;
	}

	//
	// Private methods:
	//

	/**
	 * Custom map (JSON object) deserializer.
	 */
	private static class MapDeserializer implements JsonDeserializer<Map<String, Object>> {
		public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			Map<String, Object> m = new LinkedHashMap<String, Object>();
			JsonObject jo = json.getAsJsonObject();
			for (Entry<String, JsonElement> mx : jo.entrySet()) {
				String key = mx.getKey();
				JsonElement jsonElement = mx.getValue();
				if (jsonElement.isJsonArray()) {
					m.put(key, gsonWithCollectionDeserializers.fromJson(jsonElement, List.class));
				}
				else if (jsonElement.isJsonPrimitive()) {
					m.put(key, parseJsonPrimitive(jsonElement.getAsJsonPrimitive()));
				}
				else if (jsonElement.isJsonObject()) {
					m.put(key, gsonWithCollectionDeserializers.fromJson(jsonElement, Map.class));
				}

			}
			return m;
		}
	}

	/**
	 * Custom list (JSON array) deserializer.
	 */
	private static class ListDeserializer implements JsonDeserializer<List<Object>> {
		public List<Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			List<Object> m = new ArrayList<Object>();
			JsonArray arr = json.getAsJsonArray();
			for (JsonElement jsonElement : arr) {
				if (jsonElement.isJsonObject()) {
					m.add(gsonWithCollectionDeserializers.fromJson(jsonElement, Map.class));
				}
				else if (jsonElement.isJsonArray()) {
					m.add(gsonWithCollectionDeserializers.fromJson(jsonElement, List.class));
				}
				else if (jsonElement.isJsonPrimitive()) {
					m.add(parseJsonPrimitive(jsonElement.getAsJsonPrimitive()));
				}
			}
			return m;
		}
	}

	/**
	 * Parse a JsonPrimitive as an Object.
	 * 
	 * @param jsonPrimitive
	 * @return
	 */
	private static Object parseJsonPrimitive(JsonPrimitive jsonPrimitive) {
		if (jsonPrimitive.isString()) {
			return jsonPrimitive.getAsString();
		}
		else {
			return NumberUtil.parseNumber(jsonPrimitive.getAsString());
		}
	}
}
