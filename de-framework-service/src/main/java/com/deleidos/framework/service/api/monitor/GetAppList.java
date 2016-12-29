package com.deleidos.framework.service.api.monitor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.monitoring.HadoopYarnApiClient;
import com.deleidos.framework.service.config.ServiceConfig;

/**
 * Get a list of Apex YARN apps deployed to the Hadoop cluster.
 * 
 * @author vernon
 */
public class GetAppList extends BaseWebSocketMessage {

	private transient HadoopYarnApiClient client = new HadoopYarnApiClient(
			String.format("http://%s:8088", ServiceConfig.getInstance().getHadoopNameNodeHostname()));

	private String request;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	@Override
	@Path("/getAppList")
	@GET
	public void processMessage() throws Exception {
		sendResponse(client.getApexApps());
	}
}
