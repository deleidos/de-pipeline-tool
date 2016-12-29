package com.deleidos.framework.monitoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.deleidos.analytics.common.rest.RestClient;
import com.deleidos.framework.monitoring.response.App;
import com.deleidos.framework.monitoring.response.AppsResponse;

/**
 * Hadoop YARN REST service API client.
 * 
 * @author vernona
 */
public class HadoopYarnApiClient {

	private RestClient client;

	private static final String APPS_PATH = "/ws/v1/cluster/apps";

	private static final String applicationTypeApex = "DataTorrent";

	/**
	 * Constructor.
	 * 
	 * @param hostname
	 */
	public HadoopYarnApiClient(String hostname) {
		client = new RestClient(hostname);
	}

	/**
	 * Get a list of Apex apps that have been deployed to the Hadoop cluster. Non-Apex apps are excluded. Only the most
	 * recent deployment of a given app (by name) will be returned.
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<App> getApexApps() throws Exception {
		Set<String> uniqueAppNames = new HashSet<String>();
		List<App> appList = new ArrayList<App>();
		AppsResponse response = client.getObject(APPS_PATH, AppsResponse.class);
		if (response != null && response.getApps() != null && response.getApps().getApp() != null) {
			App[] sortedApps =response.getApps().getApp();
			Arrays.sort(sortedApps);
			for (App app : sortedApps) {
				if (app.getApplicationType().equals(applicationTypeApex)) {
					String name = app.getName();
					if (!uniqueAppNames.contains(name)) {
						appList.add(app);
						uniqueAppNames.add(name);
					}
				}
			}
		}
		return appList;
	}
}
