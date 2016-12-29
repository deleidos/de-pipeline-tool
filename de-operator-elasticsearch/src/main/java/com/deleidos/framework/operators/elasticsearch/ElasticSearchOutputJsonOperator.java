package com.deleidos.framework.operators.elasticsearch;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.datatorrent.api.Context;
import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.common.util.BaseOperator;
import com.datatorrent.netlet.util.DTThrowable;
import com.deleidos.analytics.common.logging.Syslogger;
import com.deleidos.framework.operators.common.OperatorConfig;
import com.deleidos.framework.operators.common.OperatorSyslogger;
import com.deleidos.framework.operators.common.OperatorSystemInfo;
import com.deleidos.framework.operators.common.TupleUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ElasticSearchOutputJsonOperator extends BaseOperator implements OperatorSystemInfo {

	private String idField;
	private String indexName;
	private String type;
	private int batchSize = 1000;
	protected transient Queue<JsonObject> tupleBatch;
	private String clusterName;
	private String[] clusterHostnames;
	private transient TransportClient client;
	private transient BulkRequestBuilder bulkRequest;
	private static final Logger log = Logger.getLogger(ElasticSearchOutputJsonOperator.class);

	private String systemName;
	private transient OperatorSyslogger syslog;

	public void setIdField(String idField) {
		this.idField = idField;
	}

	public String getIdField() {
		return this.idField;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public String getIndexName() {
		return this.indexName;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public int getBatchSize() {
		return this.batchSize;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getClusterName() {
		return this.clusterName;
	}

	public void setClusterHostnames(String[] clusterHostnames) {
		this.clusterHostnames = clusterHostnames;
	}

	public String[] getClusterHostnames() {
		return this.clusterHostnames;
	}

	@Override
	public String getSystemName() {
		return systemName;
	}

	@Override
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public void setup(Context.OperatorContext context) {
		syslog = new OperatorSyslogger(systemName, OperatorConfig.getInstance().getSyslogUdpHostname(),
				OperatorConfig.getInstance().getSyslogUdpPort());
		try {
			super.setup(context);
			Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
			client = new TransportClient(settings);
			for (String hostname : clusterHostnames) {
				if (!hostname.isEmpty()) {
					client.addTransportAddress(new InetSocketTransportAddress(hostname, 9300));
				}
			}

			bulkRequest = client.prepareBulk();
			tupleBatch = new ArrayBlockingQueue<JsonObject>(batchSize);
		} catch (Exception e) {
			syslog.error("Error in ElasticSearch Output: " + e.getMessage(), e);
		}

	}

	public final transient DefaultInputPort<Map<String, Object>> input = new DefaultInputPort<Map<String, Object>>() {

		@Override
		public void process(Map<String, Object> tuple) {
			try {
				String jsonTup = TupleUtil.tupleMapToJson(tuple);
				Gson gson = new Gson();
				JsonElement element = gson.fromJson(jsonTup, JsonElement.class);
				JsonObject jsonObj = element.getAsJsonObject();
				processTuple(jsonObj);
			} catch (Exception e) {
				syslog.error("Error in ElasticSearch Output: " + e.getMessage(), e);
			}

		}

	};

	public void processTuple(JsonObject tuple) {
		try {
			this.indexName = this.indexName.toLowerCase();
			IndexUtil.instance.createIndex(this.indexName, null, null, client);

			tupleBatch.add(tuple);
			if (tupleBatch.size() >= batchSize) {

				processBatch();
			}
		} catch (Exception e) {
			syslog.error("Error in ElasticSearch Output: " + e.getMessage(), e);

		}

	}

	public void processBatch() {
		try {
			while (!tupleBatch.isEmpty()) {

				JsonObject tuple = tupleBatch.remove();
				bulkRequest.add(client.prepareIndex(this.indexName, this.type, UUID.randomUUID().toString())
						.setSource(tuple.toString()));
			}
			if (bulkRequest.numberOfActions() > 0) {
				BulkResponse bulkResponse = bulkRequest.execute().actionGet();
				if (bulkResponse.hasFailures()) {
					DTThrowable.rethrow(new Exception(bulkResponse.buildFailureMessage()));
				}
				bulkRequest = client.prepareBulk();
			}
		} catch (Exception e) {
			syslog.error("Error in ElasticSearch Output: " + e.getMessage(), e);

		}
	}

	@Override
	public void endWindow() {
		super.endWindow();

		processBatch();

	}
}
