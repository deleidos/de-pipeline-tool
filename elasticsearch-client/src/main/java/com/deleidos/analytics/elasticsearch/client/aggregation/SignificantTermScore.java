package com.deleidos.analytics.elasticsearch.client.aggregation;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Significant term score aggregation query result for a particular key (word).
 * 
 * @author vernona
 */
public class SignificantTermScore {

	private String term;
	private double score;
	private long docCount;

	/**
	 * Constructor.
	 * 
	 * @param term
	 * @param score
	 * @param docCount
	 */
	public SignificantTermScore(String term, double score, long docCount) {
		this.term = term;
		this.score = score;
		this.docCount = docCount;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public long getDocCount() {
		return docCount;
	}

	public void setDocCount(long docCount) {
		this.docCount = docCount;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
