package com.deleidos.analytics.stream.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.analytics.stream.StreamManager;

public class StreamTest {

	private static final String STREAM_TOPIC_1 = "stream_topic_1";
	private static final String STREAM_TOPIC_2 = "stream_topic_2";

	private static List<String> streamMessages = new ArrayList<String>();
	private static List<String> expectedMessagesStream1 = new ArrayList<String>();
	private static List<String> expectedMessagesStream2 = new ArrayList<String>();

	@BeforeClass
	public static void before() throws Exception {
		TestWebSocketClient.initServer();

		for (int i = 1; i <= 10; i++) {
			TestStreamMessage stream = new TestStreamMessage(String.valueOf(i));
			streamMessages.add(JsonUtil.toJsonString(stream));

			TestConsumeMessage consume = new TestConsumeMessage(String.valueOf(i), STREAM_TOPIC_1);
			expectedMessagesStream1.add(JsonUtil.toJsonString(consume));

			TestConsumeMessage consume2 = new TestConsumeMessage(String.valueOf(i), STREAM_TOPIC_2);
			expectedMessagesStream2.add(JsonUtil.toJsonString(consume2));
		}
	}

	@Test
	public void testOneStreamerOneConsumer() throws Exception {
		TestWebSocketClient streamer1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT,
				new Stream(STREAM_TOPIC_1));
		streamer1.connect();
		assertTrue(doesStreamTopicExist(STREAM_TOPIC_1, 1000L));

