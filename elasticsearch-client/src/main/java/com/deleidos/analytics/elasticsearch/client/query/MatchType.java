package com.deleidos.analytics.elasticsearch.client.query;

/**
 * Match type enumeration.
 * 
 * @author vernona
 */
public enum MatchType {
	match, // Match on the words without considering the order.
	matchPhrase // Match all words in the string in the same order.
}
