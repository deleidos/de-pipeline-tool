package com.deleidos.framework.monitoring.response;

public class AppsResponse {

	public static final String PATH = "/ws/v1/cluster/apps";

	private Apps apps;

	public Apps getApps() {
		return apps;
	}

	public void setApps(Apps apps) {
		this.apps = apps;
	}
}
