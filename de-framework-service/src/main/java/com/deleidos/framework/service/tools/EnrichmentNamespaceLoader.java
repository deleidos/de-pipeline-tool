package com.deleidos.framework.service.tools;

import java.util.List;

import com.deleidos.analytics.common.util.GsonFactory;
import com.deleidos.framework.service.data.SystemDataManager;

/**
 * Load enrichment namespace data into MongoDB.
 * 
 * @author vernona
 */
public class EnrichmentNamespaceLoader {

	public static void main(String[] args) {
		SystemDataManager manager = SystemDataManager.getInstance();
		List<String> namespaces = EnrichmentNamespaceFactory.getInstance().getNamespaces();
		manager.saveEnrichmentNamespaces(namespaces);
		namespaces = manager.getEnrichmentNamespaces();
		System.out.println(GsonFactory.getInstance().getGsonWithCollectionDeserializers().toJson(namespaces));
	}
}
