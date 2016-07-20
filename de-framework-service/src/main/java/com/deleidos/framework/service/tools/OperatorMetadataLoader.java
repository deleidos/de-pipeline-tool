package com.deleidos.framework.service.tools;

import java.util.List;

import com.deleidos.framework.service.data.SystemDataManager;
import com.google.gson.Gson;
import com.deleidos.framework.model.system.OperatorMetadata;

/**
 * Load operator metadata into the database.
 * 
 * @author vernona
 */
public class OperatorMetadataLoader {

	public static void main(String[] args) {
		SystemDataManager manager = SystemDataManager.getInstance();
		List<OperatorMetadata> metadata = OperatorMetadataFactory.getInstance().getOperatorMetadata();
		manager.saveOperatorMetadata(metadata);
		metadata = manager.getOperatorMetadata();
		System.out.println((new Gson()).toJson(metadata));
	}
}
