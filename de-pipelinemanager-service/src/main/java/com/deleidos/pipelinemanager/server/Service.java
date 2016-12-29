package com.deleidos.pipelinemanager.server;

import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.deleidos.applicationcreator.applicationlauncher.AppLaunchConfig;
import com.deleidos.framework.model.system.SystemDescriptor;
import com.deleidos.pipelinemanager.server.services.killApp;
import com.deleidos.pipelinemanager.server.services.shutdownApp;
import com.deleidos.pipelinemanager.utility.JSONOperations;
import com.deleidos.pipelinemanager.utility.fileUtility;

/**
 * 
 * @author shange
 *	
 *	<p>
 *	rest server that is supposed ot sit in the hadoop client node
 *  </p>
 */
@Path("/service")
public class Service {
	
	private static final Logger logger = Logger.getLogger(Service.class);
	private String path = "/tmp/";	//Will sit in tmp in the docker image
	private int status = 200; 

	@POST
	@Path("/shutdownApp")
	@Consumes(MediaType.TEXT_HTML)
	public Response shutdownApp(
			@FormDataParam("id") String appID){
		String message = "";
		logger.debug("Service stopping: " +appID);	
		try {
			message = shutdownApp.getInstance().setAppID(appID).shutdown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			message = "Stop app " + appID + " error: " + e.getMessage();
			status = 500;
		}
		return Response.status(status).entity(message).build();
	}
	
	@POST
	@Path("/killApp")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response deleteAPP(
			@FormDataParam("appID") String appID){

		String message = "";		
			logger.debug("Service killing: " +appID);
			try {
				message = killApp.getInstance().setAppID(appID).kill();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				status = 500;
				message = "Kill app " + appID + " error: " + e.getMessage();
				logger.error(message);
			}
		return Response.status(status).entity(message).build();
	}
	
	/*
	 * Primary APA server post, recieves APA file, file description, file location and the system descriptor as a string
	 */
	@POST
	@Path("/apaPost")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response receiveAPA(
				@FormDataParam("file") InputStream uploadedFile,
				@FormDataParam("filePath") String filePath,
				@FormDataParam("sys") String SysDescriptorInString,
				@FormDataParam("file") FormDataContentDisposition fileDetail){
		String message; Boolean response = false;
		String appBundleName = fileDetail.getFileName();
		System.out.println("App recieved: " + appBundleName);
	    filePath = path + appBundleName;
	
		try {
			fileUtility.getInstance().savefile(uploadedFile, filePath);
		} catch (IOException e) {
			message = "Save file Error: "  + e.getLocalizedMessage() + " with file: " +uploadedFile;
			status = 500;
			logger.error(message);
			return Response.status(status).entity(message).build();
		}
		logger.debug("File saved at:" + filePath);
		SystemDescriptor sysDesc = JSONOperations.getInstance().stringToSysDescriptor(SysDescriptorInString);
		logger.debug("Launching appliation: " + appBundleName);
		try{
			response = AppLauncher.getInstance().launchApp(new AppLaunchConfig(sysDesc, appBundleName));
//			message = "Work start: " + appBundleName;
		} catch(Exception e){
			message = "Error due to : " + e.getMessage();
			logger.error(message);
			status = 500;
		}
		return Response.status(status).entity(response).build();
	}
	
	

	@POST
	@Path("/test")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response sendMsg(
			@FormDataParam("msg") String msg)
	{
		String message = "Testing: " + msg;
		return Response.status(200).entity(message).build();
	}
}