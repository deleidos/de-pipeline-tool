package com.deleidos.analytics.websocket.event;

import java.util.ArrayList;

import com.google.common.eventbus.EventBus;

public class ServiceEventBus {

	private static final ServiceEventBus instance = new ServiceEventBus();

	/** The guava event bus. */
	private EventBus bus = new EventBus();

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static ServiceEventBus getInstance() {
		return instance;
	}

	/**
	 * Private constructor enforces the singleton pattern.
	 */
	private ServiceEventBus() {
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
	 * New consumer event notification.
	 * 
	 * @param id
	 * 				the consumer ID
	 * @param topic
	 * 				the topic
	 */
	public void newConsumer(String id, String topic) {
		bus.post(new NewConsumerEvent(id, topic));
	}
}
