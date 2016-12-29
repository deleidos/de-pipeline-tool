package com.deleidos.framework.service.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.deleidos.framework.model.system.OperatorMetadata;
import com.deleidos.framework.model.system.OperatorMetadataList;
import com.deleidos.framework.model.system.SystemDescriptor;
import com.deleidos.framework.model.system.ValidationRule;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(SystemDataManager.class);

	private static final SystemDataManager instance = new SystemDataManager();

	private DeFrameworkDb db;
	private Gson gson = (new GsonBuilder()).setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();

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
	public OperatorMetadata getOperatorMetadata(String id) {
		FindIterable<Document> documents = db.getOperatorMetadataCollection().find(getIdFilter(id));
		OperatorMetadata metadata = gson.fromJson(documents.first().toJson(), OperatorMetadata.class);
		return metadata;
	}

	/**
	 * Save operator metadata in the database. The old data will be dropped and replaced.
	 * 
	 * @param metadata
	 */
	public void saveOperatorMetadata(List<OperatorMetadata> metadata) {
		db.getOperatorMetadataCollection().drop();
		OperatorMetadataList metadataList = new OperatorMetadataList(metadata);
		db.getOperatorMetadataCollection().insertOne(new Document(objectToMap(metadataList)));
	}

	/**
	 * Get the operator metadata from the database.
	 * 
	 * @return
	 */
	public List<OperatorMetadata> getOperatorMetadataList() {
		FindIterable<Document> documents = db.getOperatorMetadataCollection().find();
		List<OperatorMetadata> metadataList = new ArrayList<OperatorMetadata>();
		for (Document document : documents) {
			metadataList.add(gson.fromJson(document.toJson(), OperatorMetadata.class));
		}
		return metadataList;
	}

	/**
	 * Insert operator metadata.
	 * 
	 * @param metadata
	 */
	public void insertOperatorMetadata(OperatorMetadata metadata) {
		Document document = new Document(objectToMap(metadata));
		if (db.getOperatorMetadataCollection().find(new Document().append("className", metadata.getClassName()))
				.first() != null) {
			metadata.set_id(db.getOperatorMetadataCollection()
					.find(new Document().append("className", metadata.getClassName())).first().getString("_id"));
			db.getOperatorMetadataCollection().findOneAndReplace(
					new Document().append("className", metadata.getClassName()), new Document(objectToMap(metadata)));

		}
		else {
			db.getOperatorMetadataCollection().insertOne(document);
		}
	}

	/**
	 * Update operator metadata.
	 * 
	 * @param metadata
	 */
	public void updateOperatorMetadata(OperatorMetadata metadata) {
		db.getOperatorMetadataCollection().findOneAndReplace(getIdFilter(metadata.get_id()),
				new Document(objectToMap(metadata)));
	}

	/**
	 * Delete operator metadata.
	 * 
	 * @param id
	 */
	public void deleteOperatorMetadata(String id) {
		db.getOperatorMetadataCollection().deleteOne(getIdFilter(id));
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
			system = gson.fromJson(documents.first().toJson(), SystemDescriptor.class);
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

	/**
	 * Get a validation rule by ID.
	 * 
	 * @param id
	 * @return
	 */
	public ValidationRule getValidationRule(String id) {
		ValidationRule rule = null;
		FindIterable<Document> documents = db.getValidationRuleCollection().find(getIdFilter(id));
		if (documents != null && documents.first() != null) {
			rule = gson.fromJson(documents.first().toJson(), ValidationRule.class);
		}
		return rule;
	}

	/**
	 * Get all validation rules.
	 * 
	 * @return
	 */
	public List<ValidationRule> getValidationRules() {
		List<ValidationRule> validationRules = new ArrayList<ValidationRule>();
		FindIterable<Document> documents = db.getValidationRuleCollection().find();
		if (documents != null && documents.first() != null) {
			for (Document document : documents) {
				validationRules.add(gson.fromJson(document.toJson(), ValidationRule.class));
			}
		}
		return validationRules;
	}

	/**
	 * Insert a validation rule.
	 * 
	 * @param rule
	 */
	public void insertValidationRule(ValidationRule rule) {
		Document document = new Document(objectToMap(rule));
		db.getValidationRuleCollection().insertOne(document);
	}

	/**
	 * Update a validation rule.
	 * 
	 * @param rule
	 */
	public void updateValidationRule(ValidationRule rule) {
		db.getValidationRuleCollection().findOneAndReplace(getIdFilter(rule.get_id()), new Document(objectToMap(rule)));
	}

	/**
	 * Delete a validation rule.
	 * 
	 * @param id
	 */
	public void deleteValidationRule(String id) {
		db.getValidationRuleCollection().deleteOne(getIdFilter(id));
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
