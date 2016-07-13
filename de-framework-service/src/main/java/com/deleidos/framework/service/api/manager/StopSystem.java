package com.deleidos.framework.service.api.manager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.rest.RestClient;
import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.applicationcreator.Application_Creation;
import com.deleidos.framework.service.data.SystemDataManager;
import com.deleidos.framework.model.system.SystemDescriptor;
import com.deleidos.framework.monitoring.MonitoringUtil;

/**
 * Stop a running system.
 * 
 * @author vernona
 */
public class StopSystem extends BaseWebSocketMessage {

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(StopSystem.class);

	private String request;
	private String id;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	@Path("/stopSystem")
	@GET
	public void processMessage() throws Exception {
		SystemDescriptor system = SystemDataManager.getInstance().getSystemDecriptor(id);
		String appID = MonitoringUtil.getAppIdByName(system.getName());
		Application_Creation.stopApp(appID);
		// TODO stop system in apex
		sendResponse(id); // TODO return value
	}
}
