package com.deleidos.analytics.elasticsearch.client.rest.response;

/**
 * Elasticsearch DELETE index response.
 * 
 * @author vernona
 */
public class DeleteIndexResponse {

	private Boolean acknowledged;

	/**
	 * Empty no-arg constructor.
	 */
	public DeleteIndexResponse() {
	}

	public Boolean getAcknowledged() {
		return acknowledged;
	}

	public void setAcknowledged(Boolean acknowledged) {
		this.acknowledged = acknowledged;
	}
}
