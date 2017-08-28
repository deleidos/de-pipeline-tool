package com.deleidos.analytics.elasticsearch.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A single query result hit.
 * 
 * Includes convenience methods for getting typed data.
 * 
 * @author vernona
 */
@Deprecated
public class ElasticsearchQueryResult {

	private String id;
	private Map<String, List<Object>> fieldValues;

	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param fieldValues
	 */
	public ElasticsearchQueryResult(String id, Map<String, List<Object>> fieldValues) {
		this.id = id;
		this.fieldValues = fieldValues;
	}

	/**
	 * Convenience method for when the caller is expecting the results for the field to be a single string value.
	 * 
	 * @param fieldName
	 * @return
	 */
	public String getStringValue(String fieldName) {
		String value = null;
		List<Object> list = fieldValues.get(fieldName);
		if (list != null && !list.isEmpty()) {
			value = list.get(0).toString(); // Calling toString on a String returns the String itself.
		}
		return value;
	}

	/**
	 * Convenience method for when the caller is expecting the results for the field to be a single integer value.
	 * 
	 * @param fieldName
	 * @return
	 */
	public Integer getIntegerValue(String fieldName) {
		Integer value = null;
		List<Object> list = fieldValues.get(fieldName);
		if (list != null && !list.isEmpty()) {
			value = (Integer) list.get(0);
		}
		return value;
	}

	/**
	 * Convenience method for when the caller is expecting the results for the field to be a single long value.
	 * 
	 * @param fieldName
	 * @return
	 */
	public Long getLongValue(String fieldName) {
		Long value = null;
		List<Object> list = fieldValues.get(fieldName);
		if (list != null && !list.isEmpty()) {
			System.out.println(list.get(0).getClass().getName());
			value = (Long) list.get(0);
		}
		return value;
	}

	/**
	 * Convenience method for when the caller is expecting the results for the field to be a single double value.
	 * 
	 * @param fieldName
	 * @return
	 */
	public Double getDoubleValue(String fieldName) {
		Double value = null;
		List<Object> list = fieldValues.get(fieldName);
		if (list != null && !list.isEmpty()) {
			value = (Double) list.get(0);
		}
		return value;
	}

	/**
	 * Convenience method for when the caller is expecting the results for the field to be a list of strings.
	 * 
	 * @param fieldName
	 * @return
	 */
	public List<String> getStringList(String fieldName) {
		List<String> stringList = new ArrayList<String>();
		List<Object> list = fieldValues.get(fieldName);
		if (list != null && !list.isEmpty()) {
			for (Object o : list) {
				stringList.add(o.toString());
			}
		}
		return stringList;
	}

	/**
	 * Convenience method for when the caller is expecting the results for the field to be a list of integers.
	 * 
	 * @param fieldName
	 * @return
	 */
	public List<Integer> getIntegerList(String fieldName) {
		List<Integer> intList = new ArrayList<Integer>();
		List<Object> list = fieldValues.get(fieldName);
		if (list != null && !list.isEmpty()) {
			for (Object o : list) {
				intList.add(Integer.parseInt(o.toString()));
			}
		}
		return intList;
	}

	/**
	 * Convenience method for when the caller is expecting the results for the field to be a list of longs.
	 * 
	 * @param fieldName
	 * @return
	 */
	public List<Long> getLongList(String fieldName) {
		List<Long> longList = new ArrayList<Long>();
		List<Object> list = fieldValues.get(fieldName);
		if (list != null && !list.isEmpty()) {
			for (Object o : list) {
				longList.add((Long.parseLong(o.toString())));
			}
		}
		return longList;
	}

	/**
	 * Convenience method for when the caller is expecting the results for the field to be a list of doubles.
	 * 
	 * @param fieldName
	 * @return
	 */
	public List<Double> getDoubleList(String fieldName) {
		List<Double> doubleList = new ArrayList<Double>();
		List<Object> list = fieldValues.get(fieldName);
		if (list != null && !list.isEmpty()) {
			for (Object o : list) {
				doubleList.add(Double.parseDouble(o.toString()));
			}
		}
		return doubleList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, List<Object>> getFieldValues() {
		return fieldValues;
	}

	public void setFieldValues(Map<String, List<Object>> fieldValues) {
		this.fieldValues = fieldValues;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getPartialFieldObject() {
		List<Object> values = getFieldValues().get("partial1");
		return (Map<String, Object>) values.get(0);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
