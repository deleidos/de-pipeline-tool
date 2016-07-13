package com.deleidos.analytics.websocket;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
 
/**
 * Simple implementation of WebSocket client endpoint.  Just logs received messages and optional stores them in
 * a provided message queue.
 */
public class WebSocketClientEndpoint implements WebSocketListener {
	
	private static final Logger logger = Logger.getLogger(WebSocketClientEndpoint.class);
	
    private Session session;
    private String id;
    private LinkedBlockingQueue<String> queue;
 
    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
    }
 
    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        this.session = null;
    }
 
    @Override
    public void onWebSocketConnect(Session session) {
    	this.session = session;
    	this.id = String.format("%s.%s", session.getRemoteAddress().toString(), session.hashCode());
    }
 
    @Override
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace(System.err);
    }
 
    @Override
    public void onWebSocketText(String message) {
    	logger.info("Received message: " + message);
    	if (queue != null) {
    		queue.add(message);
    	}
    }
    
    public synchronized void sendString(String message) {
    	try {
			session.getRemote().sendString(message);
		} catch (IOException e) {
			logger.error("Failed to send message", e);
		}
    }
    
    public Future<Void> sendStringByFuture(String message) {
    	return session.getRemote().sendStringByFuture(message);
    }
    
    public String getId() {
    	return id;
    }
    
    public boolean isConnected() {
    	return (session != null) && (session.isOpen());
    }
    
    public void setMessageReceiveQueue(LinkedBlockingQueue<String> queue) {
		this.queue = queue;
	}
}