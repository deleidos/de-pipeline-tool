package com.deleidos.framework.operators.local.file.input;

import java.io.File;
import java.util.ArrayList;

import com.datatorrent.api.Context.OperatorContext;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.lib.io.SimpleSinglePortInputOperator;
import com.deleidos.framework.operators.common.FileInputTuple;
import com.deleidos.framework.operators.common.OperatorConfig;
import com.deleidos.framework.operators.common.OperatorSyslogger;
import com.deleidos.framework.operators.common.OperatorSystemInfo;
import com.google.common.io.Files;

public class LocalFileInputOperator extends SimpleSinglePortInputOperator<FileInputTuple>
		implements Runnable, OperatorSystemInfo {

	private String systemName;
	private transient OperatorSyslogger syslog;
	private String path;
	private File directory;
	private File[] directoryList;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;

	}

	public String getSystemName() {
		return this.systemName;
	}

	@Override
	public void setup(OperatorContext context) {
		syslog = new OperatorSyslogger(systemName, OperatorConfig.getInstance().getSyslogUdpHostname(),
				OperatorConfig.getInstance().getSyslogUdpPort());
		directory = new File(path);
		directoryList = directory.listFiles();
	}

	public transient DefaultOutputPort<FileInputTuple> output = new DefaultOutputPort<FileInputTuple>();

	@Override
	public void run() {
		try {
			for (File file : directoryList) {// files in folder
				FileInputTuple tup = new FileInputTuple();
				ArrayList<String> header = new ArrayList<String>();
				header.add(file.getName());
				tup.setHeader(header);

				tup.setData(Files.toByteArray(file));
				outputPort.emit(tup);
			}
		} catch (Exception e) {
			syslog.error("Error in Local File Input Operator: " + e.getMessage(), e);
		}
	}

}
