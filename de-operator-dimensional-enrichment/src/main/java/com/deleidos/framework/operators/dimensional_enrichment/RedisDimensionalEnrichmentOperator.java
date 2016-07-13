package com.deleidos.framework.operators.dimensional_enrichment;

import java.lang.reflect.Type;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;

import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.common.util.BaseOperator;
import com.deleidos.analytics.redis.client.RedisClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Enrich a JSON input record by adding additional data to it. Data is pulled from a Redis cache using the given key
 * field and added to the top level of the tuple as a JSON object.
 * 
 * If any type of failure occurs, the original tuple is emitted.
 * 
 * @author vernona
 */
public class RedisDimensionalEnrichmentOperator extends BaseOperator {

	private static final Logger logger = Logger.getLogger(RedisDimensionalEnrichmentOperator.class);

	@NotNull
	protected String keyField;
	@NotNull
	protected String dataField;
	@NotNull
	protected String cacheHostname;

	private transient Gson gson = new Gson();

	/**
	 * Empty no-arg constructor for serialization.
	 */
	public RedisDimensionalEnrichmentOperator() {}

	/** Output port stream for tuple result emission. */
	public final transient DefaultOutputPort<String> output = new DefaultOutputPort<String>();

	/** Input port stream for tuple processing. */
	public final transient DefaultInputPort<String> input = new DefaultInputPort<String>() {
		@Override
		public void process(String tuple) {

			Map<String, Object> map = null;
			try {
				map = objectToMap(tuple);
				Object keyValue = map.get(keyField);
				String key = null;
				if (keyValue instanceof String) {
					key = (String) keyValue;
					key = key.replace("\"", "");
				}

				if (key != null) {
					RedisClient client = new RedisClient(cacheHostname);
					String jsonData = client.getValue(key);
					client.close();
					if (jsonData != null) {
						Map<String, Object> dataMap = objectToMap(jsonData);
						map.put(dataField, dataMap);
					}
				}
			}
			catch (Throwable t) {
				// Log the message and emit the original tuple unchanged.
				logger.error(t.getMessage(), t);
			}

			tuple = map == null ? tuple : gson.toJson(map);
			output.emit(tuple);
		}
	};

	public String getKeyField() {
		return keyField;
	}

	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}

	public String getDataField() {
		return dataField;
	}

	public void setDataField(String dataField) {
		this.dataField = dataField;
	}

	public String getCacheHostname() {
		return cacheHostname;
	}

	public void setCacheHostname(String cacheHostname) {
		this.cacheHostname = cacheHostname;
	}
	
	private Map<String, Object> objectToMap(String json) {
		Type type = new TypeToken<Map<String, Object>>() {}.getType();
		return gson.fromJson(json, type);
	}
}
