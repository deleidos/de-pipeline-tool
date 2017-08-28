package com.deleidos.analytics.elasticsearch.client.filter;

import java.util.HashMap;
import java.util.Map;

/**
 * Filter query results where the field(s) exactly match term value(s).
 * 
 * @author vernona
 */
public class TermFilter implements Filter {

	private Map<String, Object> fieldValues;

	/**
	 * Constructor for a map of field values.
	 * 
	 * @param fieldValues
	 */
	public TermFilter(Map<String, Object> fieldValues) {
		this.fieldValues = fieldValues;
	}

	/**
	 * Constructor for a single field value.
	 * 
	 * @param fieldName
	 * @param value
	 */
	public TermFilter(String fieldName, Object value) {
		fieldValues = new HashMap<String, Object>();
		fieldValues.put(fieldName, value);
	}

	public Map<String, Object> getFieldValues() {
		return fieldValues;
	}

	public void setFieldValues(Map<String, Object> fieldValues) {
		this.fieldValues = fieldValues;
	}
}
