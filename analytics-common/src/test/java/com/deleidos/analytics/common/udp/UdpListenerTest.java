package com.deleidos.analytics.common.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.junit.Test;

import com.deleidos.analytics.common.logging.LogUtil;
import com.deleidos.analytics.common.udp.UdpListener;

/**
 * UDP listener unit test.
 * 
 * @author vernona
 */
public class UdpListenerTest implements UdpMessageHandler {

	private static final int port = 1514;

	@Test
	public void testSyslogListener() throws Exception {
		LogUtil.initializeLog4jConsoleAppender();

		UdpListener listener = new UdpListener(port, this);
		listener.start();

		System.out.println("listener started");

		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("localhost");
		byte[] bytes = new byte[1024];
		String message = "Hello World";
		bytes = message.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, IPAddress, port);
		clientSocket.send(sendPacket);
		clientSocket.close();

		Thread.sleep(2000);
		listener.close();
	}

	@Override
	public void handleMessage(byte[] message) {
		System.out.println(new String(message));
	}
}
