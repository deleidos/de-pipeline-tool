package com.deleidos.analytics.common.util;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Simple JSON conversion utility.
 * 
 * @author vernona
 */
@Deprecated
public class GsonUtil<T> {

	private final Gson gson;

	public GsonUtil() {
		gson = new GsonBuilder().create();
	}

	public T fromJson(String json, Type type) {
		return gson.fromJson(json, type);
	}

	public T fromJson(String json, Class<T> classOfT) {
		return gson.fromJson(json, classOfT);
	}

	public String toJson(Object src) {
		return gson.toJson(src);
	}
	
	public static JsonElement getNestedJsonElement(JsonObject jsonObject, String dotNotationFieldName) {
		return getNestedJsonElement(jsonObject, dotNotationFieldName.split("\\."), 0);
	}
	
	public static JsonElement getNestedJsonElement(JsonObject jsonObject, String[] dotNotationFieldNames, int level) {
		JsonElement nestedElement = jsonObject.get(dotNotationFieldNames[level]);
		if(nestedElement != null) {
			if((level + 1) < dotNotationFieldNames.length) {
				// There is still more nesting to process
				
				// Make sure the element is an object and therefore able to be processed
				if(nestedElement.isJsonObject()) {
					nestedElement = getNestedJsonElement(nestedElement.getAsJsonObject(),
														 dotNotationFieldNames,
														 level + 1);
				}
				else {
					// There is no next level of nesting
					nestedElement = null;
				}
			}
		}
		
		return nestedElement;
	}
}
