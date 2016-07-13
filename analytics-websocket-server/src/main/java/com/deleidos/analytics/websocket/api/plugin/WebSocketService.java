package com.deleidos.analytics.websocket.api.plugin;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import com.deleidos.analytics.websocket.WebSocketServlet;

public class WebSocketService {

	private static final Logger logger = Logger.getLogger(WebSocketService.class);
	
	private Server server;
	private int port;
	
	public WebSocketService(int port) {
		this.port = port;
	}
	
	public void start() {
		try {
			logger.info("Starting WebSocketService on port: " + port);
			final HandlerList handlers = new HandlerList();

			// Handler for Jersey Resources, and Swagger Listings, and WebSockets
	        handlers.addHandler(buildContext());
	        
	        // Start server
	        server = new Server(port);
	        server.setHandler(handlers);
	        server.start();
		} catch (Exception e) {
			logger.error("Failed to start WebSocketService", e);
		}
	}
	
	public void stop() {
		try {
			server.stop();
		} catch (Exception e) {
			logger.error("Error while stopping DataServiceServer", e);
		}
	}
	
	public void join() {
		try {
			server.join();
		} catch (InterruptedException e) {
			logger.error("Failed to join DataServiceServer", e);
		}
	}

	private ContextHandler buildContext() {
		//ResourceConfig resourceConfig = new ResourceConfig();
		// list all Jersey resources and the Swagger ApiListingResource
		//resourceConfig.packages(ApiListingResource.class.getPackage().getName());
		//ServletContainer servletContainer = new ServletContainer(resourceConfig);
		ServletContainer servletContainer = new ServletContainer();
		ServletHolder servletHolder = new ServletHolder(servletContainer);
		ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContext.setContextPath("/");
		servletContext.addServlet(servletHolder, "/*");
		
		ServletHolder wsStream = new ServletHolder("WebSocketServlet", WebSocketServlet.class);
		servletContext.addServlet(wsStream, "/analytics/*");
		
		return servletContext;
	}
}
