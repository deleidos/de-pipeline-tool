package com.deleidos.framework.service.config;

import org.apache.log4j.Logger;

/**
 * Service configuration values. Initialized from environment variables.
 * 
 * @author vernona
 */
public class ServiceConfig {
	private Logger log = Logger.getLogger(ServiceConfig.class);

	private static ServiceConfig instance = new ServiceConfig();

	private String mongodbHostname;
	private String managerServiceHostname;
	private String hadoopNameNodeHostname;

	protected static final String mongodbHostnameEnv = "MONGODB_HOSTNAME";
	protected static final String managerServiceHostnameEnv = "MANAGER_SERVICE_HOSTNAME";
	protected static final String hadoopNameNodeHostnameEnv = "HADOOP_NAME_NODE_HOSTNAME";

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static ServiceConfig getInstance() {
		return instance;
	}

	/**
	 * Private constructor enforces the singleton pattern.
	 */
	private ServiceConfig() {
		init();
	}

	private void init() {
		mongodbHostname = System.getenv(mongodbHostnameEnv);
		managerServiceHostname = System.getenv(managerServiceHostnameEnv);
		hadoopNameNodeHostname = System.getenv(hadoopNameNodeHostnameEnv);
		log.info("mongodbHostname=" + mongodbHostname);
		log.info("managerServiceHostname=" + managerServiceHostname);
		log.info("hadoopNameNodeHostname=" + hadoopNameNodeHostname);
	}

	public String getMongodbHostname() {
		return mongodbHostname;
	}

	public String getManagerServiceHostname() {
		return managerServiceHostname;
	}

	public String getHadoopNameNodeHostname() {
		return hadoopNameNodeHostname;
	}

}
