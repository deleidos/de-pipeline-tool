package com.deleidos.framework.monitoring.response;

public class PhysicalPlan_OperatorsResponse {
	
	public static final String PATH = "/proxy/${APP_ID}/ws/v2/stram/physicalPlan/operators";
	
	public static class Operator {
		
		public static class Port {
			public String name;
			public String type;
			public long totalTuples;
			public int tuplesPSMA;
			public int bufferServerBytesPSMA;
			public int queueSizeMA;
			public long recordingId;
		}
		
		public int id;
		public String name;
		public String className;
		public String container;
		public String host;
		public long totalTuplesProcessed;
		public long totalTuplesEmitted;
		public int tuplesProcessedPSMA;
		public int tuplesEmittedPSMA;
		public double cpuPercentageMA;
		public int latencyMA;
		public String status;
		public long lastHeartbeat;
		public int failureCount;
		public long recoveryWindowId;
		public long currentWindowId;
		public Port[] ports;
		public String unifierClass;
		public String logicalName;
		public long recordingId;
		public Object counters;
		public Object metrics;
		public long checkpointStartTime;
		public int checkpointTime;
		public int checkpointTimeMA;
	}
	
	public Operator[] operators;
}
