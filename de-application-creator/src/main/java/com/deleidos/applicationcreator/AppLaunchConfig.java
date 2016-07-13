package com.deleidos.applicationcreator;

import com.deleidos.framework.model.system.SystemDescriptor;

/**
 * Apex application launch configuration.
 * 
 * @author vernona
 */
class AppLaunchConfig {

	private SystemDescriptor systemDescriptor;
	private String appBundleName;

	/**
	 * Constructor.
	 * 
	 * @param systemDescriptor
	 * @param appBundleName
	 */
	AppLaunchConfig(SystemDescriptor systemDescriptor, String appBundleName) {
		this.systemDescriptor = systemDescriptor;
		this.appBundleName = appBundleName;
	}

	SystemDescriptor getSystemDescriptor() {
		return systemDescriptor;
	}

	void setSystemDescriptor(SystemDescriptor systemDescriptor) {
		this.systemDescriptor = systemDescriptor;
	}

	String getAppBundleName() {
		return appBundleName;
	}

	void setAppBundleName(String appBundleName) {
		this.appBundleName = appBundleName;
	}
}
