package com.deleidos.analytics.elasticsearch.client.rest.response;

import java.util.Map;

/**
 * Elasticsearch search response object. Intended to be deserialized from JSON.
 * 
 * @author vernona
 */
public class SearchResponse {

	private Integer took;
	private Boolean timedOut;
	private Map<String, Object> _shards;
	private Hits hits;

	/**
	 * Empty no-arg constructor.
	 */
	public SearchResponse() {
	}

	public Integer getTook() {
		return took;
	}

	public void setTook(Integer took) {
		this.took = took;
	}

	public Boolean getTimedOut() {
		return timedOut;
	}

	public void setTimedOut(Boolean timedOut) {
		this.timedOut = timedOut;
	}

	public Map<String, Object> get_shards() {
		return _shards;
	}

	public void set_shards(Map<String, Object> _shards) {
		this._shards = _shards;
	}

	public Hits getHits() {
		return hits;
	}

	public void setHits(Hits hits) {
		this.hits = hits;
	}

}
