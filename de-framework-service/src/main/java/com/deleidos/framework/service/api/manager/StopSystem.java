package com.deleidos.framework.service.api.manager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.applicationcreator.APAClientNode;
import com.deleidos.framework.service.config.ServiceConfig;
import com.deleidos.framework.service.data.SystemDataManager;
import com.deleidos.framework.model.system.SystemDescriptor;
import com.deleidos.framework.monitoring.MonitoringUtil;

/**
 * Stop a running system.
 * 
 * @author vernona
 */
public class StopSystem extends BaseWebSocketMessage {

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
		APAClientNode clientNode = new APAClientNode(ServiceConfig.getInstance().getManagerServiceHostname());
		clientNode.postStop(appID);
		sendResponse(appID);
	}
}
