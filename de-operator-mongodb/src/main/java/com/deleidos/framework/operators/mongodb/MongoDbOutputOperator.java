package com.deleidos.framework.operators.mongodb;

import com.datatorrent.common.util.BaseOperator;
import com.datatorrent.api.Context;

import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;

import com.datatorrent.api.DefaultInputPort;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.*;
import com.mongodb.util.JSON;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.deleidos.framework.operators.common.OperatorConfig;
import com.deleidos.framework.operators.common.OperatorSyslogger;
import com.deleidos.framework.operators.common.OperatorSystemInfo;
import com.deleidos.framework.operators.common.TupleUtil;

/**
 * Operator to write data into MongoDB
 *
 * @since 0.9.0
 */
public class MongoDbOutputOperator extends BaseOperator implements OperatorSystemInfo {

	private static final Logger log = Logger.getLogger(MongoDbOutputOperator.class);

	@NotNull
	protected String hostName;
	@NotNull
	protected int hostPort;
	@NotNull
	protected String database;

	protected String userName;
	protected String password;

	private String systemName;

	protected String modelToIndexMapping;
	protected String collection;
	protected WriteConcern writeConcern = WriteConcern.ACKNOWLEDGED;

	protected transient MongoClient mongoClient;
	protected transient DB db;
	protected transient Map<String, DBCollection> dbCollections;
	protected Map<String, List<Map<String, Object>>> indexMappings;

	protected Map<String, List<DBObject>> dataLists = new HashMap<String, List<DBObject>>();
	private transient OperatorSyslogger syslog;
	public MongoDbOutputOperator() {

	}

	/**
	 * Take the JSON object and convert it to DBObject
	 */
	public static JsonElement getNestedJsonElement(JsonObject jsonObject, String dotNotationFieldName) {
		return getNestedJsonElement(jsonObject, dotNotationFieldName.split("\\."), 0);
	}

	public static JsonElement getNestedJsonElement(JsonObject jsonObject, String[] dotNotationFieldNames, int level) {
		JsonElement nestedElement = jsonObject.get(dotNotationFieldNames[level]);
		if (nestedElement != null) {
			if ((level + 1) < dotNotationFieldNames.length) {
				// There is still more nesting to process

				// Make sure the element is an object and therefore able to be
				// processed
				if (nestedElement.isJsonObject()) {
					nestedElement = getNestedJsonElement(nestedElement.getAsJsonObject(), dotNotationFieldNames,
							level + 1);
				} else {
					// There is no next level of nesting
					nestedElement = null;
				}
			}
		}

		return nestedElement;
	}

	public transient final DefaultInputPort<Map<String, Object>> input = new DefaultInputPort<Map<String, Object>>() {
		@Override
		public void process(Map<String, Object> tuple) {
			try {
				String jsonString = TupleUtil.tupleMapToJson(tuple);
				String collectionName = getCollection();
				if (!dataLists.containsKey(collectionName)) {
					dataLists.put(collectionName, new ArrayList<DBObject>());
				}
				List<DBObject> dataList = dataLists.get(collectionName);
			
				dataList.add((DBObject) JSON.parse(jsonString));
			} catch (Exception e) {
				syslog.error("Error in Mongo Output: " + e.getMessage(), e);
			}

		}
	};

	@Override
	public void setup(Context.OperatorContext context) {
		syslog = new OperatorSyslogger(systemName, OperatorConfig.getInstance().getSyslogUdpHostname(),
				OperatorConfig.getInstance().getSyslogUdpPort());
		super.setup(context);
		try {
			mongoClient = new MongoClient(hostName, hostPort);
			db = mongoClient.getDB(database);
			if (userName != null && password != null) {
				if (!db.authenticate(userName, password.toCharArray())) {
					throw new IllegalArgumentException(
							"MongoDB authentication failed. Illegal username and password for MongoDB!!");
				}
			}

			dbCollections = new HashMap<String, DBCollection>();

			if (modelToIndexMapping != null) {
				indexMappings = parseModelToIndexMapping(modelToIndexMapping);
			} else {
				indexMappings = new HashMap<String, List<Map<String, Object>>>();
			}
		} catch (UnknownHostException e) {
			syslog.error("Error in Mongo Output: " + e.getMessage(), e);
			log.error("Error in Mongo Output: " + e.getMessage(), e);
		}
	}

	@Override
	public void beginWindow(long windowId) {

		// nothing
	}

