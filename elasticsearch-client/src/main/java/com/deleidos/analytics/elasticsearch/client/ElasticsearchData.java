package com.deleidos.analytics.elasticsearch.client;

/**
 * The base data model for elasticsearch. Used as input to indexing.
 * 
 * @author vernona
 */
public class ElasticsearchData {

	private String indexName;
	private String typeName;
	private String id;
	private String jsonContent;

	/**
	 * Constructor.
	 * 
	 * @param indexName
	 * @param typeName
	 * @param id
	 * @param jsonContent
	 */
	public ElasticsearchData(String indexName, String typeName, String id, String jsonContent) {
		this.indexName = indexName;
		this.typeName = typeName;
		this.id = id;
		this.jsonContent = jsonContent;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJsonContent() {
		return jsonContent;
	}

	public void setJsonContent(String jsonContent) {
		this.jsonContent = jsonContent;
	}
}
