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

	private String _id;
	private String name;
	private String className;
	private String displayName;
	private String jarName;
	private String type;

	private List<OperatorProperty> properties;

	/**
	 * Empty no-arg constructor for serialization.
	 */
	public OperatorMetadata() {
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param className
	 * @param displayName
	 * @param properties
	 */
	public OperatorMetadata(String id, String name, String className, String displayName, String jarName, String type,
			List<OperatorProperty> properties) {
		this._id = id;
		this.name = name;
		this.className = className;
		this.displayName = displayName;
		this.jarName = jarName;
		this.properties = properties;
		this.type = type;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String get_id() {
		return _id;
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

	public String getJarName() {
		return this.jarName;
	}

	public void setJarName(String jarName) {
		this.jarName = jarName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<OperatorProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<OperatorProperty> properties) {
		this.properties = properties;
	}
}
