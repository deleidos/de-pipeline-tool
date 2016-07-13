package com.deleidos.analytics.config;

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
	private AnalyticsConfig() {}

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
	private String elasticsearchClusterName;
	private String[] elasticsearchHostnames;
	private String mongodbHostname;
	private String apexHostname;
	private String apexHostUsername;
	private String apexKeyFilePath;
	private String redisHostname;

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

	public String getElasticsearchClusterName() {
		return elasticsearchClusterName;
	}

	public void setElasticsearchClusterName(String elasticsearchClusterName) {
		this.elasticsearchClusterName = elasticsearchClusterName;
	}

	public String[] getElasticsearchHostnames() {
		return elasticsearchHostnames;
	}

	public void setElasticsearchHostnames(String[] elasticsearchHostnames) {
		this.elasticsearchHostnames = elasticsearchHostnames;
	}

	public String getMongodbHostname() {
		return mongodbHostname;
	}

	public void setMongodbHostname(String mongodbHostname) {
		this.mongodbHostname = mongodbHostname;
	}

	public String getApexHostname() {
		return apexHostname;
	}

	public void setApexHostname(String apexHostname) {
		this.apexHostname = apexHostname;
	}

	public String getApexHostUsername() {
		return apexHostUsername;
	}

	public void setApexHostUsername(String apexHostUsername) {
		this.apexHostUsername = apexHostUsername;
	}

	public String getApexKeyFilePath() {
		return apexKeyFilePath;
	}

	public void setApexKeyFilePath(String apexKeyFilePath) {
		this.apexKeyFilePath = apexKeyFilePath;
	}

	public String getRedisHostname() {
		return redisHostname;
	}

	public void setRedisHostname(String redisHostname) {
		this.redisHostname = redisHostname;
	}

	@Override
	public String toString() {
		return StringUtil.objectToString(this);
	}
}
