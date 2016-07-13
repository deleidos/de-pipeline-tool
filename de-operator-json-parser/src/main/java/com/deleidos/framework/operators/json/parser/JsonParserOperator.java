package com.deleidos.framework.operators.json.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.datatorrent.api.AutoMetric;
import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.common.util.BaseOperator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonParserOperator extends BaseOperator {

	private static final Logger log = Logger.getLogger(JsonParserOperator.class);

	

	/** Representative characters used in JSON */
	private final String OPEN_ARRAY_CHARACTER = "[";
	private final String CLOSE_ARRAY_CHARACTER = "]";
	private final String OBJECT_SEPERATOR = ".";

	public transient DefaultInputPort<ArrayList<byte[]>> input = new DefaultInputPort<ArrayList<byte[]>>() {
		@Override
		public void process(ArrayList<byte[]> inputTuple) {
			incomingTuplesCount++;
			processTuple(inputTuple);
		}
	};
	
	/**
	 * Metric to keep count of number of tuples coming in on {@link #output} port
	 */
	@AutoMetric
	protected long incomingTuplesCount;

			  
	/**
	 * Output port to emit validate records as JSONObject
	 */
	public transient DefaultOutputPort<Map<String, String>> output = new DefaultOutputPort<Map<String, String>>();
	
	/**
	 * Metric to keep count of number of tuples emitted on {@link #output} port
	 */
	@AutoMetric
	long parsedOutputCount;


	public void processTuple(ArrayList<byte[]> tuple) {
		Gson gson = new Gson();
		if (tuple != null) {
			
			String tupleString = new String(tuple.get(1));
			
			try {
				JsonObject asJson = gson.fromJson(tupleString, JsonObject.class);
				HashMap<String, String> outputMap = new HashMap<String, String>();
				
				loadJSONFields(outputMap, asJson, null);
		
				if (output.isConnected()) {
					output.emit(outputMap);
					parsedOutputCount++;
				}
			} catch (Exception e) {
				log.error(String.format("Failed to parse json tuple [%s].", tupleString), e);
			}
		}
	}

	private void loadJSONFields(HashMap<String, String> map, JsonObject jsonObject, String levelName) {
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
			}
			else {
				String value = jsonObject.get(key).toString();
				map.put(keyName, value);	
			}
		}
	}
	
	private void loadJSONFields(HashMap<String, String> map, JsonArray jsonArray, String levelName) {
		for (int index = 0; index < jsonArray.size(); index++) {
			String keyName = levelName + OPEN_ARRAY_CHARACTER + index + CLOSE_ARRAY_CHARACTER;
			
			JsonElement arrayElement = jsonArray.get(index);
			
			if (arrayElement.isJsonObject()) {
				JsonObject elementJsonObject = arrayElement.getAsJsonObject();
				loadJSONFields(map, elementJsonObject, keyName);
			}
			else if(arrayElement.isJsonArray()) {
				JsonArray elementArrayObject = arrayElement.getAsJsonArray();
				loadJSONFields(map, elementArrayObject, keyName);
			}
			else {
				String value = arrayElement.toString();
				map.put(keyName, value);
			}
		}
	}

}
