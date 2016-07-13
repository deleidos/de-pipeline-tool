package com.deleidos.framework.operators.elasticsearch;

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
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ElasticSearchOutputJsonOperator extends BaseOperator{

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
	public void setIdField(String idField){
		this.idField = idField;
	}
	public String getIdField(){
		return this.idField;
	}
	public void setIndexName(String indexName){
		this.indexName = indexName;
	}
	public String getIndexName(){
		return this.indexName;
	}
	public void setType(String type){
		this.type = type;
	}
	public String getType(){
		return this.type;
	}
	public void setBatchSize(int batchSize){
		this.batchSize = batchSize;
	}
	public int getBatchSize(){
		return this.batchSize;
	}
	public void setClusterName(String clusterName){
		this.clusterName = clusterName;
	}
	public String getClusterName(){
		return this.clusterName;
	}
	public void setClusterHostnames(String[] clusterHostnames){
		this.clusterHostnames = clusterHostnames;
	}
	public String[] getClusterHostnames(){
		return this.clusterHostnames;
	}
		
	
	
	public void setup(Context.OperatorContext context) {
			
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
			
			
	}
	public final transient DefaultInputPort<String> input = new DefaultInputPort<String>()
	  {

		@Override
		public void process(String tuple) {
	
			  Gson gson = new Gson();
			  JsonElement element = gson.fromJson (tuple, JsonElement.class);
			  JsonObject jsonObj = element.getAsJsonObject();
		      processTuple(jsonObj);
		      
		    
		}


	  };
	  public void processTuple(JsonObject tuple) {
				this.indexName = this.indexName.toLowerCase();
				IndexUtil.instance.createIndex(this.indexName, null, null, client);
				
				tupleBatch.add(tuple);
				if (tupleBatch.size() >= batchSize) {
					
					processBatch();
				}
			
			
			
		}
	  public void processBatch(){
		  while (!tupleBatch.isEmpty()) {
		    	
		      JsonObject tuple = tupleBatch.remove();
		      bulkRequest.add(client.prepareIndex(this.indexName, this.type, UUID.randomUUID().toString()).setSource(tuple.toString()));
		  }
		  if(bulkRequest.numberOfActions() > 0){
		  BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		    if (bulkResponse.hasFailures()) {
		      DTThrowable.rethrow(new Exception(bulkResponse.buildFailureMessage()));
		    }
		    bulkRequest = client.prepareBulk();
		  }
	  }
	  @Override
	  public void endWindow()
	  {
	    super.endWindow();
	    
	    processBatch();
	 
	    
	  }
	
}
