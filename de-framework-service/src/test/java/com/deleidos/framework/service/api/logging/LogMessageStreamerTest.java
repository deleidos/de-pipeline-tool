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
		InetAddress IPAddress = InetAddress.getByName("localhost");
		byte[] bytes = new byte[1024];
		String message = "Hello World";
		bytes = message.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, IPAddress, 1514);
		clientSocket.send(sendPacket);
		clientSocket.close();
	}
}
