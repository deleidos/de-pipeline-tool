package com.deleidos.analytics.common.graph;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A D3 graph. This class is designed to conveniently serialize to the D3 graph JSON model.
 * 
 * @author vernona
 */
@SuppressWarnings("serial")
public class D3Graph implements Serializable {

	private List<D3GraphNode> nodes;
	private List<D3GraphLink> links;
	
	/**
	 * Public no-arg constructor needed for serialization.
	 */
	public D3Graph() {}

	/**
	 * Constructor.
	 * 
	 * @param nodes
	 * @param links
	 */
	public D3Graph(List<D3GraphNode> nodes, List<D3GraphLink> links) {
		this.nodes = nodes;
		this.links = links;
	}

	public List<D3GraphNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<D3GraphNode> nodes) {
		this.nodes = nodes;
	}

	public List<D3GraphLink> getLinks() {
		return links;
	}

	public void setLinks(List<D3GraphLink> links) {
		this.links = links;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this); 
	}
}
