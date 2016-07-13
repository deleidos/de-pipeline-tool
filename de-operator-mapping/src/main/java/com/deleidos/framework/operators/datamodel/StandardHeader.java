package com.deleidos.framework.operators.datamodel;

import java.util.UUID;


import com.google.gson.JsonObject;



public class StandardHeader {
	
	public static final String HEADER_KEY        = "standardHeader";

	public static final String UUID_KEY          = "uuid";
	public static final String SOURCE_KEY        = "source";
	public static final String ACCESS_LABEL_KEY  = "accessLabel";
	public static final String MODEL_NAME_KEY    = "modelName";
	public static final String MODEL_VERSION_KEY = "modelVersion";
	
	public static final String NO_ACCESS_LABEL   = "UNLABELLED";
	public static final String NO_SOURCE         = "UNDEFINED";
	public static final String NO_MODEL_NAME     = "NOMODEL";
	public static final String NO_MODEL_VERSION  = "0.0";
	
	private JsonObject header;
	
	public StandardHeader() {
		
		UUID uuid = UUID.randomUUID();

		if (header == null) { header = new JsonObject(); }
		header.addProperty(UUID_KEY, uuid.toString());
		header.addProperty(SOURCE_KEY, NO_SOURCE);
		header.addProperty(ACCESS_LABEL_KEY, NO_ACCESS_LABEL);
		header.addProperty(MODEL_NAME_KEY, NO_MODEL_NAME);
		header.addProperty(MODEL_VERSION_KEY, NO_MODEL_VERSION);
	}
	
	public StandardHeader(JsonObject header) {
		this.header = header;
	}
	
	public void updateUUID() {
		UUID uuid = UUID.randomUUID();
		header.addProperty(UUID_KEY, uuid.toString());		
	}
	
	public String getUUID() {
		return header.get(UUID_KEY).getAsString();
	}
	
	public void setUUID(String uuid) {
		header.addProperty(UUID_KEY, uuid);
	}
	
	public void setSource(String source) {
		if (source != null) {
			header.addProperty(SOURCE_KEY, source);			
		}
	}
	
	public String getSource() {
		return header.get(SOURCE_KEY).getAsString();
	}
	
	public void setAccessLabel(String label) {
		if (label != null) {
			header.addProperty(ACCESS_LABEL_KEY, label);			
		}
	}
	
	public String getAccessLabel() {
		return header.get(ACCESS_LABEL_KEY).getAsString();
	}
	
	public void setModelName(String modelName) {
		header.addProperty(MODEL_NAME_KEY, modelName);
	}
	
	public String getModelName() {
		return header.get(MODEL_NAME_KEY).getAsString();
	}
	
	public void setModelVersion(int major, int minor) {
		header.addProperty(MODEL_VERSION_KEY, major + "." + minor);
	}
	
	public String getModelVersion() {
		return header.get(MODEL_VERSION_KEY).getAsString();
	}
	
	public JsonObject getJson() {
		return header;
	}
	
	public String toString() {
		return header.toString();
	}
}
