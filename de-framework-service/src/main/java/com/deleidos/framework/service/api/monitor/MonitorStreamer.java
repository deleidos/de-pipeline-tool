//package com.deleidos.framework.service.api.monitor;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.deleidos.analytics.common.util.JsonUtil;
//import com.deleidos.analytics.stream.StreamManager;
//import com.deleidos.analytics.websocket.WebSocketServer;
//import com.deleidos.framework.monitoring.MonitoringUtil;
//
//public class MonitorStreamer extends Thread {
//	
//	public static class Apps {
//		public String[] apps;
//	}
//	
//	public static MonitorStreamer instance = new MonitorStreamer();
//	
//	// TODO: Use wait time as min elapsed time between refreshes, rather than as sleep time
//	private static long appListWaitTime = 10000, appSummaryWaitTime = 1000;
//	private static Map<String, Thread> threads = new HashMap<String, Thread>();
//	
//	public void run() {
//		while (true) {
//			if (StreamManager.getInstance().getConsumersIds(MonitorServiceMessageFactory.monitorStreamWebSocketId).isEmpty()) {
//				try {Thread.sleep(appSummaryWaitTime); // This is _not_ as mistake
//				} catch (InterruptedException e) {e.printStackTrace();}
//			}
//			else {
//				String[] apps = new String[0];
//				try {
//					apps = JsonUtil.fromJsonString(MonitoringUtil.getAppList(), Apps.class).apps;
//				} catch (Exception e) {e.printStackTrace();}
//				for (int i = 0; i < apps.length; i++) {
//					final String appName = apps[i];
//					if (!threads.containsKey(appName)) {
//						threads.put(appName, new Thread(new Runnable(){
//							public void run() {
//								while (true) {
//									List<String> consumerIds = StreamManager.getInstance().getConsumersIds(MonitorServiceMessageFactory.monitorStreamWebSocketId);
//									if (!consumerIds.isEmpty()) {
//										String message = "{}";
//										try {message = MonitoringUtil.getAppSummaryByName(appName);
//										} catch (Exception e) {e.printStackTrace();}
//										WebSocketServer.getInstance().send(message, consumerIds);
//									}
//									try {Thread.sleep(appSummaryWaitTime);
//									} catch (InterruptedException e) {e.printStackTrace();}
//								}
//							}
//						}));
//						threads.get(appName).start();
//					}
//				}
//				for (String key : threads.keySet()) {
//					boolean contains = false;
//					for (String appName : apps) {
//						if (appName.equals(key)) contains = true;
//					}
//					if (!contains) threads.remove(key);
//				}
//				try {Thread.sleep(appListWaitTime);
//				} catch (InterruptedException e) {e.printStackTrace();}
//			}
//		}
//	}
//}
