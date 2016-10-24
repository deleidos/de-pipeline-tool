package com.deleidos.analytics.common.udp;

/**
 * Common interface for handling UDP messages in the listener.
 * 
 * @author vernona
 */
public interface UdpMessageHandler {

	/**
	 * Handle the UDP message packet.
	 * 
	 * @param message
	 */
	public void handleMessage(byte[] message);
}
