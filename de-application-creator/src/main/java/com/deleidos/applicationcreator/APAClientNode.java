package com.deleidos.applicationcreator;

import java.io.File;
import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

/*
 * Client node for the server post: should invoke launch of application sending APA file
 * @author shange
 */
public class APAClientNode {
	HostnameVerifier hostnameVerifier = getHostnameVerifier();
	private static final Logger logger = Logger.getLogger(APAClientNode.class);

	private int port = 8080;
	private String resource;

	/**
	 * Constructor.
	 * 
	 * @param managerServiceHostname
	 */
	public APAClientNode(String managerServiceHostname) {
		resource = "http://" + managerServiceHostname + ":" + port + "/rest/service/";
	}

	@SuppressWarnings("resource")
	public String postStop(String appID) {
		String message = "";
		Client client = this.initClient();
		logger.debug("Stopping id: : " + appID);
		final WebTarget server = client.target(resource + "shutdownApp");
		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("appID", appID);
		final Response response = server.request().post(Entity.entity(multipart, multipart.getMediaType()));

		int statusCode = response.getStatusInfo().getStatusCode();
		message = statusCode + " : " + response.getStatusInfo().getReasonPhrase();
		if (statusCode != 200) {
			if (statusCode == 404) {
				logger.error(message + ", serverside url changed");
			}
			logger.error(message);
			throw new RuntimeException(message);
		}

		logger.debug("Server response message: " + message);
		try {
			formDataMultiPart.close();
			multipart.close();
		}
		catch (IOException e) {
			message = e.getMessage();
		}

		return message;
	}

	@SuppressWarnings("resource")
	public String postKill(String appID) {
		String message = "";
		final Client client = this.initClient();
		logger.debug("Killing id: : " + appID);
		final WebTarget server = client.target(resource + "killApp");
		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("appID", appID);
		final Response response = server.request().post(Entity.entity(multipart, multipart.getMediaType()));

		int statusCode = response.getStatusInfo().getStatusCode();
		message = statusCode + " : " + response.getStatusInfo().getReasonPhrase();
		if (statusCode != 200) {
			if (statusCode == 404) {
				logger.error(message + ", serverside url changed");
			}
			logger.error(message);
			throw new RuntimeException(message);
		}

		logger.debug("Server response message: " + message);
		try {
			formDataMultiPart.close();
			multipart.close();
		}
		catch (IOException e) {
			message = e.getMessage();
		}
		return message;
	}

	public Client initClient() {
		return ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
	}

	// Takes file name
	@SuppressWarnings("resource")
	public String postServer(String fn, String sys, String filePath) throws Exception {
		String message;
		if (!fileExist(fn))
			return "file not found error";

		final FileDataBodyPart filePart = new FileDataBodyPart("file", new File(fn));
		Client client = this.initClient();
		final WebTarget server = client.target(resource + "apaPost");
		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("filePath", filePath)
				.bodyPart(filePart);
		multipart.field("sys", sys);
		final Response response = server.request().post(Entity.entity(multipart, multipart.getMediaType()));

		int statusCode = response.getStatusInfo().getStatusCode();
		message = statusCode + " : " + response.getStatusInfo().getReasonPhrase();
		if (statusCode != 200) {
			if (statusCode == 404) {
				logger.error(message + ", serverside url changed");
			}
			logger.error(message);
			throw new RuntimeException(message);
		}

		logger.debug("Server response message: " + message);
		try {
			formDataMultiPart.close();
			multipart.close();
		}
		catch (IOException e) {
			message = e.getMessage();
		}
		logger.debug("Server connection closed");

		return message;
	}

	private HostnameVerifier getHostnameVerifier() {
		HostnameVerifier hostnameVerifier = new HostnameVerifier() {
			@Override
			public boolean verify(String s, SSLSession sslSession) {
				return true;
			}
		};
		return hostnameVerifier;
	}

	private Boolean fileExist(String fileName) {
		File f = new File(fileName);
		if (f.exists()) {
			System.out.println("File : " + fileName + " exists");
			return true;
		}
		System.out.println("File does not exist: " + fileName);
		return false;
	}

}