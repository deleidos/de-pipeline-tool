package com.deleidos.framework.model.system;

import java.io.Serializable;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Operator decriptor representation for modeling systems.
 * 
 * @author vernona
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperatorDescriptor implements Serializable {

	private String name;
	@SerializedName("class")
	private String className;
	private Map<String, Object> properties;

	/**
	 * Empty no-arg constructor for serialization.
	 */
	public OperatorDescriptor() {}
	
	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param className
	 * @param properties
	 */
	public OperatorDescriptor(String name, String className, Map<String, Object> properties) {
		this.name = name;
		this.className = className;
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
}
