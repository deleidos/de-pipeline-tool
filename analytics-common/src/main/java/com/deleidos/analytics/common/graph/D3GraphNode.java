package com.deleidos.analytics.common.graph;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A D3 graph node.
 * 
 * @author vernona
 */
@SuppressWarnings("serial")
public class D3GraphNode implements Serializable {

	private String name;
	private int group;
	private int size;

	/**
	 * Public no-arg constructor needed for serialization.
	 */
	public D3GraphNode() {}

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param group
	 * @param size
	 */
	public D3GraphNode(String name, int group, int size) {
		this.name = name;
		this.group = group;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
