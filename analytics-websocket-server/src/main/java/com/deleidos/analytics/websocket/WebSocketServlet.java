package com.deleidos.analytics.websocket;

import javax.servlet.annotation.WebServlet;

import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
 
@SuppressWarnings("serial")
@WebServlet(
        name = "WebSocketServlet",
        urlPatterns = "/analytics",
        loadOnStartup = 1)
public class WebSocketServlet extends org.eclipse.jetty.websocket.servlet.WebSocketServlet {
 
	@Override
    public void configure(WebSocketServletFactory factory) {
		factory.getPolicy().setIdleTimeout(1000 * 60 * 30);
        factory.register(WebSocketServerEndpoint.class);
        factory.getPolicy().setMaxTextMessageSize(Integer.MAX_VALUE);
    }
}