package com.deleidos.framework.service.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.deleidos.analytics.common.util.FileUtil;
import com.deleidos.analytics.common.util.GsonFactory;
import com.deleidos.framework.model.system.OperatorMetadata;
import com.deleidos.framework.model.system.OperatorMetadataList;
import com.deleidos.framework.model.system.OperatorProperty;
import com.google.gson.Gson;

/**
 * Operator metadata factory. Loads metadata from a JSON file.
 * 
 * @author vernona
 */
public class OperatorMetadataFactory {

	/** The singleton instance. */
	private static final OperatorMetadataFactory instance = new OperatorMetadataFactory();

	/**
	 * Private constructor enforces the singleton pattern.
	 */
	private OperatorMetadataFactory() {
	}

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static OperatorMetadataFactory getInstance() {
		return instance;
	}
	
	public List<OperatorMetadata> getOperatorMetadata() throws Exception {
		Gson gson = GsonFactory.getInstance().getGsonWithNoDeserializers();
		String json = FileUtil.getResourceFileContentsAsString("operator_metadata.json");
		OperatorMetadataList metadataList = gson.fromJson(json, OperatorMetadataList.class);
		for (OperatorMetadata metadata : metadataList.getMetadata()) {
			metadata.set_id(UUID.randomUUID().toString());
			if (metadata.getProperties() == null) {
				metadata.setProperties(new ArrayList<OperatorProperty>());
			}
		}
		return metadataList.getMetadata();
	}

}
