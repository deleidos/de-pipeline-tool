package com.deleidos.analytics.elasticsearch.client.rest.response;

/**
 * Elasticsearch PUT response object returned when indexing a new object.
 * 
 * @author vernona
 */
public class PutResponse extends BaseResponse {

	private Shards _shards;
	private Boolean created;
	private String result;

	/**
	 * Empty no-arg constructor.
	 */
	public PutResponse() {
	}

	public Shards get_shards() {
		return _shards;
	}

	public void set_shards(Shards _shards) {
		this._shards = _shards;
	}

	public Boolean getCreated() {
		return created;
	}

	public void setCreated(Boolean created) {
		this.created = created;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
