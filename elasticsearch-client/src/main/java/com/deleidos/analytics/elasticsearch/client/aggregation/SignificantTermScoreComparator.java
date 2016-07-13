package com.deleidos.analytics.elasticsearch.client.aggregation;

import java.util.Comparator;

/**
 * Compares significant term score for sorting. Sorts descending.
 * 
 * This comparator is NOT consistent with equals.
 * 
 * @author vernona
 */
public class SignificantTermScoreComparator implements Comparator<SignificantTermScore> {

	@Override
	public int compare(SignificantTermScore o1, SignificantTermScore o2) {
		return -((Double) o1.getScore()).compareTo(o2.getScore());
	}

}
