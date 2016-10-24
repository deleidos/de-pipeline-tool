package com.deleidos.analytics.common.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.log4j.Logger;

/**
 * UDP socket listener.
 * 
 * @author vernona
 */
public class UdpListener extends Thread {

	private static final Logger log = Logger.getLogger(UdpListener.class);

	private DatagramSocket serverSocket;
	private boolean running = true;
	private UdpMessageHandler handler;

	/**
	 * Constructor.
	 * 
	 * @throws Exception
	 */
	public UdpListener(int port, UdpMessageHandler handler) throws Exception {
		serverSocket = new DatagramSocket(port);
		this.handler = handler;
	}

	/**
	 * Override Thread.run().
	 */
	@Override
	public void run() {
		try {
			byte[] bytes = new byte[1024];
			while (running) {
				DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
				serverSocket.receive(packet);
				handler.handleMessage(packet.getData());
				log.debug("message: " + new String(packet.getData()));
			}
		}
		catch (Throwable e) {
			if (!serverSocket.isClosed()) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Close the socket and exit.
	 */
	public void close() {
		running = false;
		if (serverSocket != null) {
			serverSocket.close();
		}
	}
}
