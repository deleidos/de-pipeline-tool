package com.deleidos.framework.service.data;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * DE Framework database object.
 * 
 * @author vernona
 */
public class DeFrameworkDb {

	private final MongoClient client;
	private final MongoDatabase db;
	private final MongoCollection<Document> systemCollection;
	private final MongoCollection<Document> operatorMetadataCollection;
	private final MongoCollection<Document> enrichmentNamespaceCollection;
	private final MongoCollection<Document> validationRuleCollection;

	private static final String databaseName = "de_framework_db";
	private static final String systemCollectionName = "system";
	private static final String operatorMetadataCollectionName = "op_metadata";
	private static final String enrichmentNamespaceCollectionName = "enrichment_namespace";
	private static final String validationRuleCollectionName = "validation_rules";

	/**
	 * Constructor.
	 * 
	 * @param hostname
	 */
	public DeFrameworkDb(String hostname) {
		client = new MongoClient(hostname);
		db = client.getDatabase(databaseName);
		systemCollection = db.getCollection(systemCollectionName);
		operatorMetadataCollection = db.getCollection(operatorMetadataCollectionName);
		enrichmentNamespaceCollection = db.getCollection(enrichmentNamespaceCollectionName);
		validationRuleCollection = db.getCollection(validationRuleCollectionName);
	}

	/**
	 * Get the MongoClient for this database.
	 * 
	 * @return
	 */
	public MongoClient getClient() {
		return client;
	}

	/**
	 * Get the MongoDatabase.
	 * 
	 * @return
	 */
	public MongoDatabase getDb() {
		return db;
	}

	/**
	 * Get the system collection.
	 * 
	 * @return
	 */
	public MongoCollection<Document> getSystemCollection() {
		return systemCollection;
	}

	/**
	 * Get the operator metadata collection.
	 * 
	 * @return
	 */
	public MongoCollection<Document> getOperatorMetadataCollection() {
		return operatorMetadataCollection;
	}

	/**
	 * Get the enrichment namespace collection.
	 * 
	 * @return
	 */
	public MongoCollection<Document> getEnrichmentNamespaceCollection() {
		return enrichmentNamespaceCollection;
	}

	/**
	 * Get the validation rule collection.
	 * 
	 * @return
	 */
	public MongoCollection<Document> getValidationRuleCollection() {
		return validationRuleCollection;
	}
}
