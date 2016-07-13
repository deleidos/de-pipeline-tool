package com.deleidos.analytics.elasticsearch.client.filter;

import java.util.HashMap;
import java.util.Map;

/**
 * Filter query results where the field(s) exactly match term value(s).
 * 
 * @author vernona
 */
public class TermFilter implements Filter {

	private Map<String, String> fieldValues;

	/**
	 * Constructor for a map of field values.
	 * 
	 * @param fieldValues
	 */
	public TermFilter(Map<String, String> fieldValues) {
		this.fieldValues = fieldValues;
	}

	/**
	 * Constructor for a single field value.
	 * 
	 * @param fieldValues
	 */
	public TermFilter(String fieldName, String value) {
		fieldValues = new HashMap<String, String>();
		fieldValues.put(fieldName, value);
	}

	public Map<String, String> getFieldValues() {
		return fieldValues;
	}

	public void setFieldValues(Map<String, String> fieldValues) {
		this.fieldValues = fieldValues;
	}
}
