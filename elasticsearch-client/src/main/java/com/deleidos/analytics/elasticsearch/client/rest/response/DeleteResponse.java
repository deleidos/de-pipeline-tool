package com.deleidos.analytics.elasticsearch.client.rest.response;

/**
 * Elasticsearch DELETE response object.
 * 
 * @author vernona
 */
public class DeleteResponse extends BaseResponse {

	private Shards _shards;
	private Boolean found;
	private String result;

	/**
	 * Empty no-arg constructor.
	 */
	public DeleteResponse() {
	}

	public Shards get_shards() {
		return _shards;
	}

	public void set_shards(Shards _shards) {
		this._shards = _shards;
	}

	public Boolean getFound() {
		return found;
	}

	public void setFound(Boolean found) {
		this.found = found;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
