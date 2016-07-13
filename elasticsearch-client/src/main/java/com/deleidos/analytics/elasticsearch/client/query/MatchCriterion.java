package com.deleidos.analytics.elasticsearch.client.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Match criterion class for specifying query parameters.
 * 
 * @author vernona
 */
public class MatchCriterion {

	private QueryType queryType;
	private MatchType matchType;
	private Map<String, String> fieldValues;

	/**
	 * Constructor.
	 * 
	 * @param queryType
	 * @param matchType
	 * @param fieldValues
	 */
	public MatchCriterion(QueryType queryType, MatchType matchType, Map<String, String> fieldValues) {
		this.queryType = queryType;
		this.matchType = matchType;
		this.fieldValues = fieldValues;
	}

	/**
	 * Constructor.
	 * 
	 * @param queryType
	 * @param matchType
	 * @param fieldName
	 * @param fieldValue
	 */
	public MatchCriterion(QueryType queryType, MatchType matchType, String fieldName, String fieldValue) {
		this.queryType = queryType;
		this.matchType = matchType;
		fieldValues = new HashMap<String, String>();
		fieldValues.put(fieldName, fieldValue);
	}

	/**
	 * Constructor.
	 * 
	 * @param queryType
	 * @param matchType
	 * @param fieldName
	 * @param values
	 */
	public MatchCriterion(QueryType queryType, MatchType matchType, String fieldName, List<String> values) {
		this.queryType = queryType;
		this.matchType = matchType;
		fieldValues = new HashMap<String, String>();
		for (String value : values) {
			fieldValues.put(fieldName, value);
		}
	}

	public QueryType getQueryType() {
		return queryType;
	}

	public void setQueryType(QueryType queryType) {
		this.queryType = queryType;
	}

	public MatchType getMatchType() {
		return matchType;
	}

	public void setMatchType(MatchType matchType) {
		this.matchType = matchType;
	}

	public Map<String, String> getFieldValues() {
		return fieldValues;
	}

	public void setFieldValues(Map<String, String> fieldValues) {
		this.fieldValues = fieldValues;
	}
}
