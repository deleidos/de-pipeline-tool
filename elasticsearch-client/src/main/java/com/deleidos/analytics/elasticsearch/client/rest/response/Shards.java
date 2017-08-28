package com.deleidos.analytics.elasticsearch.client.rest.response;

/**
 * Elasticsearch search response shards object. Intended to be deserialized from JSON.
 * 
 * @author vernona
 */
public class Shards {

	private Integer total;
	private Integer successful;
	private Integer failed;

	/**
	 * Empty no-arg constructor.
	 */
	public Shards() {
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getSuccessful() {
		return successful;
	}

	public void setSuccessful(Integer successful) {
		this.successful = successful;
	}

	public Integer getFailed() {
		return failed;
	}

	public void setFailed(Integer failed) {
		this.failed = failed;
	}
}
