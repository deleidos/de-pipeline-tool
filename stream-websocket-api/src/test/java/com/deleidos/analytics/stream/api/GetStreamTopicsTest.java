package com.deleidos.analytics.stream.api;

import static org.junit.Assert.assertEquals;

import java.util.Queue;

import org.junit.BeforeClass;
import org.junit.Test;

public class GetStreamTopicsTest {

	@BeforeClass
	public static void before() {
		TestWebSocketClient.initServer();
	}

	@Test
	public void testGetStreamTopicsNoTopics() throws Exception {
		TestWebSocketClient client1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT, null);
		client1.connect();

		pause(500);
		Queue<String> received = client1.getReceivedMessages();
		// should not have recieved any messages yet
		assertEquals(0, received.size());

		// send the getStreamTopics message
		GetStreamTopics req = new GetStreamTopics();
		req.setRequest("getStreamTopics");
		client1.sendObject(req);

		// since we have no streamers, we should get a empty list of topics back
		pause(500);
		assertEquals(1, received.size());
		assertEquals("{\"topics\":[]}", received.poll());

		client1.close();
		pause(1000);
	}

	@Test
	public void testGetStreamTopicsOneTopic() throws Exception {
		TestWebSocketClient streamer1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT, new Stream("test"));
		streamer1.connect();

		pause(500);
		TestWebSocketClient client1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT, null);
		client1.connect();

		Queue<String> received = client1.getReceivedMessages();
		// should not have recieved any messages yet
		assertEquals(0, received.size());

		// send the getStreamTopics message
		GetStreamTopics req = new GetStreamTopics();
		req.setRequest("getStreamTopics");
		client1.sendObject(req);

		// since we have no streamers, we should get a empty list of topics back
		pause(500);
		assertEquals(1, received.size());
		assertEquals("{\"topics\":[\"test\"]}", received.poll());

		streamer1.close();
		pause(1000);
	}

	public void pause(long time) {
		try {
			Thread.sleep(time);
		}
		catch (InterruptedException ignore) {}
	}

}
