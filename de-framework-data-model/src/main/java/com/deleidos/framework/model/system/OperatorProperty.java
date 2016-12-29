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

	private String name;
	private String displayName;
	private List<String> choices;
	private boolean required;
	private String type;
	private String description;

	/**
	 * Empty no-arg constructor for serialization.
	 */
	public OperatorProperty() {
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param displayName
	 * @param choices
	 * @param required
	 */
	public OperatorProperty(String name, String displayName, String type, List<String> choices, boolean required,
			String description) {
		this.name = name;
		this.displayName = displayName;
		this.type = type;
		this.choices = choices;
		this.required = required;
		this.description = description;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
