package com.deleidos.framework.monitoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

/**
 * Created by mollotb on 7/21/16.
 */
public class EventLogger implements Iterator<String> {
    private static final Path CORE_SITE_PATH = new Path("/"),
                              HDFS_SITE_PATH = new Path("/");
    private static final String APP_ROOT = "/user/hdfs/datatorrent/apps/";

    private int index = 0;
    private long lastUpdate = 0;
    private List<String> log = new ArrayList<String>();

    private String appId;
    private FileSystem fs;

    public EventLogger(String appId) throws Exception {
        this.appId = appId;

        this.fs = LogUtil.fs;
    }

    public void updateLog() throws Exception {
        Path logPath = new Path(APP_ROOT + appId + "/events/");
        RemoteIterator<LocatedFileStatus> ri = fs.listFiles(logPath, false);
        LocatedFileStatus lfs;
        long time = lastUpdate;
        while (ri.hasNext()) {
            lfs = ri.next();
            if (lfs.getModificationTime() > lastUpdate) {
                FSDataInputStream in = fs.open(lfs.getPath());

                for (String line : in.readUTF().split("\n")) {
                    if (Long.valueOf(line.substring(0, line.indexOf(":"))) > lastUpdate) {
                        log.add(line);
                    }
                }

                in.close();
                time = Math.max(time, lfs.getModificationTime());
            }
        }
    }

    public boolean hasNext() {
        return index < log.size();
    }

    public String next() {
        return log.get(index++);
    }

}
