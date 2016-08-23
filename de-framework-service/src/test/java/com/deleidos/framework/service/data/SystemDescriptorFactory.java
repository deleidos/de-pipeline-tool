package com.deleidos.framework.service.data;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.framework.model.system.ApplicationDescriptor;
import com.deleidos.framework.model.system.OperatorDescriptor;
import com.deleidos.framework.model.system.SystemDescriptor;

/**
 * Factory class for getting example system descriptors.
 * 
 * @author vernona
 */
public class SystemDescriptorFactory {

	private static final String mappingJsonFilePath = "/example_mapping.json";

	private static final SystemDescriptorFactory instance = new SystemDescriptorFactory();

	private SystemDescriptorFactory() {
	}

	public static SystemDescriptorFactory getInstance() {
		return instance;
	}

	public SystemDescriptor getExampleSystemDescriptor() throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, String> exampleMapping = (Map<String, String>) JsonUtil.fromJsonString(
				IOUtils.toString(getClass().getResourceAsStream(mappingJsonFilePath), Charset.defaultCharset()),
				Map.class);

		ApplicationDescriptor application = ApplicationDescriptorFactory.getInstance()
				.getExampleApplicationDescriptor();

		Map<String, Map<String, String>> mappingOperatorMap = new HashMap<String, Map<String, String>>();
		for (OperatorDescriptor operator : application.getOperators()) {
			if (operator.getClassName().contains("JSONMappingOperator")) {
				mappingOperatorMap.put(operator.getName(), exampleMapping);
			}
		}

		SystemDescriptor system = new SystemDescriptor(UUID.randomUUID().toString(), "Example System", application,
				mappingOperatorMap, null);
		system.set_id(UUID.randomUUID().toString());

		return system;
	}
}