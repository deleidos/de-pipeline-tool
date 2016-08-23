package com.deleidos.framework.monitoring;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.log4j.Logger;

/**
 * Created by mollotb on 7/21/16.
 */
public class LogUtil {

	private static final Logger LOG = Logger.getLogger(LogUtil.class);
    private static final String CORE_SITE_PATH = "resources/core-site.xml",
            HDFS_SITE_PATH = "resources/hdfs-site.xml";
    private static final String LOG_ROOT = "/tmp/logs/hdfs/logs/";
    
    public static FileSystem fs;

    static Map<String, EventLogger> loggers = new HashMap<String, EventLogger>();
    
    static {
    	try {
			initFS();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void initFS() throws Exception {
    	Configuration conf = new Configuration();
    	conf.addResource(CORE_SITE_PATH);
    	conf.addResource(HDFS_SITE_PATH);
    	
    	LOG.info("Initializing FileSystem");
    	
		fs = FileSystem.get(conf);
		
		LOG.info("Initialized FileSystem");
    }

    public static EventLogger getLogger(String appId) throws Exception {
        if (!loggers.containsKey(appId)) {
            loggers.put(appId, new EventLogger(appId));
        }
        return loggers.get(appId);
    }

    public static List<String> listLogs(String appId) throws Exception {
    	LOG.info("listLogs called");
        RemoteIterator<LocatedFileStatus> ri = fs.listFiles(new Path(LOG_ROOT + appId), false);
        System.out.println("RemoteIterator created");
        ArrayList<String> ret = new ArrayList<String>();
        while (ri.hasNext()) {
            ret.add(ri.next().getPath().getName());
        }

        return ret;
    }

    public static String getLog(String appId, String logName) throws Exception {
        InputStream in = fs.open(new Path(LOG_ROOT + appId + "/" + logName));
        StringWriter writer = new StringWriter();
        IOUtils.copy(in, writer, "UTF-8");
        in.close();
        return writer.toString();
    }
}
