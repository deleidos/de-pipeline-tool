package com.deleidos.analytics.elasticsearch.client.aggregation.util;

import java.util.Comparator;

/**
 * Compares normalized counts by score for sorting. Sorts descending.
 * 
 * This comparator is NOT consistent with equals.
 * 
 * @author vernona
 */
public class NormalizedCountScoreComparator implements Comparator<NormalizedScore> {

	@Override
	public int compare(NormalizedScore o1, NormalizedScore o2) {
		return -((Integer) o1.getNormalizedScore()).compareTo(o2.getNormalizedScore());
	}

}
