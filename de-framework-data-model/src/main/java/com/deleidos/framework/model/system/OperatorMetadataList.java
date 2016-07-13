package com.deleidos.framework.model.system;

import java.io.Serializable;
import java.util.List;

/**
 * Operator metadata list object wrapper for MongoDB persistence.
 * 
 * @author vernona
 */
@SuppressWarnings("serial")
public class OperatorMetadataList implements Serializable {

	private List<OperatorMetadata> metadata;

	/**
	 * Public no-arg constructor needed for serialization.
	 */
	public OperatorMetadataList() {}

	/**
	 * Constructor.
	 * 
	 * @param metadata
	 */
	public OperatorMetadataList(List<OperatorMetadata> metadata) {
		this.metadata = metadata;
	}

	public List<OperatorMetadata> getMetadata() {
		return metadata;
	}

	public void setMetadata(List<OperatorMetadata> metadata) {
		this.metadata = metadata;
	}
}
