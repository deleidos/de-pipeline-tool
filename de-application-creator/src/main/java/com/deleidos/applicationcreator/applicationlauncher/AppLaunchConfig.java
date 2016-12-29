package com.deleidos.applicationcreator.applicationlauncher;

import com.deleidos.framework.model.system.SystemDescriptor;

/**
 * Apex application launch configuration.
 * 
 * @author vernona
 */
public class AppLaunchConfig {

	private SystemDescriptor systemDescriptor;
	private String appBundleName;

	/**
	 * Constructor.
	 * 
	 * @param systemDescriptor
	 * @param appBundleName
	 */
	public AppLaunchConfig(SystemDescriptor systemDescriptor, String appBundleName) {
		this.systemDescriptor = systemDescriptor;
		this.appBundleName = appBundleName;
	}

	public SystemDescriptor getSystemDescriptor() {
		return systemDescriptor;
	}

	public void setSystemDescriptor(SystemDescriptor systemDescriptor) {
		this.systemDescriptor = systemDescriptor;
	}

	public String getAppBundleName() {
		return appBundleName;
	}

	public void setAppBundleName(String appBundleName) {
		this.appBundleName = appBundleName;
	}
}
