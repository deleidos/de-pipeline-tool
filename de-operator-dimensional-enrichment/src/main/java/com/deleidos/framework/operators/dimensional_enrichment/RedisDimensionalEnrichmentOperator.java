package com.deleidos.framework.operators.dimensional_enrichment;

import java.util.Map;


import org.apache.log4j.Logger;

import com.datatorrent.api.Context;
import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.common.util.BaseOperator;
import com.deleidos.analytics.redis.client.RedisClient;
import com.deleidos.framework.operators.common.KeyFieldValueFinder;
import com.deleidos.framework.operators.common.TupleUtil;

/**
 * Enrich a JSON input record by adding additional data to it. Data is pulled from a Redis cache using the given key
 * field and added to the top level of the tuple as a JSON object.
 * 
 * If any type of failure occurs, the original tuple is emitted.
 * 
 * @author vernona
 */
public class RedisDimensionalEnrichmentOperator extends BaseOperator {

	private static final Logger log = Logger.getLogger(RedisDimensionalEnrichmentOperator.class);

	//@NotNull
	protected String namespace;
	//@NotNull
	protected String keyField;
	//@NotNull
	protected String dataField;
	protected String parentDataField;
	//@NotNull
	protected String cacheHostname;

	private transient KeyFieldValueFinder finder = null;
	private transient RedisClient client = null;

	/**
	 * Empty no-arg constructor for serialization.
	 */
	public RedisDimensionalEnrichmentOperator() {
	}

	@Override
	public void setup(Context.OperatorContext context) {
		finder = new KeyFieldValueFinder();
		client = new RedisClient(cacheHostname);
	}

	/** Output port stream for tuple result emission. */
	public final transient DefaultOutputPort<Map<String, Object>> output = new DefaultOutputPort<Map<String, Object>>();

	/** Input port stream for tuple processing. */
	public final transient DefaultInputPort<Map<String, Object>> input = new DefaultInputPort<Map<String, Object>>() {
		@Override
		public void process(Map<String, Object> tuple) {
			try {
				Object value = finder.findValue(keyField, tuple);

				if (value != null) {
					String keyValue = value.toString();
					String jsonData = client.getValue(namespace, keyValue);
					if (jsonData != null) {
						Map<String, Object> dataMap = TupleUtil.jsonToTupleMap(jsonData);
						if (parentDataField == null) {
							tuple.put(dataField, dataMap);
						}
						else {
							Object parentObject = finder.findValue(parentDataField, tuple);
							if (parentObject != null && parentObject instanceof Map) {
								@SuppressWarnings("unchecked")
								Map<String, Object> parentObjectMap = (Map<String, Object>) parentObject;
								parentObjectMap.put(dataField, dataMap);
							}
						}
					}
				}
			}
			catch (Throwable t) {
				// Log the message and emit the original tuple unchanged.
				log.error(t.getMessage(), t);
			}

			output.emit(tuple);
		}
	};

	@Override
	public void teardown() {
		if (client != null) {
			client.close();
		}
	}

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

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getParentDataField() {
		return parentDataField;
	}

	public void setParentDataField(String parentDataField) {
		this.parentDataField = parentDataField;
	}

	public String getCacheHostname() {
		return cacheHostname;
	}

	public void setCacheHostname(String cacheHostname) {
		this.cacheHostname = cacheHostname;
	}
}
