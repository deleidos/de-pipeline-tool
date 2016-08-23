package com.deleidos.framework.model.event;

import com.google.common.eventbus.EventBus;

public class SystemEventBus {

	private static final SystemEventBus instance = new SystemEventBus();

	/** The guava event bus. */
	private EventBus bus = new EventBus();

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static SystemEventBus getInstance() {
		return instance;
	}

	/**
	 * Private constructor enforces the singleton pattern.
	 */
	private SystemEventBus() {
	}

	/**
	 * Register an event handler. Event handler objects must have a method with the Subscribe annotation which accepts
	 * the event type to be handled.
	 * 
	 * TODO consider making events/handlers more strongly typed than the underlying Object-based Guava implementation.
	 * 
	 * @param handler
	 */
	public void registerHandler(Object handler) {
		bus.register(handler);
	}

	/**
	 * Deployment complete event notification.
	 * 
	 * @param id
	 *            the system ID
	 */
	public void deploymentComplete(String id) {
		bus.post(new DeploymentCompleteEvent(id));
	}
}
