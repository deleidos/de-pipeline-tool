package com.deleidos.framework.operators.xml.parser;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.XML;

import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.common.util.BaseOperator;
import com.deleidos.analytics.common.util.GsonFactory;
import com.deleidos.framework.operators.common.InputTuple;
import com.deleidos.framework.operators.common.OperatorSystemInfo;
import com.deleidos.framework.operators.common.TupleUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Operator that converts XML string to Pojo <br>
 * <b>Properties</b> <br>
 * <b>alias</b>:This maps to the root element of the XML string. If not specified, parser would expect the root element
 * to be fully qualified name of the Pojo Class. <br>
 * <b>dateFormats</b>: Comma separated string of date formats e.g dd/mm/yyyy,dd-mmm-yyyy where first one would be
 * considered default
 * 
 * @displayName XmlParser
 * @category Parsers
 * @tags xml pojo parser
 * @since 3.2.0
 */
public class XmlParserOperator extends BaseOperator implements OperatorSystemInfo {

	private String systemName;

	public transient DefaultOutputPort<Map<String, Object>> output = new DefaultOutputPort<Map<String, Object>>();

	public transient DefaultInputPort<InputTuple> input = new DefaultInputPort<InputTuple>() {
		@Override
		public void process(InputTuple inputTuple) {

			processTuple(inputTuple);
		}
	};

	public void processTuple(InputTuple tuple) {
		String inputTuple = tuple.getData();
		JSONObject json = XML.toJSONObject(inputTuple);
		Gson gson = GsonFactory.getInstance().getGsonWithCollectionDeserializers();
		JsonObject asJson = gson.fromJson(json.toString(), JsonObject.class);
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap = TupleUtil.jsonToTupleMap(asJson.toString());

		output.emit(outputMap);
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
