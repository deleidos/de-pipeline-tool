package com.deleidos.framework.operators.csv.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.datatorrent.api.AutoMetric;
import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.common.util.BaseOperator;
import com.deleidos.analytics.common.util.JsonToMapUtil;
import com.google.gson.JsonObject;

public class CsvParserOperator extends BaseOperator {

	private static final Logger log = Logger.getLogger(CsvParserOperator.class);

	/** Representative characters used in JSON */
	protected char delimiter = ',';
	private int numHeaders;
	protected String headerKeys[];

	public int getNumHeaders() {
		return numHeaders;
	}

	public void setNumHeaders(Float numHeaders) {
		this.numHeaders = numHeaders.intValue();
	}

	public void setDelimiter(String value) {
		if (value.startsWith("\\"))
			delimiter = setDelimiterFromEscapeSequence(value.charAt(1));
		else
			delimiter = value.charAt(0);
	}

	private char setDelimiterFromEscapeSequence(char c) {
		switch (c) {
		case 't':
			return '\t';
		case 'n':
			return '\n';
		case 'r':
			return '\r';
		case 'f':
			return '\f';
		case '\'':
			return '\'';
		default:
			return ',';
		}
	}

	public String getDelimiter() {
		return Character.toString(this.delimiter);
	}

	public transient DefaultInputPort<ArrayList<byte[]>> input = new DefaultInputPort<ArrayList<byte[]>>() {
		@Override
		public void process(ArrayList<byte[]> inputTuple) {
			incomingTuplesCount++;
			try {
				processTuple(inputTuple);
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
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

	/**
	 * Determine the header key, i.e., the ordered list of field names in the input data. If the header key filename
	 * property was set, then grab the header keys from that. Otherwise, assume the first line of the stream contains
	 * the header keys.
	 */
	public String parseHeaders(ArrayList<byte[]> tuple) {
		String fullHeader = "";
		String headerRow = "";

		headerRow = new String(tuple.get(1));

		try {
			this.headerKeys = CSVParser.parse(headerRow, 0, delimiter);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		return fullHeader;
	}

	@SuppressWarnings("unchecked")
	public void processTuple(ArrayList<byte[]> tuple) throws IOException, ClassNotFoundException {
		Map<String, String> props = null;
		if (tuple.size() != 0) {

			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(tuple.get(0)));
			props = (Map<String, String>) in.readObject();
			
			int headerRows = Integer.parseInt(props.get("headerRows"));
			if (headerRows != 0) {
				parseHeaders(tuple);
			}
			String data = new String(tuple.get(headerRows + 1));

			if (tuple != null) {
				try {
					String[] parsedData = CSVParser.parse(data, 0, delimiter);
					JsonObject asJson = new JsonObject();
					for (int i = 0; i < parsedData.length; i++) {
						asJson.addProperty(headerKeys[i], parsedData[i]);
					}

					HashMap<String, String> outputMap = new HashMap<String, String>();

					JsonToMapUtil.loadJSONFields(outputMap, asJson, null);

					if (output.isConnected()) {
						output.emit(outputMap);
						parsedOutputCount++;
					}
				}
				catch (Exception e) {
					log.error(String.format("Failed to parse json tuple "), e);
				}
			}
		}

	}

}
