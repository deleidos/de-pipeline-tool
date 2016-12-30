package com.deleidos.pipelinemanager.server;

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

import com.deleidos.applicationcreator.applicationlauncher.AppLaunchConfig;

/**
 * 
 * @author shange
 *	<p>
 *	launches the APA application from the client node
 *	</p>
 */
public class AppLauncher{

	private static int waitTime = 500;		//#define wait time = 500ms
	private static final Logger logger = Logger.getLogger(AppLauncher.class);
	private static AppLauncher instance = new AppLauncher();
	private ExecutorService es = Executors.newCachedThreadPool();
	
	private volatile LinkedBlockingQueue<String> bufferedQueue = new LinkedBlockingQueue<String>();//for error debug checking
	protected volatile boolean cont;

	private AppLauncher() {
		cont = false;
	}
	
	public static AppLauncher getInstance()
	{
		return instance;
	}
	/**
	 * Queue an app for launch.
	 * 
	 * @param config
	 */
	public boolean launchApp(AppLaunchConfig config) {
		try {
			doLaunch(config);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void doLaunch(AppLaunchConfig config) throws Exception
	{
			
		String appBundleName = config.getAppBundleName();
		new LinkedBlockingQueue<String>(15);
		
		try{
			ProcessBuilder pb = new ProcessBuilder("/etc/profile.d/apex_env.sh");
			Process p = pb.start();
			p.destroy();
			pb = new ProcessBuilder("/usr/local/apex-core-3.3.0-incubating/engine/src/main/scripts/dtcli");
			p = pb.start();
			InputStream is = p.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            es.execute(new threadReader(reader,p));
            OutputStream writeTo = p.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writeTo));
            writer.flush();
            Thread.sleep(waitTime*6);
            writer.write("launch "+ appBundleName+"\n");
            writer.newLine();
            logger.debug("Launching");
            writer.flush();
            es.execute(new queueReader());
			while (!cont) {
				//wait for app to load
			}
            writer.write("exit");
            writer.newLine();
            writer.flush();
			Thread.sleep(waitTime);;
            writer.write("exit");
            writer.newLine();
            writer.flush();
			Thread.sleep(waitTime);;
			reader.close(); is.close();
			writer.close(); writeTo.close();
			p.destroy();
			p.destroyForcibly();			
			
			logger.debug("Launching: " + appBundleName);

			Runtime rt = Runtime.getRuntime();
			rt.exec("rm /tmp/"+appBundleName);
			rt.gc();
		}catch (Throwable t){
			logger.error("Error launching app in apex:" + t.getMessage());
			throw t;
		}
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
			while(process.isAlive()){
				try {
					String readLine= isbfr.readLine();
					bufferedQueue.put(readLine);
					logger.debug(readLine);
					if(readLine.contains("{\""))
					{
						cont = true;
						logger.debug("System launched: " + cont);
						break;
					}
				} catch (IOException e) {
					logger.error("Read dtcli cli error: " + e.getMessage());
				} catch (InterruptedException e) {
					//if interrupted, no big deal
				}
			}
			logger.debug("Process: " + process.isAlive());
			logger.debug("Process stream reader thread ended");
		}
	}
	
	private class queueReader implements Runnable{
		@Override
		public void run() {
			while(bufferedQueue.peek() != null){
				logger.debug(bufferedQueue.poll());
			}
		}
	}
}
