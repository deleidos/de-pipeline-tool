package com.deleidos.analytics.elasticsearch.client.aggregation;

/**
 * Value count aggregation result.
 * 
 * @author vernona
 */
public class Count {

	private int count;

	/**
	 * Constructor.
	 * 
	 * @param count
	 */
	public Count(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
