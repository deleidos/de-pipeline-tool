package com.deleidos.framework.monitoring.response;

public class App implements Comparable<App>{

	private String id;
	private String user;
	private String name;
	private String queue;
	private String state;
	private String finalStatus;
	private double progress;
	private String trackingUI;
	private String trackingUrl;
	private String diagnostics;
	private String clusterId;
	private String applicationType;
	private String startedTime;
	private String finishedTime;
	private String elapsedTime;
	private String amContainerLogs;
	private String amHostHttpAddress;
	// Below here are values only received from standalone Apex, and not
	// from the DataTorrent Apex
	private int allocatedMB;
	private int allocatedVCores;
	private String applicationTags;
	private String logAggregationStatus;
	private long memorySeconds;
	private int numAMContainerPreempted;
	private int numNonAMContainerPreempted;
	private int preemptedResourceMB;
	private int preemptedResourceVCores;
	private int runningContainers;
	private long vcoreSeconds;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getFinalStatus() {
		return finalStatus;
	}

	public void setFinalStatus(String finalStatus) {
		this.finalStatus = finalStatus;
	}

	public double getProgress() {
		return progress;
	}

	public void setProgress(double progress) {
		this.progress = progress;
	}

	public String getTrackingUI() {
		return trackingUI;
	}

	public void setTrackingUI(String trackingUI) {
		this.trackingUI = trackingUI;
	}

	public String getTrackingUrl() {
		return trackingUrl;
	}

	public void setTrackingUrl(String trackingUrl) {
		this.trackingUrl = trackingUrl;
	}

	public String getDiagnostics() {
		return diagnostics;
	}

	public void setDiagnostics(String diagnostics) {
		this.diagnostics = diagnostics;
	}

	public String getClusterId() {
		return clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

	public String getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}

	public String getStartedTime() {
		return startedTime;
	}

	public void setStartedTime(String startedTime) {
		this.startedTime = startedTime;
	}

	public String getFinishedTime() {
		return finishedTime;
	}

	public void setFinishedTime(String finishedTime) {
		this.finishedTime = finishedTime;
	}

	public String getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(String elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public String getAmContainerLogs() {
		return amContainerLogs;
	}

	public void setAmContainerLogs(String amContainerLogs) {
		this.amContainerLogs = amContainerLogs;
	}

	public String getAmHostHttpAddress() {
		return amHostHttpAddress;
	}

	public void setAmHostHttpAddress(String amHostHttpAddress) {
		this.amHostHttpAddress = amHostHttpAddress;
	}

	public int getAllocatedMB() {
		return allocatedMB;
	}

	public void setAllocatedMB(int allocatedMB) {
		this.allocatedMB = allocatedMB;
	}

	public int getAllocatedVCores() {
		return allocatedVCores;
	}

	public void setAllocatedVCores(int allocatedVCores) {
		this.allocatedVCores = allocatedVCores;
	}

	public String getApplicationTags() {
		return applicationTags;
	}

	public void setApplicationTags(String applicationTags) {
		this.applicationTags = applicationTags;
	}

	public String getLogAggregationStatus() {
		return logAggregationStatus;
	}

	public void setLogAggregationStatus(String logAggregationStatus) {
		this.logAggregationStatus = logAggregationStatus;
	}

	public long getMemorySeconds() {
		return memorySeconds;
	}

	public void setMemorySeconds(long memorySeconds) {
		this.memorySeconds = memorySeconds;
	}

	public int getNumAMContainerPreempted() {
		return numAMContainerPreempted;
	}

	public void setNumAMContainerPreempted(int numAMContainerPreempted) {
		this.numAMContainerPreempted = numAMContainerPreempted;
	}

	public int getNumNonAMContainerPreempted() {
		return numNonAMContainerPreempted;
	}

	public void setNumNonAMContainerPreempted(int numNonAMContainerPreempted) {
		this.numNonAMContainerPreempted = numNonAMContainerPreempted;
	}

	public int getPreemptedResourceMB() {
		return preemptedResourceMB;
	}

	public void setPreemptedResourceMB(int preemptedResourceMB) {
		this.preemptedResourceMB = preemptedResourceMB;
	}

	public int getPreemptedResourceVCores() {
		return preemptedResourceVCores;
	}

	public void setPreemptedResourceVCores(int preemptedResourceVCores) {
		this.preemptedResourceVCores = preemptedResourceVCores;
	}

	public int getRunningContainers() {
		return runningContainers;
	}

	public void setRunningContainers(int runningContainers) {
		this.runningContainers = runningContainers;
	}

	public long getVcoreSeconds() {
		return vcoreSeconds;
	}

	public void setVcoreSeconds(long vcoreSeconds) {
		this.vcoreSeconds = vcoreSeconds;
	}
	public int compareTo(App o){
		int compareId = Integer.parseInt(o.getId().split("_")[2]);
		return  compareId - Integer.parseInt(this.id.split("_")[2]) ;
	}

	
}
