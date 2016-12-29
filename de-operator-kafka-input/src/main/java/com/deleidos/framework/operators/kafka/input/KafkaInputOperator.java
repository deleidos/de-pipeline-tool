package com.deleidos.framework.operators.kafka.input;

import java.util.List;

import org.apache.log4j.Logger;

import com.datatorrent.api.Context.OperatorContext;
import com.datatorrent.lib.io.SimpleSinglePortInputOperator;
import com.deleidos.analytics.kafka.client.MessageConsumer;
import com.deleidos.analytics.kafka.client.MessageHandler;
import com.deleidos.analytics.kafka.client.QueueConfig;
import com.deleidos.framework.operators.common.InputTuple;
import com.deleidos.framework.operators.common.OperatorConfig;
import com.deleidos.framework.operators.common.OperatorSyslogger;
import com.deleidos.framework.operators.common.OperatorSystemInfo;

public class KafkaInputOperator extends SimpleSinglePortInputOperator<InputTuple>
		implements Runnable, OperatorSystemInfo {
	protected String hostname;
	protected String topic;
	private transient QueueConfig config;
	private String systemName;
	private List<String> headers;
	private transient OperatorSyslogger syslog;
	private transient MessageConsumer consumer;
	private transient Logger logger = Logger.getLogger(KafkaInputOperator.class);

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTopic() {
		return this.topic;
	}

	public void setup(OperatorContext context) {
		String[] hostNames = { hostname };
		config = new QueueConfig(hostNames);
		consumer = new MessageConsumer(config, "KafkaConsumer", topic, new KafkaMessageHandler());
		headers = null;
		syslog = new OperatorSyslogger(systemName, OperatorConfig.getInstance().getSyslogUdpHostname(),
				OperatorConfig.getInstance().getSyslogUdpPort());
	}

	@Override
	public void run() {
		try {

			consumer.consume();
		} catch (Exception e) {
			syslog.error("Error in Kafka Input Operator: " + e.getMessage(), e);
			logger.error("Error in Kafka Input Operator: " + e.getMessage(), e);
		}
	}

	public class KafkaMessageHandler implements MessageHandler {
		@Override
		public void handleMessage(String message) {
			InputTuple outTuple = new InputTuple();
			outTuple.setHeader(headers);
			outTuple.setData(message);
			outputPort.emit(outTuple);
		}
	}

	/**
	 * Implement Operator Interface.
	 */
	@Override
	public void beginWindow(long windowId) {
	}

	/**
	 * Implement Operator Interface.
	 */
	@Override
	public void endWindow() {
	}
	@Override
	public void teardown() {
		consumer.close();
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
