package com.deleidos.analytics.common.config;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.util.JsonUtil;

/**
 * Base JSON-based configuration file loader.
 * 
 * @author vernona
 */
public abstract class BaseConfig {
	private Logger logger = Logger.getLogger(BaseConfig.class);
	private Boolean initialized = false;

	protected BaseConfig() {
		init();
	}

	/**
	 * Initialize the config file.
	 */
	private synchronized void init() {
		if (!initialized) {
			try {
				JsonUtil.loadFromFile(getConfigFilename(), this);
				initialized = true;
			} catch (Throwable t) {
				logger.error(t);
				// Wrap the checked exception in a Runtime exception.
				throw new RuntimeException(t);
			}
		}
	}

	//
	// Abstract interface:
	//

	abstract protected String getConfigFilename();
}
