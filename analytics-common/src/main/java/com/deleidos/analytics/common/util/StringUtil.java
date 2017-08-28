package com.deleidos.analytics.common.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * String utility methods.
 * 
 * @author vernona
 */
public class StringUtil {

	/**
	 * Get an object as a printable string for logging.
	 * 
	 * @param o
	 * @return
	 */
	public static String objectToString(Object o) {
		return ToStringBuilder.reflectionToString(o);
	}

	/**
	 * Split a whitespace-delimited string of words into an array of words.
	 * 
	 * @param s
	 * @return
	 */
	public static String[] splitWhiteSpaceDelimitedString(String s) {
		return s.split("\\s+");
	}

	/**
	 * Build a space-delimited string from an array of strings.
	 * 
	 * @param array
	 * @return
	 */
	public static String stringArrayToSpaceDelimitedString(String[] array) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i] + (i < array.length - 1 ? " " : ""));
		}
		return sb.toString();
	}

	/**
	 * Build a set of strings from a string array. Returns null if a null array is passed and an empty set if an empty
	 * array is passed.
	 * 
	 * @param array
	 * @return
	 */
	public static Set<String> stringArrayToSet(String[] array) {
		Set<String> set = null;
		if (array != null) {
			set = new HashSet<String>(Arrays.asList(array));
		}
		return set;
	}
}
