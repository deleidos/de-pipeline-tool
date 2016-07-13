package com.deleidos.analytics.elasticsearch.client.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Filter query results where the field exists (has a value). Only returns records that have a value for the listed
 * field(s).
 * 
 * @author vernona
 */
public class ExistsFilter implements Filter {

	private List<String> fieldNames;

	/**
	 * Constructor for a list of fields.
	 * 
	 * @param fieldNames
	 */
	public ExistsFilter(String... fieldNames) {
		this.fieldNames = Arrays.asList(fieldNames);
	}

	/**
	 * Constructor for a single field.
	 * 
	 * @param fieldNames
	 */
	public ExistsFilter(String fieldName) {
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
