package com.deleidos.framework.service.data;

import com.deleidos.analytics.config.AnalyticsConfig;

/**
 * DE Framework database factory.
 * 
 * @author vernona
 */
public class DeFrameworkDbFactory {

	private static final DeFrameworkDbFactory instance = new DeFrameworkDbFactory();

	private DeFrameworkDb db = new DeFrameworkDb(AnalyticsConfig.getInstance().getMongodbHostname());

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static DeFrameworkDbFactory getInstance() {
		return instance;
	}

	/**
	 * Private constructor enforces the singleton pattern.
	 */
	private DeFrameworkDbFactory() {}

	public DeFrameworkDb getDeFrameworkDb() {
		return db;
	}
}
