package com.deleidos.pipelinemanager.server.services;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import org.apache.log4j.Logger;

/**
 * 
 * @author shange
 * <p>
 * Shutdown active running application with its ID
 * </p>
 */
public class shutdownApp
{
	private static int waitTime = 500;		//#define wait time = 500ms
	private static final Logger logger = Logger.getLogger(shutdownApp.class);
	private static shutdownApp instance = new shutdownApp();
	public static shutdownApp getInstance(){
		return instance;
	}
	
	private String appID = "";
	public String getAppID(){
		return appID;
	}
	public shutdownApp setAppID(String i){
		this.appID = i;
		return this;
	}
	
	
	public String shutdown() throws Exception	{
		String output = "";
		try{
			ProcessBuilder pb = new ProcessBuilder("/usr/local/apex-core-3.3.0-incubating/engine/src/main/scripts/dtcli");
			Process p = pb.start();
	        OutputStream writeTo = p.getOutputStream();
	        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writeTo));
			Thread.sleep(waitTime*6);
			writer.write(("shutdown-app " + appID + "\n"));
			writer.newLine();
	        writer.flush();
			Thread.sleep(waitTime*6);
			writer.write("exit\n"); writer.newLine();
			output = "Killed: " + appID;
			System.out.println(output);
	        writer.flush();
	        writer.close();
	        writeTo.close();
			p.destroy();
		} catch(Throwable t){
			logger.error("Error shutting down app in apex:" + t.getMessage());
			throw t;
		}
		return output;
	}

}