package com.deleidos.framework.operators.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.datatorrent.api.Context;
import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.common.util.BaseOperator;
import com.deleidos.framework.operators.common.OperatorConfig;
import com.deleidos.framework.operators.common.OperatorSyslogger;
import com.deleidos.framework.operators.common.OperatorSystemInfo;
import com.deleidos.framework.operators.common.TupleUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RESTOutputOperator extends BaseOperator implements OperatorSystemInfo {
	String url;
	Client client;
	WebResource webResource;
	URI uri;

	private String systemName;

	private static final Logger log = Logger.getLogger(RESTOutputOperator.class);
	private transient OperatorSyslogger syslog;

	public RESTOutputOperator() {
	}

	public void postRest(String json) throws IOException, InterruptedException {
		Thread.sleep(700);
		ClientResponse response = webResource.type("application/json").post(ClientResponse.class, json);
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}
		else {
			log.info("got response: " + response.getEntity(String.class));
		}

	}

	public transient final DefaultInputPort<Map<String, Object>> input = new DefaultInputPort<Map<String, Object>>() {
		@Override
		public void process(Map<String, Object> tuple) {

			String jsonString = TupleUtil.tupleMapToJson(tuple);
			try {
				postRest(jsonString);
			}
			catch (IOException | InterruptedException e) {
				e.printStackTrace();
				syslog.error("Error in REST Output: " + e.getMessage()+ "[ERROR END]",e);
			}
		}
	};

	@Override
	public void setup(Context.OperatorContext context) {
		syslog = new OperatorSyslogger(systemName,
				OperatorConfig.getInstance().getSyslogUdpHostname(), OperatorConfig.getInstance().getSyslogUdpPort());

		super.setup(context);
		client = Client.create();
		try {
			uri = new URI(url);
		}
		catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			syslog.error("Error in REST Output: " + e.getMessage()+ "[ERROR END]",e);

			e.printStackTrace();
		}
		webResource = client.resource(uri);
	}

	@Override
	public void beginWindow(long windowId) {
	}

	@Override
	public void endWindow() {
	}

	@Override
	public void teardown() {
	}
	
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
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
