package com.deleidos.framework.operators.kafka;

import java.util.Map;

import org.apache.log4j.Logger;

import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.Context.OperatorContext;
import com.datatorrent.common.util.BaseOperator;
import com.deleidos.analytics.kafka.client.MessageProducer;
import com.deleidos.analytics.kafka.client.QueueConfig;
import com.deleidos.framework.operators.common.OperatorConfig;
import com.deleidos.framework.operators.common.OperatorSyslogger;
import com.deleidos.framework.operators.common.OperatorSystemInfo;
import com.deleidos.framework.operators.common.TupleUtil;

public class KafkaOutputOperator extends BaseOperator implements OperatorSystemInfo {
	protected String hostname;
	protected String topic;
	private transient QueueConfig config;
	private transient MessageProducer producer;
	private transient OperatorSyslogger syslog;

	private String systemName;
	
	private transient final Logger log = Logger.getLogger(KafkaOutputOperator.class);

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
		producer = new MessageProducer(config, topic);
		syslog = new OperatorSyslogger(systemName, OperatorConfig.getInstance().getSyslogUdpHostname(),
				OperatorConfig.getInstance().getSyslogUdpPort());
	}

	public final transient DefaultInputPort<Map<String, Object>> input = new DefaultInputPort<Map<String, Object>>() {
		@Override
		public void process(Map<String, Object> tuple) {
			// Send out single data
			String jsonString = TupleUtil.tupleMapToJson(tuple);
			log.info("hostName: " + hostname + " topic: " + topic + " producer: " + producer.toString());
			try {
				producer.produce(jsonString);

			}
			catch (Exception e) {
				syslog.error("Error in Kafka Output Operator: " + e.getMessage(), e);
				log.error("Error in Kafka Output Operator: " + e.getMessage(), e);
			}
		}
	};

	@Override
	public void teardown() {
		producer.close();
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
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	@Override
	public String getSystemName() {
		return systemName;
	}
}
