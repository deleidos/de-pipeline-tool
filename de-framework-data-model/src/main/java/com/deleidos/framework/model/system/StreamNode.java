package com.deleidos.framework.model.system;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;

/**
 * Stream node configuration object.
 * 
 * @author vernona
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreamNode implements Serializable {

	@SerializedName("operatorName")
	private String operatorName;
	@SerializedName("portName")
	private String portName;

	/**
	 * Empty no-arg constructor for serialization.
	 */
	public StreamNode() {}

	/**
	 * Constructor.
	 * 
	 * @param operatorName
	 * @param portName
	 */
	public StreamNode(String operatorName, String portName) {
		this.operatorName = operatorName;
		this.portName = portName;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}
}
