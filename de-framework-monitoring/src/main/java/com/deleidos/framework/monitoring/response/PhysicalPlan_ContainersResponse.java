package com.deleidos.framework.monitoring.response;

public class PhysicalPlan_ContainersResponse {
	
	public static final String PATH = "/proxy/${APP_ID}/ws/v2/stram/physicalPlan/containers";
	
	public static class Container {
		public String id;
		public String host;
		public String state;
		public String jvmName;
		public long lastHeartbeat;
		public int numOperators;
		public int memoryMBAllocated;
		public int memoryMBFree;
		public long gcCollectionTime;
		public int gcCollectionCount;
		public String containerLogsUrl;
		public long startedTime;
		public long finishedTime;
		public String rawContainerLogsUrl;
	}
	
	public Container[] containers;
}
