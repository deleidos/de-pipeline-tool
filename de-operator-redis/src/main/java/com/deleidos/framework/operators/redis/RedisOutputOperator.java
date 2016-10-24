package com.deleidos.framework.operators.redis;

import java.util.Map;

import org.apache.log4j.Logger;

import com.datatorrent.api.Context;
import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.common.util.BaseOperator;
import com.deleidos.analytics.redis.client.RedisClient;
import com.deleidos.framework.operators.common.TupleUtil;
import com.deleidos.framework.operators.common.KeyFieldValueFinder;
import com.deleidos.framework.operators.common.OperatorConfig;
import com.deleidos.framework.operators.common.OperatorSyslogger;
import com.deleidos.framework.operators.common.OperatorSystemInfo;

/**
 * Redis output operator. Key is obtained from the tuple map using the key field parameter. Key field may be
 * dot-delimited, in which case the key value will be found in a nested object.
 * 
 * @author vernona
 */
public class RedisOutputOperator extends BaseOperator implements OperatorSystemInfo {

	protected String hostname;
	protected String namespace;
	protected String keyField;
	private transient KeyFieldValueFinder finder = new KeyFieldValueFinder();
	private transient RedisClient client;
	
	private String systemName;
	private transient OperatorSyslogger syslog;
	public RedisOutputOperator() {
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public String getKeyField() {
		return keyField;
	}

	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}

	@Override
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	@Override
	public String getSystemName() {
		return systemName;
	}
	
	@Override
	public void setup(Context.OperatorContext context) {
		syslog = new OperatorSyslogger(systemName,
				OperatorConfig.getInstance().getSyslogUdpHostname(), OperatorConfig.getInstance().getSyslogUdpPort());

		client = new RedisClient(hostname);
	}

	public transient final DefaultInputPort<Map<String, Object>> input = new DefaultInputPort<Map<String, Object>>() {
		@Override
		public void process(Map<String, Object> tuple) {
			try{
			Object keyValue = finder.findValue(keyField, tuple);
			if (keyValue != null) {
				String jsonString = TupleUtil.tupleMapToJson(tuple);
				client.setValue(namespace, keyValue.toString(), jsonString);
			}
			}catch(Exception e){
				syslog.error("Error in Redis Output: " + e.getMessage() + "[ERROR END]",e);
			}
		}

	};

	@Override
	public void teardown() {
		if (client != null) {
			client.close();
		}
	}
}
