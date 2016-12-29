package com.deleidos.framework.model.system;

import java.io.Serializable;
import java.util.List;

/**
 * Validation rule object.
 * 
 * @author vernona
 */
@SuppressWarnings("serial")
public class ValidationRule implements Serializable {

	private String _id;
	private String name;
	private String type;
	private boolean list;
	private String regex;
	private List<String> options;
	private String file;

	/**
	 * Empty no-arg constructor for serialization.
	 */
	public ValidationRule() {
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param type
	 * @param list
	 * @param regex
	 * @param options
	 * @param file
	 */
	public ValidationRule(String id, String name, String type, boolean list, String regex, List<String> options,
			String file) {
		_id = id;
		this.name = name;
		this.type = type;
		this.list = list;
		this.regex = regex;
		this.options = options;
		this.file = file;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean getList() {
		return list;
	}

	public void setList(boolean list) {
		this.list = list;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
}
