package com.deleidos.framework.monitoring;

import java.util.List;
import com.deleidos.framework.monitoring.LogUtil;

/**
 * Created by mollotb on 7/26/16.
 */
public class LogTest {

    public static void main(String[] args) {
    	String appId = "application_1470051483978_0015";
        try {
        	System.out.println("Init FS");
        	LogUtil.initFS();
        	System.out.println("FS init done");
            List<String> logs;
            logs = LogUtil.listLogs(appId);
            System.out.println(logs.size());
            logs.forEach(log -> System.out.println(log));
            
            System.out.println(LogUtil.getLog(appId, logs.get(0)));
            
            LogUtil.getLogger(appId).forEachRemaining(event -> {System.out.println(event);});
            
            
        } catch (Exception e) {e.printStackTrace();}
    }

}
