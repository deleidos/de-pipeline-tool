package com.deleidos.framework.service.api.manager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.applicationcreator.AppCreationInterface;
import com.deleidos.applicationcreator.local.ApplicationCreator;
import com.deleidos.framework.service.config.ServiceConfig;
import com.deleidos.framework.service.data.SystemDataManager;
import com.deleidos.framework.model.event.SystemEventBus;
import com.deleidos.framework.model.system.SystemDescriptor;

/**
 * Deploy a system to the cluster.
 * 
 * @author vernona
 */
public class DeploySystem extends BaseWebSocketMessage {

	private Logger logger = Logger.getLogger(DeploySystem.class);

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
	@Path("/deploySystem")
	@GET
	public void processMessage() throws Exception {
		//sendResponse("HEEEEEEY KEVVINNNNN");
		logger.info("received request: " + request);
		SystemDescriptor system = SystemDataManager.getInstance().getSystemDecriptor(id);
		system.getApplication().setOperatorSystemNameProperty(system.getName());

		// Create APA file
		ApplicationCreator app = new ApplicationCreator(ServiceConfig.getInstance().getManagerServiceHostname(), system,
				system.getName());
		String out = ((AppCreationInterface) app).run();
		logger.info("sending response to the websocket: " + out);
		try {
//			sendResponse("Launching " + out);
			SystemEventBus.getInstance().deploymentComplete(id);
		} catch (Exception e) {
			logger.error("send response error: " + e.getMessage(), e);
		}
		logger.info("sent response to websocket");
	}
}
