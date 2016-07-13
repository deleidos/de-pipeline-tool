package com.deleidos.analytics.elasticsearch.client.query;

/**
 * Boolean query/match type enumeration.
 * 
 * @author vernona
 */
public enum QueryType {

	must, // All criteria must match. "AND"
	should // At least one criteria must match. The more criteria that match, the higher the score. "OR"
}
