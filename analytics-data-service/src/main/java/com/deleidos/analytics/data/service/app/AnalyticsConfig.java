package com.deleidos.analytics.data.service.app;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.analytics.common.util.StringUtil;

/**
 * Analytics application configuration.
 * 
 * @author vernona
 */
public class AnalyticsConfig {

	private Logger logger = Logger.getLogger(AnalyticsConfig.class);
	private static final String defaultConfigFile = "analytics_config.json";
	private static AnalyticsConfig instance = new AnalyticsConfig();
	private static Boolean initialized = false;

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static synchronized AnalyticsConfig getInstance() {
		synchronized (initialized) {
			if (!initialized) {
				instance.init();
			}
		}
		return instance;
	}

	/**
	 * Private no-arg constructor enforces the singleton pattern.
	 */
	private AnalyticsConfig() {
	}

	/**
	 * Initialize the config file.
	 */
	private void init() {
		try {
			JsonUtil.loadFromFile(defaultConfigFile, this);
			initialized = true;
		}
		catch (Throwable t) {
			logger.error(t);
			// Wrap the checked exception in a Runtime exception.
			throw new RuntimeException(t);
		}
	}

	private int serverPort;
	private String[] apiPlugins;

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String[] getApiPlugins() {
		return apiPlugins;
	}

	public void setApiPlugins(String[] apiPlugins) {
		this.apiPlugins = apiPlugins;
	}

	@Override
	public String toString() {
		return StringUtil.objectToString(this);
	}
}
