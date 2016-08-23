package com.deleidos.framework.model.event;

/**
 * This event is fired when an Apex app deployment is completed.
 * 
 * @author vernona
 */
public class DeploymentCompleteEvent {

	private String id;

	/**
	 * Constructor.
	 * 
	 * @param id
	 */
	public DeploymentCompleteEvent(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
