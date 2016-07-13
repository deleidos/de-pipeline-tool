package com.deleidos.analytics.stream;

import com.codahale.metrics.health.HealthCheck;
import com.deleidos.analytics.stream.api.Consume;
import com.deleidos.analytics.stream.api.Stream;
import com.deleidos.analytics.websocket.WebSocketClient;

public class StreamApiHealthCheck extends HealthCheck {
    
    @Override
    protected Result check() throws Exception {
        WebSocketClient sender = new WebSocketClient();
        WebSocketClient receiver = new WebSocketClient();
        
        sender.sendObject(new Stream("stream_api_health_check"));
        Thread.sleep(200);
        receiver.sendObject(new Consume("stream_api_health_check"));
        Thread.sleep(200);
        sender.sendString("{\"stream_api\":\"health_check\"}");
        Thread.sleep(200);
        
        String message = receiver.getNextReceivedMessage();
        sender.close();
        receiver.close();
        
        if (message != null) {
        	return Result.healthy();
        } else {
        	return Result.unhealthy("Failed to receive health_check message on stream_api_health_check topic");
        }
    }
}