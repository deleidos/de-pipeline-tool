package com.deleidos.framework.operators.json.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.datatorrent.api.AutoMetric;
import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.api.Context.OperatorContext;
import com.datatorrent.common.util.BaseOperator;
import com.deleidos.framework.operators.common.InputTuple;
import com.deleidos.framework.operators.common.OperatorConfig;
import com.deleidos.framework.operators.common.OperatorSyslogger;
import com.deleidos.framework.operators.common.OperatorSystemInfo;
import com.deleidos.framework.operators.common.TupleUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonParserOperator extends BaseOperator implements OperatorSystemInfo {

	private static final Logger log = Logger.getLogger(JsonParserOperator.class);

	/** Representative characters used in JSON */
	private final String OPEN_ARRAY_CHARACTER = "[";
	private final String CLOSE_ARRAY_CHARACTER = "]";
	private final String OBJECT_SEPERATOR = ".";

	private String systemName;
	private transient OperatorSyslogger syslog;
	public transient DefaultInputPort<InputTuple> input = new DefaultInputPort<InputTuple>() {
		@Override
		public void process(InputTuple inputTuple) {

			incomingTuplesCount++;
			processTuple(inputTuple);
		}
	};

	/**
	 * Metric to keep count of number of tuples coming in on {@link #output}
	 * port
	 */
	@AutoMetric
	protected long incomingTuplesCount;

	@Override
	public void setup(OperatorContext context) {
		syslog = new OperatorSyslogger(systemName, OperatorConfig.getInstance().getSyslogUdpHostname(),
				OperatorConfig.getInstance().getSyslogUdpPort());

	}

	/**
	 * Output port to emit validate records as JSONObject
	 */
	public transient DefaultOutputPort<Map<String, Object>> outputPort = new DefaultOutputPort<Map<String, Object>>();

	/**
	 * Metric to keep count of number of tuples emitted on {@link #output} port
	 */
	@AutoMetric
	long parsedOutputCount;

	public void processTuple(InputTuple tuple) {
		Gson gson = new Gson();
		if (tuple != null) {

			String tupleString = tuple.getData();
			try {
				JsonObject asJson = gson.fromJson(tupleString, JsonObject.class);
				Map<String, Object> outputMap = new HashMap<String, Object>();
				outputMap = TupleUtil.jsonToTupleMap(asJson.toString());

				if (outputPort.isConnected()) {
					outputPort.emit(outputMap);
					parsedOutputCount++;
				}
			} catch (Exception e) {
				log.error("Error in Json Parser: " + e.getMessage(), e);
				syslog.error("Error in Json Parser: " + e.getMessage(), e);
			}
		}
	}

	private void loadJSONFields(HashMap<String, String> map, JsonObject jsonObject, String levelName) {
		try {
			String keyName = null;

			Iterator<Entry<String, JsonElement>> parsedDataEntryIterator = jsonObject.entrySet().iterator();
			while (parsedDataEntryIterator.hasNext()) {
				String key = parsedDataEntryIterator.next().getKey();
				JsonElement element = jsonObject.get(key);

				keyName = (levelName != null) ? (levelName + OBJECT_SEPERATOR + key) : (key);

				if (element.isJsonObject()) {
					JsonObject elementJsonObject = element.getAsJsonObject();
					loadJSONFields(map, elementJsonObject, keyName);
				} else if (element.isJsonArray()) {
					JsonArray elementJsonArray = element.getAsJsonArray();
					loadJSONFields(map, elementJsonArray, keyName);
				} else {
					String value = jsonObject.get(key).toString();
					map.put(keyName, value);
				}
			}
		} catch (Exception e) {
			syslog.error("Error in Json Parser: " + e.getMessage(), e);

		}
	}

	private void loadJSONFields(HashMap<String, String> map, JsonArray jsonArray, String levelName) {
		try {
			for (int index = 0; index < jsonArray.size(); index++) {
				String keyName = levelName + OPEN_ARRAY_CHARACTER + index + CLOSE_ARRAY_CHARACTER;

				JsonElement arrayElement = jsonArray.get(index);

				if (arrayElement.isJsonObject()) {
					JsonObject elementJsonObject = arrayElement.getAsJsonObject();
					loadJSONFields(map, elementJsonObject, keyName);
				} else if (arrayElement.isJsonArray()) {
					JsonArray elementArrayObject = arrayElement.getAsJsonArray();
					loadJSONFields(map, elementArrayObject, keyName);
				} else {
					String value = arrayElement.toString();
					map.put(keyName, value);
				}
			}
		} catch (Exception e) {
			syslog.error("Error in Json Parser: " + e.getMessage(), e);

		}
	}

	@Override
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	@Override
	public String getSystemName() {
		return systemName;
	}

}
