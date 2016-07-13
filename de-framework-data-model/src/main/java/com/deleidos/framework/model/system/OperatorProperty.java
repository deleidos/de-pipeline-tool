package com.deleidos.framework.model.system;

import java.io.Serializable;
import java.util.List;

/**
 * Operator metadata property.
 * 
 * @author vernona
 */
@SuppressWarnings("serial")
public class OperatorProperty implements Serializable {

	public enum Type {
		String, StringList, Integer, FloatingPoint, File
	};

	private String name;
	private String displayName;
	private List<String> choices;
	private boolean required;
	private Type type;

	/**
	 * Empty no-arg constructor for serialization.
	 */
	public OperatorProperty() {}

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param displayName
	 * @param choices
	 * @param required
	 */
	public OperatorProperty(String name, String displayName, Type type, List<String> choices, boolean required) {
		this.name = name;
		this.displayName = displayName;
		this.type = type;
		this.choices = choices;
		this.required = required;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public List<String> getChoices() {
		return choices;
	}

	public void setChoices(List<String> choices) {
		this.choices = choices;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
}
