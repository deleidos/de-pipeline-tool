package com.deleidos.framework.operators.dimensional_enrichment;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;

import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.common.util.BaseOperator;
import com.deleidos.analytics.common.util.JsonToMapUtil;
import com.deleidos.analytics.redis.client.RedisClient;

/**
 * Enrich a JSON input record by adding additional data to it. Data is pulled from a Redis cache using the given key
 * field and added to the top level of the tuple as a JSON object.
 * 
 * If any type of failure occurs, the original tuple is emitted.
 * 
 * @author vernona
 */
public class FutureRedisDimensionalEnrichmentOperator extends BaseOperator {

	private static final Logger logger = Logger.getLogger(FutureRedisDimensionalEnrichmentOperator.class);

	@NotNull
	protected String keyField;
	@NotNull
	protected String dataField;
	@NotNull
	protected String cacheHostname;

	/**
	 * Empty no-arg constructor for serialization.
	 */
	public FutureRedisDimensionalEnrichmentOperator() {}

	/** Output port stream for tuple result emission. */
	public final transient DefaultOutputPort<Map<String, String>> output = new DefaultOutputPort<Map<String, String>>();

	/** Input port stream for tuple processing. */
	public final transient DefaultInputPort<Map<String, String>> input = new DefaultInputPort<Map<String, String>>() {
		@Override
		public void process(Map<String, String> tuple) {

			try {
				String keyValue = ((String) tuple.get(keyField)).replace("\"", "");
				if (keyValue != null) {
					RedisClient client = new RedisClient(cacheHostname);
					String jsonData = client.getValue(keyValue);
					if (jsonData != null) {
						JsonToMapUtil.loadJSONFields(tuple, jsonData, dataField);
					}
				}
			}
			catch (Throwable t) {
				// Log the message and emit the original tuple unchanged.
				logger.error(t.getMessage(), t);
			}

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
}
