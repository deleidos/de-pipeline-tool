package com.deleidos.framework.monitoring.response;

public class AppsResponse {

	public static final String PATH = "/ws/v1/cluster/apps";

	public static class AppWrapper {

		public static class App {
			public String id;
			public String user;
			public String name;
			public String queue;
			public String state;
			public String finalStatus;
			public double progress;
			public String trackingUI;
			public String trackingUrl;
			public String diagnostics;
			public String clusterId;
			public String applicationType;
			public String startedTime;
			public String finishedTime;
			public String elapsedTime;
			public String amContainerLogs;
			public String amHostHttpAddress;
			// Below here are values only received from standalone Apex, and not
			// from the DataTorrent Apex
			public int allocatedMB;
			public int allocatedVCores;
			public String applicationTags;
			public String logAggregationStatus;
			public long memorySeconds;
			public int numAMContainerPreempted;
			public int numNonAMContainerPreempted;
			public int preemptedResourceMB;
			public int preemptedResourceVCores;
			public int runningContainers;
			public long vcoreSeconds;
		}

		public App[] app;
	}

	public AppWrapper apps;

}
