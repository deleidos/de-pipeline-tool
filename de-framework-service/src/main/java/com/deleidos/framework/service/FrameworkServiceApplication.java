package com.deleidos.framework.service;

import com.deleidos.analytics.data.service.app.DataServiceApplication;

/**
 * Executable framework service application.
 * 
 * @author vernona
 */
public class FrameworkServiceApplication extends DataServiceApplication {

	/**
	 * Constructor.
	 */
	public FrameworkServiceApplication() {
		super(new FrameworkServiceDataApiPlugin(), 8080);
	}

	/**
	 * Main function for application execution.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		FrameworkServiceApplication app = new FrameworkServiceApplication();
		app.start();
		app.join();
	}
}