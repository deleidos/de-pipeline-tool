package com.deleidos.framework.service.api.logging;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Not a unit test per se. Run this class while running the data service app in eclipse to generate a test message.
 * Also, open a consumer from the browser web socket client to see the message flow end to end.
 * 
 * @author vernona
 */
public class LogMessageStreamerTest {

	public static void main(String[] args) throws Exception {
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress address = InetAddress.getByName("ec2-54-226-195-61.compute-1.amazonaws.com");
		byte[] bytes = new byte[1024];
		String message = "Hello World";
		bytes = message.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, address, 1514);
		clientSocket.send(sendPacket);
		clientSocket.close();
	}
}
