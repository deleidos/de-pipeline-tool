package com.deleidos.framework.operators.elasticsearch;



import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.apache.log4j.Logger;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public enum IndexUtil {

	instance;

	private LRUMap mappingCache = new LRUMap();
	private Logger log = Logger.getLogger(getClass());
	
	/**
	 * Creates an ElasticSearch Index with the given Mappings and Settings
	 * @param indexName the name of the index to create
	 * @param mappings a Map of <type name, name of file containing mapping for type (file must be on classpath)>
	 * @param settingsFile name of a file containing the settings to be applied to the index (file must be on classpath)
	 */
	public synchronized void createIndex(String indexName, Map<String,String> mappings, String settingsFile, Client client) {
		try {
			if (mappingCache.get(indexName) == null) {

				IndicesExistsRequest indexExistsRequest = new IndicesExistsRequest(indexName);
				ActionFuture<IndicesExistsResponse> future = client.admin().indices().exists(indexExistsRequest);
				IndicesExistsResponse indexExistsResponse = future.actionGet();

				if (!indexExistsResponse.isExists()) {
					String settings = getFileAsString(settingsFile);
					
					//logger.info(String.format("Creating index {%s} with settings: %s and mappings: %s", indexName,
						//	settings, mappings));

					CreateIndexRequestBuilder builder = client.admin().indices().prepareCreate(indexName);
					if (settings != null) {
						builder = builder.setSettings(settings);
					}
					if (mappings != null) {
						for (Map.Entry<String, String> entry : mappings.entrySet()) {
						    String mappingKey = entry.getKey();
						    String mappingFile = entry.getValue();
						    String mappingJson = getFileAsString(mappingFile);
						    builder = builder.addMapping(mappingKey, mappingJson);
						}
					}
					builder.execute().actionGet();
				}
			}
		} catch (ElasticsearchException e) {
			log.error(e);
		} finally {
			mappingCache.put(indexName, "true");
		}
	}
	//take out ensure
	
	private String getFileAsString(String fileName) {
		if (fileName == null || fileName.trim().isEmpty()) {
			return null;
		} else {
			try {
				URL url = Resources.getResource(fileName);
				return Resources.toString(url, Charsets.UTF_8);
			} catch (Exception e) {
				log.error("Setting/Mapping file: " + fileName + " not found!", e);
				return null;
			}
		}
	}
}
