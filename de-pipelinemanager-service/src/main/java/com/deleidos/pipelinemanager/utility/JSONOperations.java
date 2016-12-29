package com.deleidos.pipelinemanager.utility;

import org.apache.log4j.Logger;

import com.deleidos.framework.model.system.SystemDescriptor;
import com.google.gson.Gson;

/**
 * 
 * @author shange
 * <p>
 * Miscellaneous JSON operations
 * </p>
 */
public class JSONOperations
{
	private static final Logger log = Logger.getLogger(JSONOperations.class);
	private static JSONOperations instance = new JSONOperations();
	public static JSONOperations getInstance(){
		return instance;
	}
	public String sysDescriptorToString(SystemDescriptor obj)
	{
		Gson gson = new Gson();
		String jsonInString = gson.toJson(obj);
		log.debug("Json-Op SystemDescriptor to string: " + jsonInString);
		return jsonInString;
	}
	
	public SystemDescriptor stringToSysDescriptor(String obj)
	{
		Gson gson = new Gson();
		// 2. JSON to Java object, read it from a Json String.
		SystemDescriptor sys = gson.fromJson(obj, SystemDescriptor.class);
		log.debug("Json-Op SystemDescriptor from string: " + sys);
		return sys;
	}
}