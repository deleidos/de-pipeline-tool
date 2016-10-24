package com.deleidos.framework.service.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.deleidos.analytics.common.util.GsonFactory;
import com.deleidos.framework.model.system.ApplicationDescriptor;

/**
 * Factory class for getting example application descriptors.
 * 
 * @author vernona
 */
public class ApplicationDescriptorFactory {

	private static final String apexAppConfigFilePath = "/example_apex_app_config.json";

	private static final ApplicationDescriptorFactory instance = new ApplicationDescriptorFactory();

	private ApplicationDescriptorFactory() {
	}

	public static ApplicationDescriptorFactory getInstance() {
		return instance;
	}

	public ApplicationDescriptor getExampleApplicationDescriptor() throws Exception {
		return GsonFactory.getInstance().getGson().fromJson(loadExampleJson(),
				ApplicationDescriptor.class);
	}

	private String loadExampleJson() throws Exception {
		InputStream stream = getClass().getResourceAsStream(apexAppConfigFilePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line = null;
		StringBuffer buffer = new StringBuffer();
		while ((line = br.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();
	}
}
