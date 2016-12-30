package com.deleidos.framework.monitoring;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.function.ToDoubleFunction;

import com.deleidos.analytics.common.rest.RestClient;
import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.framework.monitoring.response.AppsResponse;
import com.deleidos.framework.monitoring.response.App;
import com.deleidos.framework.monitoring.response.InfoResponse;
import com.deleidos.framework.monitoring.response.InfoResponse.Stats;
import com.deleidos.framework.monitoring.response.PhysicalPlan_ContainersResponse;
import com.deleidos.framework.monitoring.response.PhysicalPlan_ContainersResponse.Container;
import com.deleidos.framework.monitoring.response.PhysicalPlan_OperatorsResponse;
import com.deleidos.framework.monitoring.response.PhysicalPlan_OperatorsResponse.Operator;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MonitoringUtil {

	private RestClient rc;
	private AppsResponse aResponse;
	private InfoResponse iResponse;
	private PhysicalPlan_ContainersResponse ppcResponse;
	private PhysicalPlan_OperatorsResponse ppoResponse;
	private long aTime = 0, iTime = 0, ppcTime = 0, ppoTime = 0, cacheTime = 2000;
	private String iAppId = "", ppcAppId = "", ppoAppId = "";

	public MonitoringUtil(String apexNameNodeHostname) {
		rc = new RestClient(
				String.format("http://%s:8088", apexNameNodeHostname));
	}

	// Note: These methods will throw exceptions if their GET requests fail.
	// The requests are expected to fail if the given app ID refers to an
	// application that doesn't support a given call.
	private void updateAResponse() throws Exception {
		if (System.currentTimeMillis() > aTime + cacheTime) {
			aResponse = rc.getObject(AppsResponse.PATH, AppsResponse.class);
			aTime = System.currentTimeMillis();
		}
	}

	private void updateIResponse(String appId) throws Exception {
		System.out.println(appId);
		if (!iAppId.equals(appId) || System.currentTimeMillis() > iTime + cacheTime) {
			iResponse = rc.getObject(InfoResponse.PATH.replace("${APP_ID}", appId), InfoResponse.class);
			iTime = System.currentTimeMillis();
			iAppId = appId;
		}
	}

	private void updatePpcResponse(String appId) throws Exception {
		if (!ppcAppId.equals(appId) || System.currentTimeMillis() > ppcTime + cacheTime) {
			ppcResponse = rc.getObject(PhysicalPlan_ContainersResponse.PATH.replace("${APP_ID}", appId),
					PhysicalPlan_ContainersResponse.class);
			ppcTime = System.currentTimeMillis();
			ppcAppId = appId;
		}
	}

	private void updatePpoResponse(String appId) throws Exception {
		if (!ppoAppId.equals(appId) || System.currentTimeMillis() > ppoTime + cacheTime) {
			ppoResponse = rc.getObject(PhysicalPlan_OperatorsResponse.PATH.replace("${APP_ID}", appId),
					PhysicalPlan_OperatorsResponse.class);
			ppoTime = System.currentTimeMillis();
			ppoAppId = appId;
		}
	}

	@SuppressWarnings("rawtypes")
	private Object getSummedIField(AppValue v) throws Exception {
		updateAResponse();
		if (aResponse.getApps() != null) {
			for (App a : aResponse.getApps().getApp()) {
				updateIResponse(a.getId());
				if (a.getState().equals("RUNNING"))
					v.addValueOf(a);
			}
		}
		return v.getValue();
	}

	@SuppressWarnings("rawtypes")
	private Object getSummedPpoField(AppValue v) throws Exception {
		updateAResponse();
		if (aResponse.getApps() != null) {
			for (App a : aResponse.getApps().getApp()) {
				updatePpoResponse(a.getId());
				if (a.getState().equals("RUNNING"))
					v.addValueOf(a);
			}
		}
		return v.getValue();
	}

	public Object getAField(String field) throws Exception {
		updateAResponse();
		return AppsResponse.class.getField(field).get(aResponse);
	}

	public Object getIField(String appId, String field) throws Exception {
		updateIResponse(appId);
		return InfoResponse.class.getField(field).get(iResponse);
	}

	public Object getIStatsField(String appId, String field) throws Exception {
		updateIResponse(appId);
		return Stats.class.getField(field).get(InfoResponse.class.getField("stats").get(iResponse));
	}

	public Object getCField(String appId, int containerIndex, String field) throws Exception {
		updatePpcResponse(appId);
		return Container.class.getField(field).get(((Container[]) PhysicalPlan_ContainersResponse.class
				.getField("containers").get(ppoResponse))[containerIndex]);
	}

	public Object getOField(String appId, int operatorIndex, String field) throws Exception {
		updatePpoResponse(appId);
		return Container.class.getField(field).get(((Operator[]) PhysicalPlan_OperatorsResponse.class
				.getField("operators").get(ppcResponse))[operatorIndex]);
	}

	// Returns total VCores allocated to applications
	public int getCpuCoreCount() throws Exception {
		return (Integer) getSummedIField(new AppValue<Integer>() {
			int sum = 0;

			public void addValueOf(App a) {
				sum += iResponse.stats.totalVCoresAllocated;
			}

			public Integer getValue() {
				return sum;
			}
		});
	}

	// Returns utilized cores across all applications
	public double getCpuUsage() throws Exception {
		return (Double) getSummedPpoField(new AppValue<Double>() {
			double sum = 0;

			public void addValueOf(App a) {
				sum += Arrays.stream(ppoResponse.operators).parallel().mapToDouble(new ToDoubleFunction<Operator>() {
					public double applyAsDouble(Operator x) {
						return x.cpuPercentageMA;
					}
				}).sum();
			}

			public Double getValue() {
				return sum;
			}
		});
	}

	// Returns total allocated memory of all applications in MB
	public int getCurrentlyAllocatedMemory() throws Exception {
		return (Integer) getSummedIField(new AppValue<Integer>() {
			int sum;

			public void addValueOf(App a) {
				sum += iResponse.stats.totalMemoryAllocated;
			}

			public Integer getValue() {
				return sum;
			}
		});
	}

	// Returns total allocated memory for each application as JSON
	public String getCurrentlyAllocatedMemoryApps() throws Exception {
		ObjectNode root = JsonNodeFactory.instance.objectNode();
		ArrayNode data = root.putArray("data");
		data.addAll((ArrayNode) getSummedIField(new AppValue<Object>() {
			ArrayNode data = JsonNodeFactory.instance.arrayNode();

			public void addValueOf(App a) {
				ObjectNode o = JsonNodeFactory.instance.objectNode();
				o.put("label", a.getName());
				o.put("value", iResponse.stats.totalMemoryAllocated);
				data.add(o);
			}

			public Object getValue() {
				return data;
			}
		}));
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		new ObjectMapper().writeTree(new JsonFactory().createGenerator(stream), root);
		return stream.toString(java.nio.charset.StandardCharsets.UTF_8.name());
	}

	// Returns a JSON object containing an array of objects representing the
	// running operators in {name: string, cpuPercentageMA: number} form
	public String getAppCpuUsage(String appId) throws Exception {
		updatePpoResponse(appId);
		ObjectNode root = JsonNodeFactory.instance.objectNode();
		ArrayNode data = root.putArray("operators");
		for (Operator o : ppoResponse.operators) {
			ObjectNode node = data.addObject();
			node.put("name", o.name);
			node.put("cpuPercentageMA", o.cpuPercentageMA);
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		new ObjectMapper().writeTree(new JsonFactory().createGenerator(stream), root);
		return stream.toString(java.nio.charset.StandardCharsets.UTF_8.name());
	}

	// Returns total preempted memory of all applications in MB
	public int getPreemptedMemory() throws Exception {
		return (Integer) getSummedIField(new AppValue<Integer>() {
			int sum;

			public void addValueOf(App a) {
				sum += iResponse.stats.memoryRequired;
			}

			public Integer getValue() {
				return sum;
			}
		});
	}

	// Returns count of running, pending, failed, finished, killed, and
	// total(submitted) applications as JSON
	public String getApplicationCounts() throws Exception {
		int running = 0, pending = 0, failed = 0, finished = 0, killed = 0, submitted = 0;
		updateAResponse();
		if (aResponse.getApps() != null) {
			for (App a : aResponse.getApps().getApp()) {
				if (a.getState().equals("RUNNING"))
					running++;
				if (a.getState().equals("PENDING"))
					pending++;
				if (a.getState().equals("FAILED"))
					failed++;
				if (a.getState().equals("FINISHED"))
					finished++;
				if (a.getState().equals("KILLED"))
					killed++;
			}
		}
		submitted = running + pending + failed + finished + killed;
		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectNode root = factory.objectNode();
		ArrayNode data = root.putArray("data");
		ObjectNode r = factory.objectNode();
		r.put("label", "Running");
		r.put("value", running);
		r.put("color", "#008000");
		ObjectNode p = factory.objectNode();
		p.put("label", "Pending");
		p.put("value", pending);
		p.put("color", "#1E90FF");
		ObjectNode f = factory.objectNode();
		f.put("label", "Failed");
		f.put("value", failed);
		f.put("color", "#8B0000");
		ObjectNode fi = factory.objectNode();
		fi.put("label", "Finished");
		fi.put("value", finished);
		fi.put("color", "#696969");
		ObjectNode k = factory.objectNode();
		k.put("label", "Killed");
		k.put("value", killed);
		k.put("color", "#8B4513");
		ObjectNode s = factory.objectNode();
		s.put("label", "Submitted");
		s.put("value", submitted);
		s.put("color", "#2F4F4F");
		data.add(r);
		data.add(p);
		data.add(f);
		data.add(fi);
		data.add(k);
		data.add(s);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		new ObjectMapper().writeTree(new JsonFactory().createGenerator(stream), root);
		return stream.toString(java.nio.charset.StandardCharsets.UTF_8.name());
	}

	// Returns total count of running containers across all applications
	public int getContainerCount() throws Exception {
		return (Integer) getSummedIField(new AppValue<Integer>() {
			int sum;

			public void addValueOf(App a) {
				sum += iResponse.stats.allocatedContainers;
			}

			public Integer getValue() {
				return sum;
			}
		});
	}

	// Returns total count of running operators across all applications
	public int getOperatorCount() throws Exception {
		return (Integer) getSummedIField(new AppValue<Integer>() {
			int sum;

			public void addValueOf(App a) {
				sum += iResponse.stats.numOperators;
			}

			public Integer getValue() {
				return sum;
			}
		});
	}

	// Returns total number of processed tuples across all applications
	public int getProcessedTuplesCount() throws Exception {
		return (Integer) getSummedIField(new AppValue<Integer>() {
			int sum;

			public void addValueOf(App a) {
				sum += iResponse.stats.totalTuplesProcessed;
			}

			public Integer getValue() {
				return sum;
			}
		});
	}

	// Returns total number of emitted tuples across all applications
	public int getEmittedTuplesCount() throws Exception {
		return (Integer) getSummedIField(new AppValue<Integer>() {
			int sum;

			public void addValueOf(App a) {
				sum += iResponse.stats.totalTuplesEmitted;
			}

			public Integer getValue() {
				return sum;
			}
		});
	}

	// Returns number of processed tuples per second across all applications
	public int getProcessedTuplesPerSecond() throws Exception {
		return (Integer) getSummedIField(new AppValue<Integer>() {
			int sum;

			public void addValueOf(App a) {
				sum += iResponse.stats.tuplesProcessedPSMA;
			}

			public Integer getValue() {
				return sum;
			}
		});
	}

	// Returns number of emitted tuples per second across all applications
	public int getEmittedTuplesPerSecond() throws Exception {
		return (Integer) getSummedIField(new AppValue<Integer>() {
			int sum;

			public void addValueOf(App a) {
				sum += iResponse.stats.tuplesEmittedPSMA;
			}

			public Integer getValue() {
				return sum;
			}
		});
	}

	// Returns cursory info about all apps in an array via JSON
	public String getAppInfo() throws Exception {
		updateAResponse();
		return JsonUtil.toJsonString(aResponse);
	}

	@Deprecated
	public String getAppListDEPRECATED() throws Exception {
		System.out.println(aResponse);
		updateAResponse();
		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectNode root = factory.objectNode();
		ArrayNode data = root.putArray("apps");
		if (aResponse != null && aResponse.getApps() != null) {
			for (App a : aResponse.getApps().getApp()) {
				ObjectNode o = data.addObject();
				o.setAll((ObjectNode) JsonUtil.parseJson(JsonUtil.toJsonString(a)));
			}
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		new ObjectMapper().writeTree(new JsonFactory().createGenerator(stream), root);
		return stream.toString(java.nio.charset.StandardCharsets.UTF_8.name());
	}

	public String getAppSummaryByName(String appName) throws Exception {
		updateAResponse();
		App firstOnline = null, firstOffline = null;
		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectNode root = factory.objectNode();
		if (aResponse != null && aResponse.getApps() != null) {
			for (App a : aResponse.getApps().getApp()) {
				if (a.getName().equals(appName)) {
					if (a.getState().equals("RUNNING") && firstOnline == null)
						firstOnline = a;
					if (!a.getState().equals("RUNNING") && firstOffline == null)
						firstOffline = a;
				}
			}
		}
		App a;
		if (firstOnline == null)
			a = firstOffline;
		else
			a = firstOnline;
		if (a == null)
			return "{}";
		updateIResponse(a.getId());
		root.put("name", a.getName());
		root.put("user", a.getUser());
		root.put("state", a.getState());
		root.put("startedTime", a.getStartedTime());
		root.put("progress", a.getProgress());
		root.put("applicationType", a.getApplicationType());
		root.put("elapsedTime", a.getElapsedTime());
		root.put("finishedTime", a.getFinishedTime());
		root.put("finalStatus", a.getFinalStatus());
		root.put("id", a.getId());
		root.put("queue", a.getQueue());
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		new ObjectMapper().writeTree(new JsonFactory().createGenerator(stream), root);
		return stream.toString(java.nio.charset.StandardCharsets.UTF_8.name());
	}

	// Given an app's name, returns its ID if it is on Apex. If the app is not
	// found, returns the empty string.
	// Tries to return the ID of the youngest running app of the specified name,
	// and falls back to the youngest
	// stopped/finished/killed app of the specified name.
	public String getAppIdByName(String appName) throws Exception {
		updateAResponse();
		String firstOffline = "", firstOnline = "";
		if (aResponse != null && aResponse.getApps() != null) {
			for (App a : aResponse.getApps().getApp()) {
				if (a.getName().equals(appName)) {
					if (a.getState().equals("RUNNING") && firstOnline.equals(""))
						firstOnline = a.getId();
					if (!a.getState().equals("RUNNING") && firstOffline.equals(""))
						firstOffline = a.getId();
				}
			}
		}
		if (firstOnline.equals(""))
			return firstOffline;
		return firstOnline;
	}

	// Returns detailed info about a single specified app via JSON
	public String getAppDetails(String appId) throws Exception {
		updateIResponse(appId);
		return JsonUtil.toJsonString(iResponse);
	}

	// Returns an array of containers belonging to the specified app via JSON
	public String getContainers(String appId) throws Exception {
		updatePpcResponse(appId);
		return JsonUtil.toJsonString(ppcResponse);
	}
}
