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
	private final MongoCollection<Document> operatorMetadataCollecti;

	private static final String databaseName = "de_framework_db";
	private static final String systemCollectionName = "system";
	private static final String operatorMetadataCollectionName = "op_metadata";

	/**
	 * Constructor.
	 * 
	 * @param hostname
	 */
	public DeFrameworkDb(String hostname) {
		client = new MongoClient(hostname);
		db = client.getDatabase(databaseName);
		systemCollection = db.getCollection(systemCollectionName);
		operatorMetadataCollecti = db.getCollection(operatorMetadataCollectionName);
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
		return operatorMetadataCollecti;
	}
}
