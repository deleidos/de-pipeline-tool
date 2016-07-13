package com.deleidos.analytics.common.graph;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A D3 graph link.
 * 
 * @author vernona
 */
@SuppressWarnings("serial")
public class D3GraphLink implements Serializable {

	private int source;
	private int target;
	private int value;

	/**
	 * Public no-arg constructor needed for serialization.
	 */
	public D3GraphLink() {}

	/**
	 * Constructor.
	 * 
	 * @param source
	 * @param target
	 * @param value
	 */
	public D3GraphLink(int source, int target, int value) {
		this.source = source;
		this.target = target;
		this.value = value;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
