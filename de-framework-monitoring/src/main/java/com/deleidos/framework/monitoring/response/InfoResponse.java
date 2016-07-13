package com.deleidos.framework.monitoring.response;

public class InfoResponse {
	
	public static final String PATH = "/proxy/${APP_ID}/ws/v2/stram/info";
	
	public static class Stats {
		public int allocatedContainers;
		public int plannedContainers;
		public int totalVCoresAllocated;
		public int vcoresRequired;
		public int memoryRequired;
		public int tuplesProcessedPSMA;
		public long totalTuplesProcessed;
		public int tuplesEmittedPSMA;
		public long totalTuplesEmitted;
		public int totalMemoryAllocated;
		public int totalBufferServerReadBytesPSMA;
		public int totalBufferServerWriteBytesPSMA;
		public int[] criticalPath;
		public int latency;
		public long windowStartMillis;
		public int numOperators;
		public int failedContainers;
		public long currentWindowId;
		public long recoveryWindowId;
	}
	
	public String name;
	public String user;
	public long startTime;
	public long elapsedTime;
	public String appPath;
	public String gatewayAddress;
	public boolean gatewayConnected;
	public Object[] appDataSources;
	public Object metrics;
	public Object attributes;
	public String appMasterTrackingUrl;
	public String version;
	public Stats stats;
	public String id;
	
	//public String state; // Can't get this from this request
}
