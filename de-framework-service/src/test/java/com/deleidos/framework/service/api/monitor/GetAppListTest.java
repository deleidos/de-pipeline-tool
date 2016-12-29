package com.deleidos.framework.service.api.monitor;

import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Test;

import com.deleidos.framework.monitoring.HadoopYarnApiClient;
import com.deleidos.framework.service.config.ServiceConfig;

public class GetAppListTest {

	@Test
	public void getAppListTest(){
		HadoopYarnApiClient client = new HadoopYarnApiClient(
				String.format("http://%s:8088", ServiceConfig.getInstance().getHadoopNameNodeHostname()));
		try {
			System.out.println(client.getApexApps().size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		
	}
	
	
}
