package com.deleidos.pipelinemanager.server.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.mortbay.log.Log;

import com.deleidos.applicationcreator.applicationlauncher.AppLaunchConfig;

/**
 * 
 * @author shange
 * <p>
 * Kill launched application with application ID
 * </p>
 */
public class killApp 
{
	private static int waitTime = 500;		//#define wait time = 500ms
	private static final Logger logger = Logger.getLogger(killApp.class);
	private static killApp instance = new killApp();
	private LinkedBlockingQueue<AppLaunchConfig> queue;
	private ExecutorService es = Executors.newCachedThreadPool();
	private volatile LinkedBlockingQueue<String> bufferedQueue = new LinkedBlockingQueue<String>();//for error debug checking

	protected volatile boolean cont;
	
	public static killApp getInstance(){
		return instance;
	}
	
	private String appID = "";
	public String getAppID(){
		return appID;
	}
	public killApp setAppID(String i){
		this.appID = i;
		return this;
	}
	
	
	public String kill() throws Exception	{
		String output = "";
		try
		{
			logger.info("begun killing process");
			ProcessBuilder pb = new ProcessBuilder("/usr/local/apex-core-3.3.0-incubating/engine/src/main/scripts/dtcli");
			Process p = pb.start();
			InputStream is = p.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//	        es.submit(new queueReader(reader,p));
	        es.execute(new threadReader(reader,p));
	        OutputStream writeTo = p.getOutputStream();
	        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writeTo));
			Thread.sleep(waitTime*6);
			writer.write(("kill-app " + appID + "\n"));
			writer.newLine();
	        writer.flush();
//	        logger.info("waiting for app to kill");
//			while (!cont) {
//				//wait for app to kill
//			}
	        Thread.sleep(waitTime*4);
//			logger.info("after kill loop");
			writer.write("exit\n"); writer.newLine();
			output = "Killed: " + appID;
	        writer.flush();
			writer.write("exit\n"); writer.newLine();
			logger.debug(output);
	        writer.flush();
			reader.close(); is.close();
			writer.close(); writeTo.close();
			p.destroy();
//			logger.info("everything is done in killApp");
		} catch(Throwable t){
			logger.error("Error killing app in apex:" + t.getMessage());
			throw t;
		}
		return output;
	}	

	private class threadReader implements Runnable{
		private transient BufferedReader isbfr;
		private transient Process process;
		public threadReader(BufferedReader isbfr, Process process){
			this.process= process;
			this.isbfr=isbfr;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(!cont){
				try {
					
//					String readLine= isbfr.readLine();
					if(!bufferedQueue.isEmpty() && bufferedQueue.peek()!=null)
					{
						logger.debug(bufferedQueue.peek());
						if(bufferedQueue.poll().contains("Kill app requested"))
						{
							cont = true;
							logger.debug("System launched: " + cont);
//							bufferedQueue.clear();
							break;
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Read dtcli cli error: " + e.getMessage(),e);
				}
			}
//			logger.debug("finished threadreader thread");
		}
	}
		
	//Deprecated
	/*private class queueReader implements Runnable{
		private BufferedReader bfr;
		private transient Process process;
		public queueReader(BufferedReader bfr, Process proc){
			this.bfr=bfr;
			this.process=proc;
		}

		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				while(!cont){
					bufferedQueue.put(bfr.readLine());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}*/
}