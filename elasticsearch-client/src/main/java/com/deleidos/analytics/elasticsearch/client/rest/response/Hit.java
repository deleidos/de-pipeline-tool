package com.deleidos.analytics.elasticsearch.client.rest.response;

import java.util.Map;

/**
 * Elasticsearch search result hit object. The _source field is the response object. Intended to be deserialized from JSON.
 * 
 * @author vernona
 */
public class Hit {
	private String _index;
	private String _type;
	private String _id;
	private Double _score;
	private Map<String, Object> _source;

	/**
	 * Empty no-arg constructor.
	 */
	public Hit() {
	}

	public String get_index() {
		return _index;
	}

	public void set_index(String _index) {
		this._index = _index;
	}

	public String get_type() {
		return _type;
	}

	public void set_type(String _type) {
		this._type = _type;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public Double get_score() {
		return _score;
	}

	public void set_score(Double _score) {
		this._score = _score;
	}

	public Map<String, Object> get_source() {
		return _source;
	}

	public void set_source(Map<String, Object> _source) {
		this._source = _source;
	}
}
