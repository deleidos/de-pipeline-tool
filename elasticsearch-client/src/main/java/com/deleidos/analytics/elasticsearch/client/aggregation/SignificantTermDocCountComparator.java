package com.deleidos.analytics.elasticsearch.client.aggregation;

import java.util.Comparator;

/**
 * Compares significant term doc counts for sorting. Sorts descending.
 * 
 * This comparator is NOT consistent with equals.
 * 
 * @author vernona
 */
public class SignificantTermDocCountComparator implements Comparator<SignificantTermScore> {

	@Override
	public int compare(SignificantTermScore o1, SignificantTermScore o2) {
		return -((Long) o1.getDocCount()).compareTo(o2.getDocCount());
	}

}
