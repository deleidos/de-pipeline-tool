package com.deleidos.analytics.elasticsearch.client.aggregation.util;

import java.util.Comparator;

/**
 * Compares normalized counts for sorting. Sorts descending.
 * 
 * This comparator is NOT consistent with equals.
 * 
 * @author vernona
 */
public class NormalizedCountComparator implements Comparator<NormalizedScore> {

	@Override
	public int compare(NormalizedScore o1, NormalizedScore o2) {
		return -((Integer) o1.getNormalizedCount()).compareTo(o2.getNormalizedCount());
	}

}
