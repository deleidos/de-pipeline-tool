package com.deleidos.analytics.common.util;

import java.util.Arrays;
import java.util.List;

/**
 * Chunks of utility code that are reusable across DAOs.
 * 
 * @author vernona
 */
public final class DaoUtil {

	/**
	 * Get an Integer from a String value. Returns null if the value is null or it is not a valid integer.
	 * 
	 * @param value
	 * @return
	 */
	public static Integer stringToInteger(String value) {
		Integer i = null;
		if (value != null) {
			try {
				i = Integer.parseInt(value);
			}
			catch (NumberFormatException e) {
				// log
			}
		}
		return i;
	}

	/**
	 * Get a Boolean from a String value.
	 * 
	 * @param value
	 * @return null of value is null. true if the value is the string 'true'. false otherwise.
	 */
	public static Boolean stringToBoolean(String value) {
		Boolean b = null;
		if (value != null) {
			b = Boolean.parseBoolean(value);
		}
		return b;
	}

	/**
	 * Get a String from an Integer.
	 * 
	 * @param i
	 * @return null if the value is null
	 */
	public static String integerToString(Integer i) {
		return i == null ? null : i.toString();
	}

	/**
	 * Get a String from a Boolean.
	 * 
	 * @param b
	 * @return null if the value is null
	 */
	public static String booleanToString(Boolean b) {
		return b == null ? null : b.toString();
	}

	/**
	 * Get a list of strings from a comma-separated list of string values.
	 * 
	 * @param value
	 * @return null if the value is null
	 */
	public static List<String> csvToStringList(String value) {
		List<String> valueList = null;
		if (value != null) {
			String[] values = value.split(",");
			valueList = Arrays.asList(values);
		}
		return valueList;
	}

	/**
	 * Get a comma-separated list of strings from a List.
	 * 
	 * @param values
	 * @return null if values is null or empty
	 */
	public static String stringListToCsv(List<String> values) {
		String csv = null;
		if (values != null && !values.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < values.size(); i++) {
				sb.append(values.get(i));
				if (i != values.size() -1) {
					sb.append(',');
				}
			}
			csv = sb.toString();
		}
		return csv;
	}
}
