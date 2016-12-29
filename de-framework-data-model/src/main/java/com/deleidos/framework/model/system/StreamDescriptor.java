package com.deleidos.framework.model.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;

/**
 * Stream descriptor configuration object.
 * 
 * @author vernona
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreamDescriptor implements Serializable {

	private String name;
	private StreamNode source;
	private List<StreamNode> sinks;

	@JsonIgnore
	@Expose(serialize = false, deserialize = false)
	private static final String sourcePortName = "outputPort";
	@JsonIgnore
	@Expose(serialize = false, deserialize = false)
	private static final String sinkPortName = "input";

	/**
	 * Empty no-arg constructor for serialization.
	 */
	public StreamDescriptor() {}
	
	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param sourceOperator
	 * @param sinkOperator
	 */
	public StreamDescriptor(String name, OperatorDescriptor sourceOperator, OperatorDescriptor sinkOperator) {
		this.name = name;

		source = new StreamNode(sourceOperator.getName(), sourcePortName);
		sinks = new ArrayList<StreamNode>();
		sinks.add(new StreamNode(sinkOperator.getName(), sinkPortName));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StreamNode getSource() {
		return source;
	}

	public void setSource(StreamNode source) {
		this.source = source;
	}

	public List<StreamNode> getSinks() {
		return sinks;
	}

	public void setSinks(List<StreamNode> sinks) {
		this.sinks = sinks;
	}
}
