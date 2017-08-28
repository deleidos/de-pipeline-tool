package com.deleidos.framework.service.api.manager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.applicationcreator.APAClientNode;
import com.deleidos.framework.service.config.ServiceConfig;
import com.deleidos.framework.service.data.SystemDataManager;
import com.deleidos.framework.model.system.SystemDescriptor;
import com.deleidos.framework.monitoring.MonitoringUtil;

/**
 * Kill a running system.
 * 
 * @author vernona
 */
public class KillSystem extends BaseWebSocketMessage {

	private Logger logger = Logger.getLogger(KillSystem.class);
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
	@Path("/killSystem")
	@GET
	public void processMessage() throws Exception {
		SystemDescriptor system = SystemDataManager.getInstance().getSystemDecriptor(id);
		MonitoringUtil util = new MonitoringUtil(ServiceConfig.getInstance().getHadoopNameNodeHostname());
		String appID = util.getAppIdByName(system.getName());
		APAClientNode clientNode = new APAClientNode(ServiceConfig.getInstance().getManagerServiceHostname());
		clientNode.postKill(appID);
		logger.info("Killing app: " + appID);
		sendResponse(appID + " " + system.getName());
	}
}
