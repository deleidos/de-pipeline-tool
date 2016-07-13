package com.deleidos.framework.model.system;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Apache Apex system definition object.
 * 
 * @author vernona
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemDescriptor implements Serializable {
	/** The primary key for the system document in the database. */
	private String _id;
	/** The application name. This field can be used to name the APA file. */
	private String name;
	/** The system descriptor, which is intended to be serialized to JSON in the APA file. */
	private ApplicationDescriptor application;
	/** The operator mapping JSON, indexed by JsonMappingOperator name (which will be unique within the system). */
	private Map<String, String> mappings;
	/** Frontend-specific state data. */
	private String state;

	public SystemDescriptor() {}

	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param appName
	 * @param applicationDescriptor
	 * @param mappings
	 */
	public SystemDescriptor(String id, String appName, ApplicationDescriptor applicationDescriptor,
			Map<String, String> mappings, String state) {
		this._id = id;
		this.name = appName;
		this.application = applicationDescriptor;
		this.mappings = mappings;
	}

	@JsonProperty("_id")
	public String get_id() {
		return _id;
	}

	@JsonProperty("_id")
	public void set_id(String _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ApplicationDescriptor getApplication() {
		return application;
	}

	public void setApplication(ApplicationDescriptor application) {
		this.application = application;
	}

	public Map<String, String> getMappings() {
		return mappings;
	}

	public void setMappings(Map<String, String> mappings) {
		this.mappings = mappings;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
