package com.deleidos.framework.model.system;

import java.io.Serializable;
import java.util.List;

/**
 * Apex operator metadata object.
 * 
 * @author vernona
 */
@SuppressWarnings("serial")
public class OperatorMetadata implements Serializable {

	private String name;
	private String className;
	private String displayName;
	private List<OperatorProperty> properties;

	/**
	 * Empty no-arg constructor for serialization.
	 */
	public OperatorMetadata() {}

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param className
	 * @param displayName
	 * @param properties
	 */
	public OperatorMetadata(String name, String className, String displayName, List<OperatorProperty> properties) {
		this.name = name;
		this.className = className;
		this.displayName = displayName;
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<OperatorProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<OperatorProperty> properties) {
		this.properties = properties;
	}
}
