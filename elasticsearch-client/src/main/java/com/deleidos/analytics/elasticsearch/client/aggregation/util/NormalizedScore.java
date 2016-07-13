package com.deleidos.analytics.elasticsearch.client.aggregation.util;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A normalized score.
 * 
 * @author vernona
 */
public class NormalizedScore {

	private String key;
	private int normalizedScore;
	private int normalizedCount;

	/**
	 * Constructor.
	 * 
	 * @param key
	 * @param normalizedScore
	 * @param normalizedCount
	 */
	public NormalizedScore(String key, int normalizedScore, int normalizedCount) {
		this.key = key;
		this.normalizedScore = normalizedScore;
		this.normalizedCount = normalizedCount;
	}

	public int getNormalizedScore() {
		return normalizedScore;
	}

	public void setNormalizedScore(int normalizedScore) {
		this.normalizedScore = normalizedScore;
	}

	public int getNormalizedCount() {
		return normalizedCount;
	}

	public void setNormalizedCount(int normalizedCount) {
		this.normalizedCount = normalizedCount;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
