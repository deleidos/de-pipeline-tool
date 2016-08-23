package com.deleidos.framework.operators.common;

import java.util.Map;

/**
 * Find a value in the tuple map using a hierarchical key field.
 * 
 * @author vernona
 */
public class KeyFieldValueFinder {

	private static final String keyFieldDelimiter = "\\.";

	/**
	 * Split a dot-delimited key field and find it's value in the map as a String.
	 * 
	 * @param keyField
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object findValue(String keyField, Map<String, Object> map) {
		String[] fieldNames = keyField.split(keyFieldDelimiter);
		Map<String, Object> innerMap = map;
		for (int i = 0; i < fieldNames.length - 1; i++) {
			Object value = innerMap.get(fieldNames[i]);
			if (value instanceof Map) {
				innerMap = (Map<String, Object>) value;
			}
		}

		return innerMap.get(fieldNames[fieldNames.length - 1]);
	}
}
