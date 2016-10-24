package com.deleidos.framework.operators.common;

import com.deleidos.analytics.common.util.JsonUtil;

public class OperatorConfig {

	private static final String defaultConfigFile = "operator_config.json";
	private static OperatorConfig instance = new OperatorConfig();
	private static Boolean initialized = false;

	private String syslogUdpHostname;
	private int syslogUdpPort;

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static synchronized OperatorConfig getInstance() {
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
	private OperatorConfig() {
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
			// Wrap the checked exception in a Runtime exception.
			throw new RuntimeException(t);
		}
	}

	public String getSyslogUdpHostname() {
		return syslogUdpHostname;
	}

	public void setSyslogUdpHostname(String syslogUdpHostname) {
		this.syslogUdpHostname = syslogUdpHostname;
	}

	public int getSyslogUdpPort() {
		return syslogUdpPort;
	}

	public void setSyslogUdpPort(int syslogUdpPort) {
		this.syslogUdpPort = syslogUdpPort;
	}
}
