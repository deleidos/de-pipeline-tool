package com.deleidos.analytics.websocket;

import java.io.IOException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

import com.deleidos.analytics.common.util.ProfanityFilter;

/**
 * Abstract WebSocket server endpoint.  Extending classes must implement the onWebSocketText method.
 */
public class WebSocketServerEndpoint implements WebSocketListener {
	
	private static final Logger logger = Logger.getLogger(WebSocketServerEndpoint.class);
	
	private static final WebSocketServer webSocketServer = WebSocketServer.getInstance();
	private Session session;
	private String id;
	
	@Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
    }
 
    @Override
    public void onWebSocketClose(int statusCode, String reason) {
    	logger.info("WebSocket connection closed " + getId());
        this.session = null;
        webSocketServer.removeWebSocket(this);
    }
 
    @Override
    public void onWebSocketConnect(Session session) {
    	this.session = session;
    	this.id = String.format("%s.%s", session.getRemoteAddress().toString(), session.hashCode());
    	logger.info("WebSocket connection opened " + getId());
    	webSocketServer.addWebSocket(this);
    }
 
    @Override
    public void onWebSocketError(Throwable cause) {
        logger.error(cause);
    }
 
    public void onWebSocketText(String message) {
    	if ((session != null) && (session.isOpen())) {
    		webSocketServer.processMessage(message, getId());
        }
    }
    
    public synchronized void sendString(String message) {
    	try {
			session.getRemote().sendString(ProfanityFilter.filter(message));
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
}
