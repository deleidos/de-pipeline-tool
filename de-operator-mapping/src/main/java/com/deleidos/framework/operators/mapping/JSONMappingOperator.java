package com.deleidos.framework.operators.mapping;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.common.util.BaseOperator;
import com.deleidos.framework.operators.common.TupleUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
public class JSONMappingOperator extends BaseOperator{
	private String modelName;
	private String inputFormatName;
	private String modelVersion;
	private String modelPath;
	public void setModelName(String modelName){
		this.modelName = modelName;
	}
	public String getModelName(){
		return this.modelName;
	}
	public void setInputFormatName(String inputFormatName){
		this.inputFormatName = inputFormatName;
	}
	public String getInputFormatName(){
		return this.inputFormatName;
	}
	public void setModelVersion(String modelVersion){
		this.modelVersion = modelVersion;
	}
	public String getModelVersion(){
		return this.modelVersion;
	}
	public void setModelPath(String modelPath){
		this.modelPath = modelPath;
	}
	public String getModelPath(){
		return this.modelPath;
	}
	

	public transient DefaultOutputPort<Map<String, Object>> output = new DefaultOutputPort<Map<String, Object>>();
	
	public transient DefaultInputPort<Map<String, Object>> input = new DefaultInputPort<Map<String, Object>>() {
	
	    @Override
	    public void process(Map<String, Object> tuple)
	    {
	    
			try {
				processTuple(tuple);
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 

	    }
	  };
	  
	  protected void processTuple(Map<String, Object> tuple) throws ParseException, IOException{
		  SimpleConfigurableTranslator translator = new SimpleConfigurableTranslator();
		  translator.setModelName(modelName);
		  translator.setInputFormatName(modelName);
		  translator.setModelVersion(modelVersion);
	
		  
		  translator.loadDataModel(modelPath);
		  //this is just the path to the datamodel zip file, which is modelname.zip
		  translator.initialize();
		  JsonObject parsedData = null;
		  parsedData = translator.recordTranslation(tuple, null, null);
		  Map<String,Object> out = TupleUtil.jsonToTupleMap(parsedData.toString());
		  output.emit(out);

	  }
	  

	
	
}
