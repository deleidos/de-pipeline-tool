package com.deleidos.analytics.elasticsearch.client.rest.response;

import java.util.List;

/**
 * Elasticsearch search response hits object. Intended to be deserialized from JSON.
 * 
 * @author vernona
 *
 */
public class Hits {

	private Integer total;
	private Double maxScore;
	private List<Hit> hits;

	/**
	 * Empty no-arg constructor.
	 */
	public Hits() {
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Double getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Double maxScore) {
		this.maxScore = maxScore;
	}

	public List<Hit> getHits() {
		return hits;
	}

	public void setHits(List<Hit> hits) {
		this.hits = hits;
	}
}