		TestWebSocketClient consumer1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT,
				new Consume(STREAM_TOPIC_1));
		consumer1.connect();
		pause(500);

		// should not have received any messages yet
		List<String> received = consumer1.getReceivedMessagesAsList();
		assertEquals(0, received.size());

		// now lets send some test messages
		streamer1.sendMessages(streamMessages);

		pause(500);
		// check that we received all messages sent
		received = consumer1.getReceivedMessagesAsList();
		assertEquals(expectedMessagesStream1, received);

		streamer1.close();
		consumer1.close();
		pause(500);
	}

	@Test
	public void testOneStreamMultiConsumers() throws Exception {
		TestWebSocketClient streamer1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT,
				new Stream(STREAM_TOPIC_1));
		streamer1.connect();
		assertTrue(doesStreamTopicExist(STREAM_TOPIC_1, 1000L));

		TestWebSocketClient consumer1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT,
				new Consume(STREAM_TOPIC_1));
		consumer1.connect();

		TestWebSocketClient consumer2 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT,
				new Consume(STREAM_TOPIC_1));
		consumer2.connect();

		pause(500);

		// should not have received any messages yet
		List<String> received1 = consumer1.getReceivedMessagesAsList();
		assertEquals(0, received1.size());
		List<String> received2 = consumer2.getReceivedMessagesAsList();
		assertEquals(0, received2.size());

		// now lets send some test messages
		streamer1.sendMessages(streamMessages);

		pause(500);
		// check that we received all messages sent
		received1 = consumer1.getReceivedMessagesAsList();
		assertEquals(expectedMessagesStream1, received1);
		received2 = consumer2.getReceivedMessagesAsList();
		assertEquals(expectedMessagesStream1, received2);

		streamer1.close();
		consumer1.close();
		consumer2.close();
		pause(500);
	}

	@Test
	public void testMultiStreamsOneConsumer() throws Exception {
		TestWebSocketClient streamer1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT,
				new Stream(STREAM_TOPIC_1));
		streamer1.connect();
		assertTrue(doesStreamTopicExist(STREAM_TOPIC_1, 1000L));

		TestWebSocketClient streamer2 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT,
				new Stream(STREAM_TOPIC_2));
		streamer2.connect();
		assertTrue(doesStreamTopicExist(STREAM_TOPIC_2, 1000L));

		TestWebSocketClient consumer1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT, null);
		consumer1.connect();
		consumer1.sendObject(new Consume(STREAM_TOPIC_1));
		consumer1.sendObject(new Consume(STREAM_TOPIC_2));
		pause(500);

		// should not have received any messages yet
		List<String> received = consumer1.getReceivedMessagesAsList();
		assertEquals(0, received.size());

		// let streamer1 send some messages
		streamer1.sendMessages(streamMessages);

		pause(500);
		// check that we received all messages sent from streamer1
		received = consumer1.getReceivedMessagesAsList();
		assertEquals(expectedMessagesStream1, received);

		// let streamer2 send some messages
		streamer2.sendMessages(streamMessages);

		pause(500);
		// check that we received all messages sent from streamer2
		received = consumer1.getReceivedMessagesAsList();
		assertEquals(expectedMessagesStream2, received);

		streamer1.close();
		consumer1.close();
		pause(500);
	}

	// @Test
	public void testStream10KMessages() throws Exception {
		TestWebSocketClient streamer1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT,
				new Stream(STREAM_TOPIC_1));
		streamer1.connect();
		assertTrue(doesStreamTopicExist(STREAM_TOPIC_1, 1000L));

		TestWebSocketClient consumer1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT,
				new Consume(STREAM_TOPIC_1));
		consumer1.connect();
		pause(500);

		// should not have received any messages yet
		Queue<String> received = consumer1.getReceivedMessages();
		assertEquals(0, received.size());

		// now lets send some test messages
		for (int i = 1; i <= 10000; i++) {
			TestStreamMessage stream = new TestStreamMessage(String.valueOf(i));
			streamer1.sendObject(stream);
		}

		pause(2000);
		// check that we received all messages sent
		assertEquals(10000, received.size());

		streamer1.close();
		consumer1.close();
		pause(500);
	}

	@Test
	public void testStreamFilterMessages() throws Exception {
		TestWebSocketClient streamer1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT,
				new Stream(STREAM_TOPIC_1));
		streamer1.connect();
		assertTrue(doesStreamTopicExist(STREAM_TOPIC_1, 1000L));

		TestWebSocketClient consumer1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT,
				new Consume(STREAM_TOPIC_1));
		consumer1.connect();
		pause(500);

		// should not have received any messages yet
		Queue<String> received = consumer1.getReceivedMessages();
		assertEquals(0, received.size());

		// set the filter that should filter out all messages with "test" (all messages in this case)
		TestStreamFilter filter = new TestStreamFilter();
		StreamManager.getInstance().addStreamFilter(filter);

		// now lets send some test messages
		for (int i = 1; i <= 100; i++) {
			TestStreamMessage stream = new TestStreamMessage(String.valueOf(i));
			streamer1.sendObject(stream);
		}

		pause(2000);
		// check that all 1000 messages were filtered out
		assertEquals(0, received.size());

		// now remove the filter
		StreamManager.getInstance().removeStreamFilter(filter);

		streamer1.close();
		consumer1.close();
		pause(500);
	}

	@Test
	public void testTopicCreationDestruction() throws Exception {
		// make sure the topic doesn't exist before we start
		assertTrue(!doesStreamTopicExist(STREAM_TOPIC_1, 2000L));

		TestWebSocketClient streamer1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT,
				new Stream(STREAM_TOPIC_1));
		streamer1.connect();
		// created the streamer, now the topic should exist
		assertTrue(doesStreamTopicExist(STREAM_TOPIC_1, 2000L));

		// now close the streamer, and check that the topic goes away
		streamer1.close();
		pause(1000);
		assertTrue(!doesStreamTopicExist(STREAM_TOPIC_1, 2000L));
	}

	@Test
	public void testProfanityFilter() throws Exception {
		TestWebSocketClient streamer1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT,
				new Stream(STREAM_TOPIC_1));
		streamer1.connect();
		assertTrue(doesStreamTopicExist(STREAM_TOPIC_1, 1000L));

		TestWebSocketClient consumer1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT,
				new Consume(STREAM_TOPIC_1));
		consumer1.connect();
		pause(500);

		// should not have received any messages yet
		Queue<String> received = consumer1.getReceivedMessages();
		assertEquals(0, received.size());

		// now lets send some test messages
		TestStreamMessage stream = new TestStreamMessage("Is the fucking filter working?");
		streamer1.sendObject(stream);

		pause(500);
		// check that the filter worked
		String expectedStr = "[{\"test\":\"Is the f****** filter working?\",\"topic\":\"stream_topic_1\"}]";
		assertEquals(expectedStr, received.toString());

		streamer1.close();
		consumer1.close();
		pause(500);
	}

	private boolean doesStreamTopicExist(String topic, long maxWait) throws Exception {
		TestWebSocketClient client = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT, null);
		client.connect();
		GetStreamTopics request = new GetStreamTopics();
		request.setRequest("getStreamTopics");

		Queue<String> received = client.getReceivedMessages();
		boolean topicCreated = false;
		long start = System.currentTimeMillis();
		do {
			pause(200);
			client.sendObject(request);
			String message = received.poll();
			if (message != null) {
				TopicsWrapper topics = JsonUtil.fromJsonString(message, TopicsWrapper.class);
				if (topics.getTopics().contains(topic)) {
					topicCreated = true;
				}
			}
		} while ((!topicCreated) && (System.currentTimeMillis() - start < maxWait));

		return topicCreated;
	}

	public void pause(long time) {
		try {
			Thread.sleep(time);
		}
		catch (InterruptedException ignore) {}
	}

}
