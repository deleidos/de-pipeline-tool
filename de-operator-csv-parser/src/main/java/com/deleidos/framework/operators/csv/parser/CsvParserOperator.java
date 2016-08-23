package com.deleidos.framework.operators.csv.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.datatorrent.api.AutoMetric;
import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.common.util.BaseOperator;
import com.deleidos.framework.operators.common.InputTuple;
import com.deleidos.framework.operators.common.TupleUtil;

public class CsvParserOperator extends BaseOperator {

	private static final Logger log = Logger.getLogger(CsvParserOperator.class);

	/** Representative characters used in JSON */
	protected String delimiter = ",";
	private int numHeaders;
	protected String headerKeys[];

	public int getNumHeaders() {
		return numHeaders;
	}

	public void setNumHeaders(Float numHeaders) {
		this.numHeaders = numHeaders.intValue();
	}

	public void setDelimiter(String value) {
		delimiter = value;
	}

	public String getDelimiter() {
		return this.delimiter;
	}

	public transient DefaultInputPort<InputTuple> input = new DefaultInputPort<InputTuple>() {
		@Override
		public void process(InputTuple inputTuple) {
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
	public transient DefaultOutputPort<Map<String, Object>> output = new DefaultOutputPort<Map<String, Object>>();

	/**
	 * Metric to keep count of number of tuples emitted on {@link #output} port
	 */
	@AutoMetric
	long parsedOutputCount;

	public void processTuple(InputTuple tuple) throws IOException, ClassNotFoundException {

		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap = TupleUtil.csvInputTupleToMap(tuple, delimiter);

		if (tuple != null) {
			if (output.isConnected()) {
				output.emit(outputMap);
				parsedOutputCount++;
			}
		}

	}

}
