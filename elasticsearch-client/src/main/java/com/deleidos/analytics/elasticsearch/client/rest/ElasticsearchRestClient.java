package com.deleidos.analytics.elasticsearch.client.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.deleidos.analytics.common.rest.RestClient;
import com.deleidos.analytics.common.util.GsonFactory;
import com.deleidos.analytics.elasticsearch.client.config.ElasticsearchClientConfig;
import com.deleidos.analytics.elasticsearch.client.rest.response.DeleteIndexResponse;
import com.deleidos.analytics.elasticsearch.client.rest.response.DeleteResponse;
import com.deleidos.analytics.elasticsearch.client.rest.response.GetResponse;
import com.deleidos.analytics.elasticsearch.client.rest.response.Hit;
import com.deleidos.analytics.elasticsearch.client.rest.response.PutResponse;
import com.deleidos.analytics.elasticsearch.client.rest.response.SearchResponse;
import com.google.gson.Gson;

/**
 * Elasticsearch REST-based client.
 * 
 * Use the ElasticsearchRequestBuilder to form valid JSON requests.
 * 
 * @author vernona
 */
public class ElasticsearchRestClient {

	private Logger logger = Logger.getLogger(ElasticsearchRestClient.class);

	protected TransportClient client;
	protected ElasticsearchClientConfig config;
	protected RestClient restClient;
	protected Gson gson = GsonFactory.getInstance().getGsonWithNoDeserializers();

	/**
	 * Constructor.
	 * 
	 * @param config
	 */
	public ElasticsearchRestClient(ElasticsearchClientConfig config) {
		this.config = config;
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", config.getClusterName()).build();
		client = new TransportClient(settings);
		for (String hostname : config.getClusterHostnames()) {
			client.addTransportAddress(new InetSocketTransportAddress(hostname, config.getPort()));
		}

		restClient = new RestClient("http://" + config.getClusterHostnames()[0] + ":9200");
	}

	/**
	 * Index a single document into Elasticsearch.
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public boolean indexDocument(String index, String type, String id, String document) throws Exception {
		String result = restClient.put(buildUriPath(index, type, id), document);
		logger.debug("indexDocument result=" + result);

		PutResponse response = gson.fromJson(result, PutResponse.class);
		return response.getCreated();
	}

	/**
	 * Delete a single document from Elasticsearch.
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean deleteDocument(String index, String type, String id) throws Exception {
		String result = restClient.delete(buildUriPath(index, type, id));
		logger.debug("deleteDocument result=" + result);

		boolean found = false;
		if (result != null) {
			DeleteResponse response = gson.fromJson(result, DeleteResponse.class);
			found = response.getFound();
		}
		return found;
	}

	/**
	 * Get a single document from Elasticsearch by ID.
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getSingleDocument(String index, String type, String id) throws Exception {
		String result = restClient.get(buildUriPath(index, type, id));
		logger.debug("getSingleDocument result=" + result);
		Map<String, Object> map = null;
		if (result != null) {
			GetResponse response = gson.fromJson(result, GetResponse.class);
			map = response.get_source();
		}
		return map;
	}

	/**
	 * Get all documents in the index.
	 * 
	 * @param index
	 * @return
	 */
	public List<Map<String, Object>> getAllDocuments(String index) throws Exception {
		String result = restClient.get(buildSearchUriPath(index));
		logger.debug("getAllDocuments result=" + result);

		SearchResponse response = gson.fromJson(result, SearchResponse.class);
		List<Map<String, Object>> documents = new ArrayList<Map<String, Object>>();
		if (response.getHits() != null && response.getHits().getHits() != null) {
			for (Hit hit : response.getHits().getHits()) {
				documents.add(hit.get_source());
			}
		}
		return documents;
	}

	/**
	 * Submit a search request and return the resulting documents. Use this method for complex queries with match and
	 * filter criteria.
	 * 
	 * @param index
	 * @param searchRequest
	 * @return
	 */
	public List<Map<String, Object>> search(String index, String searchRequest) throws Exception {
		String result = restClient.post(buildSearchUriPath(index), searchRequest);
		logger.debug("search result=" + result);

		SearchResponse response = gson.fromJson(result, SearchResponse.class);
		List<Map<String, Object>> documents = new ArrayList<Map<String, Object>>();
		if (response.getHits() != null && response.getHits().getHits() != null) {
			for (Hit hit : response.getHits().getHits()) {
				documents.add(hit.get_source());
			}
		}
		return documents;
	}

	/**
	 * Delete an entire index from Elasticsearch.
	 * 
	 * @param index
	 * @return
	 * @throws Exception
	 */
	public boolean deleteIndex(String index) throws Exception {
		String result = restClient.delete(buildDeleteIndexUriPath(index));
		logger.debug("deleteIndex result=" + result);

		boolean acknowledged = false;
		if (result != null) {
			DeleteIndexResponse response = gson.fromJson(result, DeleteIndexResponse.class);
			acknowledged = response.getAcknowledged();
		}
		return acknowledged;
	}

	//
	// Private methods:
	//

	/**
	 * Build the URI path for GET, PUT and DELETE requests.
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	private String buildUriPath(String index, String type, String id) {
		return "/" + index + "/" + type + "/" + id;
	}

	/**
	 * Build the search URI path.
	 * 
	 * @param index
	 * @return
	 */
	private String buildSearchUriPath(String index) {
		return "/" + index + "/_search";
	}

	/**
	 * Build the URI path for deleting an index (the entire index - use with caution).
	 * 
	 * @param index
	 * @return
	 */
	private String buildDeleteIndexUriPath(String index) {
		return "/" + index;
	}
}
