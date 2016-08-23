package com.deleidos.framework.service.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.deleidos.framework.model.system.OperatorMetadata;
import com.deleidos.framework.model.system.OperatorMetadataList;
import com.deleidos.framework.model.system.SystemDescriptor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.FindIterable;

/**
 * Data access object for performing operations on system data. Execution of the methods in this class assume the
 * database and collection already exist.
 * 
 * Database=Schema, Collection=Table, Document=Row, Field=Column
 * 
 * Collection is key/value pairs.
 * 
 * @author vernona
 */
public class SystemDataManager {

	private static final SystemDataManager instance = new SystemDataManager();

	private DeFrameworkDb db;
	private Gson gson = new Gson();

	/**
	 * Private constructor enforces the singleton pattern.
	 */
	private SystemDataManager() {
		db = DeFrameworkDbFactory.getInstance().getDeFrameworkDb();
	}

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static SystemDataManager getInstance() {
		return instance;
	}

	/**
	 * Get the operator metadata from the database.
	 * 
	 * @return
	 */
	public List<OperatorMetadata> getOperatorMetadata() {
		FindIterable<Document> documents = db.getOperatorMetadataCollection().find();
		Document document = documents.first();
		OperatorMetadataList metadata = gson.fromJson(document.toJson(), OperatorMetadataList.class);
		return metadata.getMetadata();
	}

	/**
	 * Save operator metadata in the database. The old data will be dropped and replaced.
	 * 
	 * @param metadata
	 */
	public void saveOperatorMetadata(List<OperatorMetadata> metadata) {
		db.getOperatorMetadataCollection().drop();
		OperatorMetadataList metadataList = new OperatorMetadataList(metadata);
		Document document = new Document(objectToMap(metadataList));
		db.getOperatorMetadataCollection().insertOne(document);
	}

	/**
	 * Get all system descriptors from the database.
	 * 
	 * @return
	 */
	public List<SystemDescriptor> getSystemDecriptors() {
		FindIterable<Document> documents = db.getSystemCollection().find();
		List<SystemDescriptor> systems = new ArrayList<SystemDescriptor>();
		if (documents != null && documents.first() != null) {
			for (Document document : documents) {
				System.out.println(document.toJson());
				systems.add(gson.fromJson(document.toJson(), SystemDescriptor.class));
			}
		}
		return systems;
	}

	/**
	 * Get a system descriptor from the database by ID.
	 * 
	 * @return
	 */
	public SystemDescriptor getSystemDecriptor(String id) {
		FindIterable<Document> documents = db.getSystemCollection().find(getIdFilter(id));
		SystemDescriptor system = null;
		if (documents != null && documents.first() != null) {
			Document document = documents.first();
			System.out.println(document.toJson());
			system = gson.fromJson(document.toJson(), SystemDescriptor.class);
		}
		return system;
	}

	/**
	 * Insert a new system descriptor into the database.
	 * 
	 * @param system
	 */
	public void insertSystemDescriptor(SystemDescriptor system) {
		Document document = new Document(objectToMap(system));
		db.getSystemCollection().insertOne(document);
	}

	/**
	 * Update an existing system descriptor in the database.
	 * 
	 * @param system
	 */
	public void updateSystemDescriptor(SystemDescriptor system) {
		db.getSystemCollection().findOneAndReplace(getIdFilter(system.get_id()), new Document(objectToMap(system)));
	}

	/**
	 * Delete a system descriptor.
	 * 
	 * @param id
	 */
	public void deleteSystemDescriptor(String id) {
		db.getSystemCollection().deleteOne(getIdFilter(id));
	}

	/**
	 * Get all enrichment namespaces from the database.
	 * 
	 * @return
	 */
	public List<String> getEnrichmentNamespaces() {
		FindIterable<Document> documents = db.getEnrichmentNamespaceCollection().find();
		Document document = documents.first();
		EnrichmentNamespaces namespaces = gson.fromJson(document.toJson(), new TypeToken<EnrichmentNamespaces>() {
		}.getType());
		
		return namespaces.getNamespaces();
	}

	/**
	 * Save enrichment namespaces in the database. The old data will be dropped and replaced.
	 * 
	 * @param metadata
	 */
	public void saveEnrichmentNamespaces(List<String> namespaces) {
		db.getEnrichmentNamespaceCollection().drop();
		Document document = new Document(objectToMap(new EnrichmentNamespaces(namespaces)));
		db.getEnrichmentNamespaceCollection().insertOne(document);
	}
	
	//
	// Protected methods:
	//

	/**
	 * Convert an Object to a key-value pair map.
	 * 
	 * @param o
	 * @return
	 */
	protected Map<String, Object> objectToMap(Object o) {
		Type type = new TypeToken<Map<String, Object>>() {
		}.getType();
		return gson.fromJson(gson.toJson(o), type);
	}

	//
	// Private methods:
	//

	private Document getIdFilter(String id) {
		return new Document().append("_id", id);
	}
}
