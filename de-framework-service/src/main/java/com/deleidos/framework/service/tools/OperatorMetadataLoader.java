package com.deleidos.framework.service.tools;

import java.util.List;

import com.deleidos.framework.service.data.DeFrameworkDb;
import com.deleidos.analytics.config.AnalyticsConfig;
import com.deleidos.framework.model.system.OperatorMetadata;

/**
 * Load operator metadata into the database.
 * 
 * @author vernona
 */
public class OperatorMetadataLoader {

	public static void main(String[] args) {
		DeFrameworkDb db = new DeFrameworkDb(AnalyticsConfig.getInstance().getMongodbHostname());
		List<OperatorMetadata> metadata = OperatorMetadataFactory.getInstance().getOperatorMetadata();
		
	}
}
