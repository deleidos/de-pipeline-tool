package com.deleidos.framework.service.tools;

import java.util.ArrayList;
import java.util.List;

import com.deleidos.framework.model.system.OperatorMetadata;
import com.deleidos.framework.model.system.OperatorProperty;

/**
 * Apex operator metadata factory.
 * 
 * @author vernona
 */
public class OperatorMetadataFactory {

	/** The singleton instance. */
	private static final OperatorMetadataFactory instance = new OperatorMetadataFactory();

	private static final List<OperatorMetadata> metadata = new ArrayList<OperatorMetadata>();

	static {
		metadata.add(getS3InputOperatorMetadata());
		metadata.add(getCsvParserOperatorMetadata());
		metadata.add(getElasticSearchOutputOperatorMetadata());
		metadata.add(getJsonParserOperatorMetadata());
		metadata.add(getJSONMappingOperatorMetadata());
		metadata.add(getMongoDbOutputOperatorMetadata());
		metadata.add(getDimensionalEnrichmentOperatorMetadata());
	}

	/**
	 * Private no-arg constructor enforces the singleton pattern.f
	 */
	private OperatorMetadataFactory() {}

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static OperatorMetadataFactory getInstance() {
		return instance;
	}

	public List<OperatorMetadata> getOperatorMetadata() {
		return metadata;
	}

	//
	// Private methods:
	//

	private static OperatorMetadata getS3InputOperatorMetadata() {
		List<OperatorProperty> properties = new ArrayList<OperatorProperty>();
		properties.add(new OperatorProperty("bucketName", "Bucket Name", OperatorProperty.Type.String, null, true));
		properties.add(new OperatorProperty("path", "Path", OperatorProperty.Type.String, null, true));
		properties.add(new OperatorProperty("accessKey", "Access Key", OperatorProperty.Type.String, null, true));
		properties.add(new OperatorProperty("secretKey", "Secret Key", OperatorProperty.Type.String, null, true));
		properties.add(new OperatorProperty("endpoint", "Endpoint", OperatorProperty.Type.String, null, false));
		List<String> splitterChoices = new ArrayList<String>();
		splitterChoices.add("Line");
		splitterChoices.add("JSON");
		properties
				.add(new OperatorProperty("splitter", "Splitter", OperatorProperty.Type.String, splitterChoices, true));
		properties
				.add(new OperatorProperty("headerRows", "Header Row Count", OperatorProperty.Type.Integer, null, true));
		return new OperatorMetadata("S3InputOperator", "com.deleidos.framework.operators.s3.S3InputOperator",
				"S3 Input", properties);
	}

	private static OperatorMetadata getCsvParserOperatorMetadata() {
		List<OperatorProperty> properties = new ArrayList<OperatorProperty>();
		properties.add(new OperatorProperty("delimiter", "Delimiter", OperatorProperty.Type.String, null, true));
		return new OperatorMetadata("CsvParserOperator",
				"com.deleidos.framework.operators.csv.parser.CsvParserOperator", "CSV Parser", properties);
	}

	private static OperatorMetadata getElasticSearchOutputOperatorMetadata() {
		List<OperatorProperty> properties = new ArrayList<OperatorProperty>();
		properties.add(new OperatorProperty("idField", "ID Field", OperatorProperty.Type.String, null, true));
		properties.add(new OperatorProperty("indexName", "Index Name", OperatorProperty.Type.String, null, true));
		properties.add(new OperatorProperty("type", "Type", OperatorProperty.Type.String, null, true));
		properties.add(new OperatorProperty("batchSize", "Batch Size", OperatorProperty.Type.Integer, null, false));
		properties.add(new OperatorProperty("clusterName", "Cluster Name", OperatorProperty.Type.String, null, true));
		properties.add(new OperatorProperty("clusterHostnames", "Cluster Host Names", OperatorProperty.Type.StringList,
				null, true));
		return new OperatorMetadata("ElasticSearchOutputOperator",
				"com.deleidos.framework.operators.elasticsearch.ElasticSearchOutputJsonOperator",
				"Elasticsearch Output", properties);
	}

	private static OperatorMetadata getJsonParserOperatorMetadata() {
		return new OperatorMetadata("JsonParserOperator",
				"com.deleidos.framework.operators.json.parser.JsonParserOperator", "JSON Parser", null);
	}

	private static OperatorMetadata getJSONMappingOperatorMetadata() {
		List<OperatorProperty> properties = new ArrayList<OperatorProperty>();
		properties.add(new OperatorProperty("modelName", "Model Name", OperatorProperty.Type.String, null, true));
		properties.add(new OperatorProperty("modelVersion", "Model Version", OperatorProperty.Type.String, null, true));
		properties.add(new OperatorProperty("mappingFile", "Mapping File", OperatorProperty.Type.File, null, true));
		return new OperatorMetadata("JSONMappingOperator",
				"com.deleidos.framework.operators.mapping.JSONMappingOperator", "JSON Mapping", properties);
	}

	private static OperatorMetadata getMongoDbOutputOperatorMetadata() {
		List<OperatorProperty> properties = new ArrayList<OperatorProperty>();
		properties.add(new OperatorProperty("hostName", "Hostname", OperatorProperty.Type.String, null, true));
		properties.add(new OperatorProperty("hostPort", "Port", OperatorProperty.Type.String, null, true));
		properties.add(new OperatorProperty("database", "Database Name", OperatorProperty.Type.String, null, true));
		properties.add(new OperatorProperty("userName", "Username", OperatorProperty.Type.String, null, false));
		properties.add(new OperatorProperty("password", "Password", OperatorProperty.Type.String, null, false));
		return new OperatorMetadata("MongoDbOutputOperator",
				"com.deleidos.framework.operators.mongodb.MongoDbOutputOperator", "MongoDB Output", properties);
	}

	private static OperatorMetadata getDimensionalEnrichmentOperatorMetadata() {
		List<OperatorProperty> properties = new ArrayList<OperatorProperty>();
		properties.add(new OperatorProperty("keyField", "Key Field", OperatorProperty.Type.String, null, true));
		properties.add(new OperatorProperty("dataField", "Data Field", OperatorProperty.Type.String, null, true));
		properties.add(new OperatorProperty("cacheHostname", "Redis Cache Hostname", OperatorProperty.Type.String, null,
				true));
		return new OperatorMetadata("DimensionalEnrichmentOperator",
				"com.deleidos.framework.operators.dimensional_enrichment.RedisDimensionalEnrichmentOperator",
				"Dimensional Enrichment", properties);
	}
}
