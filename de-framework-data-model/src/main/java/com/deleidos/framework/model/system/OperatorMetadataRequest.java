package com.deleidos.framework.model.system;

import java.io.Serializable;

/**
 * operator meta data + byte
 */
@SuppressWarnings("serial")
public class OperatorMetadataRequest implements Serializable {
	private OperatorMetadata metadata;
	private String bytes;

	public OperatorMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(OperatorMetadata metadata) {
		this.metadata = metadata;
	}

	public String getBytes() {
		return bytes;
	}

	public void setBytes(String bytes) {
		this.bytes = bytes;
	}
}
