package com.deleidos.framework.service.api.monitor;

import org.junit.Test;

import com.deleidos.framework.monitoring.MonitoringUtil;
import com.deleidos.framework.service.config.ServiceConfig;

import org.junit.Assert;

public class GetAppDetailsTest {
	@Test
	public void getAppDetailsTest(){
		try {
			MonitoringUtil util = new MonitoringUtil(ServiceConfig.getInstance().getHadoopNameNodeHostname());
			System.out.println(util.getAppDetails("application_1481640257635_0001"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}
}
