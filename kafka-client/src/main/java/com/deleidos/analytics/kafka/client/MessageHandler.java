package com.deleidos.analytics.kafka.client;

public interface MessageHandler {

	public void handleMessage(String message) throws Exception;
}
