package com.deleidos.framework.operators.rest;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.datatorrent.api.Context;
import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.common.util.BaseOperator;
import com.deleidos.framework.operators.common.TupleUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RESTOutputOperator extends BaseOperator {
	String url;

	Client client; 
	WebResource webResource;
	private static final Logger log = Logger.getLogger(RESTOutputOperator.class);
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public RESTOutputOperator() {

	}

	public void postRest(String json) throws IOException {
		
		
		ClientResponse response = webResource.type("application/json").post(ClientResponse.class, json);
		if (response.getStatus() != 200) {
			   throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatus());
		}
		else{
			log.info("got response: " + response.getEntity(String.class));
		}


	}

	public transient final DefaultInputPort<Map<String, Object>> input = new DefaultInputPort<Map<String, Object>>() {
		@Override
		public void process(Map<String, Object> tuple) {

			String jsonString = TupleUtil.tupleMapToJson(tuple);
			try {
				postRest(jsonString);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void setup(Context.OperatorContext context) {

		super.setup(context);
		client = Client.create();
		webResource = client.resource(url);
	}

	@Override
	public void beginWindow(long windowId) {

		// nothing
	}

	@Override
	public void endWindow() {

	}

	@Override
	public void teardown() {

	}
}
