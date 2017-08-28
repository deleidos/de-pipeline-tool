package com.deleidos.framework.service.api.builder;

import java.util.UUID;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.util.GsonFactory;
import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.service.data.SystemDataManager;
import com.deleidos.framework.model.system.SystemDescriptor;

/**
 * Save the system descriptor. If it is new (_id is null), then a new _id value will be set, and the document will be
 * inserted. If the _id already exists, then the document will be updated. The resulting system descriptor is returned.
 * 
 * @author vernona
 */
public class SaveSystemDescriptor extends BaseWebSocketMessage {

	private static final Logger log = Logger.getLogger(SaveSystemDescriptor.class);

	private String request;
	private SystemDescriptor systemDescriptor;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public SystemDescriptor getSystemDescriptor() {
		return systemDescriptor;
	}

	public void setSystemDescriptor(SystemDescriptor systemDescriptor) {
		this.systemDescriptor = systemDescriptor;
	}

	@Override
	@Path("/saveSystemDescriptor")
	@POST
	public void processMessage() throws Exception {
		log.info(GsonFactory.getInstance().getGsonWithNoDeserializers().toJson(systemDescriptor));
		if (systemDescriptor.get_id() == null || systemDescriptor.get_id().trim().equals("")) {
			systemDescriptor.set_id(UUID.randomUUID().toString());
			SystemDataManager.getInstance().insertSystemDescriptor(systemDescriptor);
		}
		else {
			SystemDataManager.getInstance().updateSystemDescriptor(systemDescriptor);
		}

		sendResponse(systemDescriptor);
	}
}