	@Override
	public void endWindow() {
		try{
		for (Map.Entry<String, List<DBObject>> entry : dataLists.entrySet()) {
			String collectionName = entry.getKey();
			List<DBObject> dataList = entry.getValue();

			DBCollection dbCollection = getDbCollection(collectionName);

			if (dataList.size() > 0) {
				dbCollection.insert(dataList, writeConcern);
				dataList.clear();
			}
		}
		}catch(Exception e){
			syslog.error("Error in Mongo Output: " + e.getMessage() , e);

		}
	}

	@Override
	public void teardown() {

		if (mongoClient != null) {
			mongoClient.close();
		}
	}

	private DBCollection getDbCollection(String collectionName) {
		
		DBCollection dbCollection = dbCollections.get(collectionName);

		if (dbCollection == null) {
			dbCollection = db.getCollection(collectionName);

			// Add the user defined indices to the collection
			List<Map<String, Object>> indices = indexMappings.get(collectionName);
			if (indices != null) {
				for (Map<String, Object> index : indices) {
					dbCollection.createIndex(new BasicDBObject(index));
				}
			}

			dbCollections.put(collectionName, dbCollection);
		}

		return dbCollection;
		
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getHostPort() {
		return hostPort;
	}

	public void setHostPort(int hostPort) {
		boolean invalidPort = false;
		try {
			this.hostPort = hostPort;
		} catch (NumberFormatException e) {
			syslog.error("Error in Mongo Output: " + e.getMessage() , e);

			log.error(String.format("Failed to parse host port value [%s].", hostPort), e);
		}

		if (this.hostPort < 0 || this.hostPort > 65535) {
			syslog.error("Error in Mongo Output: " + String.format("Invalid port value [%d]. Port must be in the range of 0-65535.", this.hostPort) );

			log.error(String.format("Invalid port value [%d]. Port must be in the range of 0-65535.", this.hostPort));
		}

		if (invalidPort) {
			log.warn("Using MongoDB default port of 27017.");
			this.hostPort = 27017;
		}
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getModelToIndexMapping() {
		return modelToIndexMapping;
	}

	public void setModelToIndexMapping(String modelToIndexMapping) {
		this.modelToIndexMapping = modelToIndexMapping;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public String getCollection() {
		return this.collection;
	}

	@Override
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	@Override
	public String getSystemName() {
		return systemName;
	}

	/**
	 * Create a mapping from data model to fields to be indexed when the MongoDB
	 * collection for that data model is created.
	 * 
	 * The modelIndexMappingDefinition should be in the format: model1Name =
	 * indexField1, compoundIndexField1-compoundIndexField2, ..., indexFieldN;
	 * ... ; modelXName = indexFieldY, ..., indexFieldZ
	 *
	 * Models can have single field indices and compound indices. To define a
	 * compound index, separate the fields of the compound index with '-'. The
	 * order of indexing the compound index will be based on the order of the
	 * components. White space is ignored.
	 * 
	 * @param modelToIndexMappingDefinition
	 *            A mapping definition from data model to fields to be indexed
	 * @return A mapping from data model to the fields of that data model to be
	 *         indexed
	 */
	private Map<String, List<Map<String, Object>>> parseModelToIndexMapping(String modelToIndexMappingDefinition) {
		Map<String, List<Map<String, Object>>> indexMapping = new HashMap<String, List<Map<String, Object>>>();

		if (!modelToIndexMappingDefinition.isEmpty()) {
			// Split on ';' to obtain the model to index field mapping
			// definitions
			for (String modelToIndexFieldMappingDefinition : modelToIndexMappingDefinition.split(";")) {
				// Split on '=' to obtain the modelName and index fields
				String[] modelNameAndIndices = modelToIndexFieldMappingDefinition.trim().split("=");

				// Split the index fields on ',' to obtain the indices
				List<Map<String, Object>> indices = new ArrayList<Map<String, Object>>();
				for (String index : modelNameAndIndices[1].split(",")) {
					Map<String, Object> indexFieldMap = new LinkedHashMap<String, Object>();

					// Split on '-' to handle compound indices
					for (String indexField : index.split("-")) {
						indexFieldMap.put(indexField.trim(), 1);
					}

					indices.add(indexFieldMap);
				}

				// Save the indices for the model
				indexMapping.put(modelNameAndIndices[0].trim(), indices);
			}
		}

		return indexMapping;
	}
}