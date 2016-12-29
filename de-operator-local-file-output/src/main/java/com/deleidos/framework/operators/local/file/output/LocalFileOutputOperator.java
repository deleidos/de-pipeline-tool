package com.deleidos.framework.operators.local.file.output;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import com.datatorrent.api.Context;
import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.common.util.BaseOperator;
import com.deleidos.framework.operators.common.FileInputTuple;
import com.deleidos.framework.operators.common.OperatorConfig;
import com.deleidos.framework.operators.common.OperatorSyslogger;
import com.deleidos.framework.operators.common.OperatorSystemInfo;

public class LocalFileOutputOperator extends BaseOperator implements OperatorSystemInfo {
	
	
	private transient OperatorSyslogger syslog;
	private String systemName;
	private transient final Logger log = Logger.getLogger(LocalFileOutputOperator.class);

	private String path;
	public final transient DefaultInputPort<FileInputTuple> input = new DefaultInputPort<FileInputTuple>() {

		@Override
		public void process(FileInputTuple tuple) {
			try {
				log.info("made it into processing");
				
				byte[] data = tuple.getData();
				String fileName = tuple.getHeader().get(0);
				Path path2 = Paths.get(path + "/" + fileName);
				Files.write(path2, data);

			} catch (Exception e) {
				log.error("Error in Output: " + e.getMessage(), e);
				syslog.error("Error in Output: " + e.getMessage(), e);
			}

		}

	};
	public void setup(Context.OperatorContext context) {
		syslog = new OperatorSyslogger(systemName, OperatorConfig.getInstance().getSyslogUdpHostname(),
				OperatorConfig.getInstance().getSyslogUdpPort());
		try {
			super.setup(context);

		}catch(Exception e){
			log.error("Error in Output: " + e.getMessage(), e);

			syslog.error("Error in Output: " + e.getMessage(),e);
		}
	}
	public void setSystemName(String systemName) {
		this.systemName = systemName;
		
	}
	public String getSystemName() {
		return this.systemName;
	}

	public void setPath(String path){
		this.path = path;
	}
	public String getPath(){
		return this.path;
	}
	
}
