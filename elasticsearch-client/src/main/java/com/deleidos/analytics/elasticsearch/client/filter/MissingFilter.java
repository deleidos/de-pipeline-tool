package com.deleidos.analytics.elasticsearch.client.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter query results where the field is missing. Only returns records that do NOT have the listed field(s).
 * 
 * @author vernona
 */
public class MissingFilter implements Filter {

	private List<String> fieldNames;

	/**
	 * Constructor for a list of fields.
	 * 
	 * @param fieldNames
	 */
	public MissingFilter(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}
	
	/**
	 * Constructor for a single field.
	 * 
	 * @param fieldNames
	 */
	public MissingFilter(String fieldName) {
		fieldNames = new ArrayList<String>();
		fieldNames.add(fieldName);
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}
}
