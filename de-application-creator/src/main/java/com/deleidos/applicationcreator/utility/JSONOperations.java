package com.deleidos.applicationcreator.utility;

import com.deleidos.framework.model.system.SystemDescriptor;
import com.google.gson.Gson;

public class JSONOperations
{
	private static JSONOperations instance = new JSONOperations();
	public static JSONOperations getInstance(){
		return instance;
	}
	public String sysDescriptorToString(SystemDescriptor obj)
	{
		Gson gson = new Gson();
		String jsonInString = gson.toJson(obj);
		return jsonInString;
	}
	
	public SystemDescriptor stringToSysDescriptor(String obj)
	{
		Gson gson = new Gson();
		// 2. JSON to Java object, read it from a Json String.
		SystemDescriptor sys = gson.fromJson(obj, SystemDescriptor.class);
		return sys;
	}
}