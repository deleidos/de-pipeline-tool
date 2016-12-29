package com.deleidos.framework.service.api.monitor;

import org.junit.Test;

import com.deleidos.framework.monitoring.MonitoringUtil;

import org.junit.Assert;

public class GetAppDetailsTest {
	@Test
	public void getAppDetailsTest(){
		try {
			System.out.println(MonitoringUtil.getAppDetails("application_1481640257635_0001"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}
}
