package com.deleidos.analytics.data.service.app;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import com.codahale.metrics.servlets.AdminServlet;
import com.deleidos.analytics.data.service.app.listeners.HealthCheckServletContextListener;
import com.deleidos.analytics.data.service.app.listeners.MetricsServletContextListener;
import com.deleidos.analytics.websocket.WebSocketServer;
import com.deleidos.analytics.websocket.WebSocketServlet;
import com.deleidos.analytics.websocket.api.WebSocketApiPlugin;

public class DataServiceApplication {

	private static final Logger logger = Logger.getLogger(DataServiceApplication.class);

	private Server server;
	private List<String> combinedResourcePackageList = new ArrayList<String>();

	public DataServiceApplication() throws Exception {
		logger.info(AnalyticsConfig.getInstance());

		// Load every configured ApiPlugin
		for (String apiPlugin : AnalyticsConfig.getInstance().getApiPlugins()) {
			System.out.println(apiPlugin);
			@SuppressWarnings("rawtypes")
			Class clazz = Class.forName(apiPlugin);
			WebSocketApiPlugin plugin = (WebSocketApiPlugin) clazz.newInstance();
			WebSocketServer.getInstance().registerPlugin(plugin);
			if (plugin.getResourcePackages() != null) {
				combinedResourcePackageList.addAll(plugin.getResourcePackages());
			}
		}
	}

	public void start() {
		try {
			// configureSwagger();

			final HandlerList handlers = new HandlerList();

			// Handler for Swagger UI, static handler.
			// handlers.addHandler(buildSwaggerUI());

			// Handler wsclient UI
			handlers.addHandler(buildWSClientUI());

			// Handler for Jersey Resources, and Swagger Listings, and WebSockets
			handlers.addHandler(buildContext());

			// Start server
			QueuedThreadPool threadPool = new QueuedThreadPool(1000, 10);
			server = new Server(threadPool);
			server.setHandler(handlers);
			ServerConnector connector = new ServerConnector(server);
			connector.setPort(AnalyticsConfig.getInstance().getServerPort());
			server.setConnectors(new Connector[] { connector });
			server.start();
		}
		catch (Exception e) {
			logger.error("Failed to start DataServiceServer", e);
		}
	}

	public void stop() {
		try {
			server.stop();
		}
		catch (Exception e) {
			logger.error("Error while stopping DataServiceServer", e);
		}
	}

	public void join() {
		try {
			server.join();
		}
		catch (InterruptedException e) {
			logger.error("Failed to join DataServiceServer", e);
		}
	}

	// private void configureSwagger() {
	// // This configures Swagger
	// BeanConfig beanConfig = new BeanConfig();
	// beanConfig.setVersion("1.5.0");
	// // TODO make configurable
	// beanConfig.setResourcePackage(StringUtils.join(combinedResourcePackageList, ","));
	// beanConfig.setScan(true);
	// beanConfig.setBasePath("/");
	// beanConfig.setDescription("WebSocket endpoint: ws://&lt;host&gt;:8080/analytics");
	// beanConfig.setTitle("Analytics Data Service API Documentation");
	// }
	//
	// private ContextHandler buildSwaggerUI() throws Exception {
	// final ResourceHandler swaggerUIResourceHandler = new ResourceHandler();
	// swaggerUIResourceHandler.setResourceBase(
	// DataServiceApplication.class.getClassLoader().getResource("swaggerui").toURI().toString());
	// final ContextHandler swaggerUIContext = new ContextHandler();
	// swaggerUIContext.setContextPath("/api-docs/");
	// swaggerUIContext.setHandler(swaggerUIResourceHandler);
	// return swaggerUIContext;
	// }

	private ContextHandler buildWSClientUI() throws Exception {
		final ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase(
				DataServiceApplication.class.getClassLoader().getResource("wsclient").toURI().toString());
		final ContextHandler contextHandler = new ContextHandler();
		contextHandler.setContextPath("/ws-client/");
		contextHandler.setInitParameter("cacheControl", "max-age=0,public");
		contextHandler.setHandler(resourceHandler);
		return contextHandler;
	}

	private ContextHandler buildContext() {
		ResourceConfig resourceConfig = new ResourceConfig();
		// list all Jersey resources and the Swagger ApiListingResource
		// combinedResourcePackageList.add(ApiListingResource.class.getPackage().getName());
		resourceConfig.packages(combinedResourcePackageList.toArray(new String[] {}));
		ServletContainer servletContainer = new ServletContainer(resourceConfig);
		ServletHolder servletHolder = new ServletHolder(servletContainer);
		ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContext.setContextPath("/");
		servletContext.addServlet(servletHolder, "/*");

		ServletHolder wsStream = new ServletHolder("WebSocketServlet", WebSocketServlet.class);
		servletContext.addServlet(wsStream, "/analytics/*");

		// Adding the dropwizard metrics here
		ServletHolder adminServlet = new ServletHolder("AdminServlet", AdminServlet.class);
		servletContext.addServlet(adminServlet, "/metrics/*");
		servletContext.addEventListener(new HealthCheckServletContextListener());
		servletContext.addEventListener(new MetricsServletContextListener());

		return servletContext;
	}

	public static void main(String[] args) throws Exception {
		// String configFile = DEFAULT_CONFIG_FILE;
		// if (args.length > 0) {
		// configFile = args[0];
		// }
		// else {
		// logger.warn("No configuration file passed, using default");
		// }

		DataServiceApplication app = new DataServiceApplication(); // configFile);
		app.start();
		app.join();
	}
}
