package com.deleidos.framework.operators.hdfs;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;

import com.datatorrent.api.Context;
import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.common.util.BaseOperator;
import com.deleidos.framework.operators.common.FileInputTuple;
import com.deleidos.framework.operators.common.OperatorConfig;
import com.deleidos.framework.operators.common.OperatorSyslogger;
import com.deleidos.framework.operators.common.OperatorSystemInfo;

public class HDFSOutputOperator extends BaseOperator implements OperatorSystemInfo {

	private String systemName;
	private transient OperatorSyslogger syslog;
	private transient FileSystem hdfs;

	// Configured parameters:
	private String hostName;
	private int port;
	private String path;

	public void setup(Context.OperatorContext context) {
		syslog = new OperatorSyslogger(systemName, OperatorConfig.getInstance().getSyslogUdpHostname(),
				OperatorConfig.getInstance().getSyslogUdpPort());
		try {
			Configuration configuration = new Configuration();
			configuration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
			hdfs = FileSystem.get(new URI("hdfs://" + hostName + ":" + port), configuration);
		}
		catch (Exception e) {
			syslog.error("Error in HDFS Output: " + e.getMessage(), e);
		}
	}

	public final transient DefaultInputPort<FileInputTuple> input = new DefaultInputPort<FileInputTuple>() {

		@Override
		public void process(FileInputTuple tuple) {
			try {
				byte[] data = tuple.getData();
				String fileName = tuple.getHeader().get(0);
				Path file = new Path("hdfs://" + hostName + ":" + port + "/" + path + "/" + fileName);
				OutputStream os = hdfs.create(file, true);
				os.write(data);
				os.close();
			}
			catch (Exception e) {
				syslog.error("Error in HDFS Output: " + e.getMessage(), e);
			}
		}
	};

	@Override
	public void teardown() {
		try {
			hdfs.close();
		}
		catch (IOException e) {
			syslog.error("Error in HDFS Output: " + e.getMessage(), e);
		}
	}

	@Override
	public String getSystemName() {
		return systemName;
	}

	@Override
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostName() {
		return this.hostName;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return this.port;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return this.path;
	}
}
