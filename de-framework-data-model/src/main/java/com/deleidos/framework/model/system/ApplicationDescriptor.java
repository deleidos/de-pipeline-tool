package com.deleidos.framework.model.system;

import java.io.Serializable;
import java.util.List;

import com.deleidos.analytics.common.util.GsonUtil;
import com.deleidos.analytics.common.util.GsonFactory;
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

	private transient static final String systemNameProperty = "systemName";

	/**
	 * Empty no-arg constructor for serialization.
	 */
	public ApplicationDescriptor() {
	}

	/**
	 * Get Apex app formatted JSON for this application.
	 * 
	 * @return
	 */
	@JsonIgnore
	public String getApexAppJson() throws Exception {
		return GsonFactory.getInstance().getGson().toJson(this);
	}

	public void setOperatorSystemNameProperty(String systemName) {
		if (operators != null) {
			for (OperatorDescriptor operator : operators) {
				operator.getProperties().put(systemNameProperty, systemName);
			}
		}
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
