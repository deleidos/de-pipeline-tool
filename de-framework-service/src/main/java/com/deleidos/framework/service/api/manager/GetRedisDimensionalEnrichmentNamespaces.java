package com.deleidos.framework.service.api.manager;

import java.util.List;
import java.util.ArrayList;

import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;

/**
 * Get Redis dimensional enrichment namespaces.
 * 
 * @author vernona
 */
public class GetRedisDimensionalEnrichmentNamespaces extends BaseWebSocketMessage {

	private String request;
	
	@Override
	public void processMessage() throws Exception {
		// TODO query mongodb

		List<String> namespaces = new ArrayList<String>();
		namespaces.add("faa_data");
		namespaces.add("sales_customer");
		sendResponse(JsonUtil.toJsonString(namespaces));
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}
}
