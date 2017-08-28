package com.deleidos.framework.service.tools;

import java.util.List;

import com.deleidos.analytics.common.util.GsonFactory;
import com.deleidos.framework.model.system.OperatorMetadata;
import com.deleidos.framework.service.data.SystemDataManager;

public class OperatorMetadataLoader {

	public static void main(String[] args) throws Exception {
		System.out.println(GsonFactory.getInstance().getGsonWithNoDeserializers()
				.toJson(OperatorMetadataFactory.getInstance().getOperatorMetadata()));

		SystemDataManager manager = SystemDataManager.getInstance();
		manager.saveOperatorMetadata(OperatorMetadataFactory.getInstance().getOperatorMetadata());
		List<OperatorMetadata> metadata = manager.getOperatorMetadataList();
		System.out.println(GsonFactory.getInstance().getGsonWithCollectionDeserializers().toJson(metadata));
	}
}
