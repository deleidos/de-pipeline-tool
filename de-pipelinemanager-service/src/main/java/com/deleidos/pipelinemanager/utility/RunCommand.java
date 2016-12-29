package com.deleidos.pipelinemanager.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @author shange
 * <p>
 * Uses java Runtime to execute cmd commands programmatically. Used to launch and kill apps on the hadoop client node
 * </p>
 */
public class RunCommand
{
	private static RunCommand instance = new RunCommand();
	private static LinkedBlockingQueue<String> cmd;
	private static String status;
	private ArrayList<Integer> delayTimers = new ArrayList<Integer>();
	private ArrayList<String> delayKeywords = new ArrayList<String>();
	public static RunCommand getInstance()
	{
		return instance;
	}
	public void setRunCommand(LinkedBlockingQueue<String> c) throws Exception
	{
		cmd = c;
		status = run();
	}
	public static String returnStatus(){
		return status;
	}
		
	private String run() throws Exception
	{
		String cmdStat = "";
		Runtime rt = Runtime.getRuntime();
		Process proc = null;
		String line = null;
		Integer count = 0;
		while(!(cmd.isEmpty()))
		{
			try {
				proc = rt.exec(cmd.poll().toString());
				BufferedReader bfr =  new BufferedReader(new InputStreamReader(proc.getInputStream()));
				while((line=bfr.readLine())!= null)
				{
					System.out.println(line);
					if(count<delayKeywords.size())
					{
						while(!line.contains(delayKeywords.get(count)) && line!=null)
						{
							line=bfr.readLine();
							System.out.println(line);
							Thread.sleep(delayTimers.get(count));
						}
					}
					count++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				cmdStat = e.getMessage(); 
				return cmdStat;
			}
		}
		return cmdStat;
	}
	
	//Keywords for the command prompt to show, used to induce delay
	public RunCommand setDelayKeywords(ArrayList<String> d)
	{
		this.delayKeywords = d;
		return this;
	}
	//Induces artifically created delay
	public RunCommand setDelayTimer(ArrayList<Integer> t)
	{
		this.delayTimers = t;
		return this;
	}
		
}