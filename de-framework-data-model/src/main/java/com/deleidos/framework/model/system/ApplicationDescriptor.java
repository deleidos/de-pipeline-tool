package com.deleidos.framework.model.system;

import java.io.Serializable;
import java.util.List;

import com.deleidos.analytics.common.util.GsonUtil;
//import com.deleidos.analytics.common.util.JsonUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Apache Apex application description object.
 * 
 * @author vernona
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationDescriptor implements Serializable {

	private String description;
	private List<OperatorDescriptor> operators;
	private List<StreamDescriptor> streams;

	/**
	 * Empty no-arg constructor for serialization.
	 */
	public ApplicationDescriptor() {}

	/**
	 * Constructor.
	 * 
	 * @param description
	 * @param operators
	 * @param streams
	 */
	public ApplicationDescriptor(String description, List<OperatorDescriptor> operators, List<StreamDescriptor> streams) {
		this.description = description;
		this.operators = operators;
		this.streams = streams;
	}

	/**
	 * Get Apex app formatted JSON for this application.
	 * 
	 * @return
	 */
	@JsonIgnore
	public String getApexAppJson() throws Exception {
		GsonUtil<ApplicationDescriptor> gson = new GsonUtil<ApplicationDescriptor>();
		return gson.toJson(this);
		//return JsonUtil.toJsonString(this);
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<OperatorDescriptor> getOperators() {
		return operators;
	}

	public void setOperators(List<OperatorDescriptor> operators) {
		this.operators = operators;
	}

	public List<StreamDescriptor> getStreams() {
		return streams;
	}

	public void setStreams(List<StreamDescriptor> streams) {
		this.streams = streams;
	}
}
