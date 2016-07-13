package com.deleidos.analytics.elasticsearch.client.aggregation;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A metric value for a term.
 * 
 * @author vernona
 */
public class TermMetricValue {

	private String term;
	private Double value;

	/**
	 * Constructor.
	 * 
	 * @param term
	 * @param value
	 */
	public TermMetricValue(String term, Double value) {
		this.term = term;
		this.value = value;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
