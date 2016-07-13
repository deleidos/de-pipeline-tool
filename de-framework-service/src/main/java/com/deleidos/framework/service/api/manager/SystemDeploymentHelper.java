package com.deleidos.framework.service.api.manager;

import java.util.Map;

import com.deleidos.framework.model.system.ApplicationDescriptor;
import com.deleidos.framework.model.system.OperatorDescriptor;
import com.deleidos.framework.model.system.StreamDescriptor;
import com.deleidos.framework.model.system.StreamNode;
import com.deleidos.framework.model.system.SystemDescriptor;

/**
 * System deployment helper functionality.
 * 
 * @author vernona
 */
public class SystemDeploymentHelper {

	private static final String typeProperty = "type";
	private static final String classProperty = "class";

	private static final String sinkPortName = "input";
	private static final String sourcePortName = "output";

	/**
	 * Certain properties are required by the backend, but we don't want to expose them to the frontend. These
	 * properties are added to the system descriptor model here before deploying the system. We also don't want to have
	 * two different representation for what is basically the same data model.
	 */
	public void prepareSystemDescriptorForDeployment(SystemDescriptor system) {
		ApplicationDescriptor application = system.getApplication();
		for (OperatorDescriptor operator : application.getOperators()) {
			Map<String, Object> properties = operator.getProperties();
			String type = (String) properties.get(typeProperty);
			operator.setClassName(type);
			properties.put(classProperty, type);
			properties.remove(typeProperty);
		}

		for (StreamDescriptor stream : application.getStreams()) {
			stream.getSource().setPortName(sourcePortName);
			for (StreamNode sink : stream.getSinks()) {
				sink.setPortName(sinkPortName);
			}
		}
	}
}
