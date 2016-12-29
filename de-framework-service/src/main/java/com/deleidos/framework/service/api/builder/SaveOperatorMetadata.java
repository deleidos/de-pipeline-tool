package com.deleidos.framework.service.api.builder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.UUID;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;
import com.deleidos.framework.model.system.OperatorMetadataRequest;
import com.deleidos.framework.service.data.SystemDataManager;

public class SaveOperatorMetadata extends BaseWebSocketMessage {

	private String request;
	private OperatorMetadataRequest metadata;
	private Logger logger = Logger.getLogger(SaveOperatorMetadata.class);

	public OperatorMetadataRequest getMetadata() {
		return metadata;
	}

	public void setMetadata(OperatorMetadataRequest metadata) {
		this.metadata = metadata;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	@Override
	@Path("/saveOperatorMetadata")
	@POST
	public void processMessage() throws Exception {		
		if (metadata.getMetadata().get_id() == null) {
			metadata.getMetadata().set_id(UUID.randomUUID().toString());
			SystemDataManager.getInstance().insertOperatorMetadata(metadata.getMetadata());
		} else {
			SystemDataManager.getInstance().updateOperatorMetadata(metadata.getMetadata());
		}
		sendResponse(metadata.getMetadata().get_id());

		// Save byte to file; path probably hardcoded
		if (metadata.getBytes() != null) {
			try {
				String filePath = "/opt/apex-deployment/operators/";
				File jar = new File(filePath + metadata.getMetadata().getClassName() + ".jar");
				byte[] jbyte = Base64.getDecoder().decode(metadata.getBytes());
				FileOutputStream fos = new FileOutputStream(jar);
				fos.write(jbyte);
				fos.flush();
				fos.close();
			} catch (Exception e) {
				logger.error("Exception in SaveOperatorMetadata: " + e.getMessage(),e);
			}
		}
	}
}
