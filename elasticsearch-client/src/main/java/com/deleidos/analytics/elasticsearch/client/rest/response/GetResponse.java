package com.deleidos.analytics.elasticsearch.client.rest.response;

import java.util.Map;

/**
 * Elasticsearch GET response.
 * 
 * @author vernona
 */
public class GetResponse extends BaseResponse {

	private Boolean found;
	private Map<String, Object> _source;

	/**
	 * Empty no-arg constructor.
	 */
	public GetResponse() {
	}

	public Boolean getFound() {
		return found;
	}

	public void setFound(Boolean found) {
		this.found = found;
	}

	public Map<String, Object> get_source() {
		return _source;
	}

	public void set_source(Map<String, Object> _source) {
		this._source = _source;
	}
}
