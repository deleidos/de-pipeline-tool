package com.deleidos.framework.model.system;

import java.io.Serializable;
import java.util.Base64;

/**
 * Operator property file upload.
 * 
 * @author vernona
 */
@SuppressWarnings("serial")
public class OperatorFile implements Serializable {
	private String filename;
	private String filenameField;
	private byte[] bytes;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilenameField() {
		return filenameField;
	}

	public void setFilenameField(String filenameField) {
		this.filenameField = filenameField;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(String byteArr) {
		this.bytes = Base64.getDecoder().decode(byteArr);
	}
}
