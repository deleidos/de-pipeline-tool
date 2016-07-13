package com.deleidos.analytics.stream.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.deleidos.analytics.common.datetime.TimeWindow;
import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.analytics.pubsub.api.Content;
import com.deleidos.analytics.pubsub.api.Epoch;
import com.deleidos.analytics.pubsub.api.GetPubSubTopics;
import com.deleidos.analytics.pubsub.api.Pub;
import com.deleidos.analytics.pubsub.api.SessionId;
import com.deleidos.analytics.pubsub.api.Sub;
import com.deleidos.analytics.pubsub.api.TimeStamp;
import com.deleidos.analytics.pubsub.api.TopicsWrapper;
import com.deleidos.analytics.pubsub.query.TestPubSubQueryCallable;

public class PubSubTest {

	private static List<Integer> receiveCountAtTick;
	private static List<String> minuteTestTicks;
	private static List<String> hourTestTicks;
	private static List<String> dayTestTicks;
	private static List<String> monthTestTicks;
	private static List<String> yearTestTicks;

	@BeforeClass
	public static void before() {
		TestWebSocketClient.initServer();

		// used to check how many messages we have received (cumulative) for every tick sent
		// to keep things simpler, for all windows we plan on receiving a message on the 3rd and 6th tick
		receiveCountAtTick = Arrays.asList(new Integer[] { 0, 0, 1, 1, 1, 2 });

		// the 3rd and 6th ticks cross the minute "window"
		minuteTestTicks = Arrays.asList(
				new String[] { "2015-09-09T19:02:26.977Z", "2015-09-09T19:02:52.999Z", "2015-09-09T19:03:10.139Z",
						"2015-09-09T19:03:23.139Z", "2015-09-09T19:03:44.139Z", "2015-09-09T19:04:10.139Z" });

		// the 3rd and 6th ticks cross the hour "window"
		hourTestTicks = Arrays.asList(
				new String[] { "2015-09-09T19:02:26.977Z", "2015-09-09T19:42:52.999Z", "2015-09-09T20:03:10.139Z",
						"2015-09-09T20:23:23.139Z", "2015-09-09T20:44:44.139Z", "2015-09-09T21:04:10.139Z" });

		// the 3rd and 6th ticks cross the day "window"
		dayTestTicks = Arrays.asList(
				new String[] { "2015-09-09T19:02:26.977Z", "2015-09-09T22:42:52.999Z", "2015-09-10T02:03:10.139Z",
						"2015-09-10T10:23:23.139Z", "2015-09-10T16:44:44.139Z", "2015-09-11T05:04:10.139Z" });

		// the 3rd and 6th ticks cross the month "window"
		monthTestTicks = Arrays.asList(
				new String[] { "2015-09-09T19:02:26.977Z", "2015-09-15T22:42:52.999Z", "2015-10-10T02:03:10.139Z",
						"2015-10-16T10:23:23.139Z", "2015-10-22T16:44:44.139Z", "2015-11-11T05:04:10.139Z" });

		// the 3rd and 6th ticks cross the year "window"
		yearTestTicks = Arrays.asList(
				new String[] { "2015-09-09T19:02:26.977Z", "2015-11-15T22:42:52.999Z", "2016-02-10T02:03:10.139Z",
						"2016-06-16T10:23:23.139Z", "2016-10-22T16:44:44.139Z", "2017-04-11T05:04:10.139Z" });
	}

	@Test
	public void testPubSubMinuteEpoch() throws Exception {
		TestWebSocketClient pub1 = createPublisher(TestPubSubQueryCallable.UNIT_TEST_TOPIC, TimeWindow.MINUTE,
				"testing");
		TestWebSocketClient sub1 = createSubscriber(TestPubSubQueryCallable.UNIT_TEST_TOPIC, TimeWindow.MINUTE,
				pub1.getSessionId());

		Queue<String> received = sub1.getReceivedMessages();
		assertEquals(0, received.size());

		// should receive a message at the 3rd and 6th tick (that's when the minute changes)
		checkSubMessageCount(pub1, received, minuteTestTicks, receiveCountAtTick);

		pub1.close();
		sub1.close();
		pause(500);
	}

	@Test
	public void testPubSubHourEpoch() throws Exception {
		TestWebSocketClient pub1 = createPublisher(TestPubSubQueryCallable.UNIT_TEST_TOPIC, TimeWindow.HOUR, "testing");
		TestWebSocketClient sub1 = createSubscriber(TestPubSubQueryCallable.UNIT_TEST_TOPIC, TimeWindow.HOUR,
				pub1.getSessionId());

		Queue<String> received = sub1.getReceivedMessages();
		assertEquals(0, received.size());

		// should receive a message at the 3rd and 6th tick (that's when the hour changes)
		checkSubMessageCount(pub1, received, hourTestTicks, receiveCountAtTick);

		pub1.close();
		sub1.close();
		pause(500);
	}

	@Test
	public void testPubSubDayEpoch() throws Exception {
		TestWebSocketClient pub1 = createPublisher(TestPubSubQueryCallable.UNIT_TEST_TOPIC, TimeWindow.DAY, "testing");
		TestWebSocketClient sub1 = createSubscriber(TestPubSubQueryCallable.UNIT_TEST_TOPIC, TimeWindow.DAY,
				pub1.getSessionId());

		Queue<String> received = sub1.getReceivedMessages();
		assertEquals(0, received.size());

		// should receive a message at the 3rd and 6th tick (that's when the day changes)
		checkSubMessageCount(pub1, received, dayTestTicks, receiveCountAtTick);

		pub1.close();
		sub1.close();
		pause(500);
	}

	@Test
	public void testPubSubMonthEpoch() throws Exception {
		TestWebSocketClient pub1 = createPublisher(TestPubSubQueryCallable.UNIT_TEST_TOPIC, TimeWindow.MONTH,
				"testing");
		TestWebSocketClient sub1 = createSubscriber(TestPubSubQueryCallable.UNIT_TEST_TOPIC, TimeWindow.MONTH,
				pub1.getSessionId());

		Queue<String> received = sub1.getReceivedMessages();
		assertEquals(0, received.size());

		// should receive a message at the 3rd and 6th tick (that's when the month changes)
		checkSubMessageCount(pub1, received, monthTestTicks, receiveCountAtTick);

		pub1.close();
		sub1.close();
		pause(500);
	}

	@Test
	public void testPubSubYearEpoch() throws Exception {
		TestWebSocketClient pub1 = createPublisher(TestPubSubQueryCallable.UNIT_TEST_TOPIC, TimeWindow.YEAR, "testing");
		TestWebSocketClient sub1 = createSubscriber(TestPubSubQueryCallable.UNIT_TEST_TOPIC, TimeWindow.YEAR,
				pub1.getSessionId());

		Queue<String> received = sub1.getReceivedMessages();
		assertEquals(0, received.size());

		// should receive a message at the 3rd and 6th tick (that's when the year changes)
		checkSubMessageCount(pub1, received, yearTestTicks, receiveCountAtTick);

		pub1.close();
		sub1.close();
		pause(500);
	}

	@Test
	public void testPubSubMultipleSessions() throws Exception {
		TestWebSocketClient pub1 = createPublisher(TestPubSubQueryCallable.UNIT_TEST_TOPIC, TimeWindow.MINUTE,
				"testing");
		TestWebSocketClient pub2 = createPublisher(TestPubSubQueryCallable.UNIT_TEST_TOPIC, TimeWindow.MINUTE,
				"testing");
		TestWebSocketClient pub3 = createPublisher(TestPubSubQueryCallable.UNIT_TEST_TOPIC, TimeWindow.MINUTE,
				"testing");

		TestWebSocketClient sub1 = createSubscriber(TestPubSubQueryCallable.UNIT_TEST_TOPIC, TimeWindow.MINUTE,
				pub1.getSessionId());
		TestWebSocketClient sub2 = createSubscriber(TestPubSubQueryCallable.UNIT_TEST_TOPIC, TimeWindow.MINUTE,
				pub2.getSessionId());
		TestWebSocketClient sub3 = createSubscriber(TestPubSubQueryCallable.UNIT_TEST_TOPIC, TimeWindow.MINUTE,
				pub3.getSessionId());

		// all subs should not have any messages yet
		Queue<String> received1 = sub1.getReceivedMessages();
		assertEquals(0, received1.size());
		Queue<String> received2 = sub2.getReceivedMessages();
		assertEquals(0, received2.size());
		Queue<String> received3 = sub3.getReceivedMessages();
		assertEquals(0, received3.size());

		// should receive a message at the 3rd and 6th tick (that's when the minute changes)
		checkSubMessageCount(pub1, received1, minuteTestTicks, receiveCountAtTick);
		// only sub1 should have its 2 messages now
		assertEquals(2, received1.size());
		assertEquals(0, received2.size());
		assertEquals(0, received3.size());

		// should receive a message at the 3rd and 6th tick (that's when the minute changes)
		checkSubMessageCount(pub2, received2, minuteTestTicks, receiveCountAtTick);
		// sub1 should still have only 2, and now sub2 should have 2 as well
		assertEquals(2, received1.size());
		assertEquals(2, received2.size());
		assertEquals(0, received3.size());

		// should receive a message at the 3rd and 6th tick (that's when the minute changes)
		checkSubMessageCount(pub3, received3, minuteTestTicks, receiveCountAtTick);
		// finally all 3 subs should have 2 messages
		assertEquals(2, received1.size());
		assertEquals(2, received2.size());
		assertEquals(2, received3.size());

		pub1.close();
		pub2.close();
		sub1.close();
		sub2.close();
		pause(500);
	}

	private TestWebSocketClient createPublisher(String topic, TimeWindow window, String content) throws Exception {
		TestWebSocketClient pub1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT, null);
		pub1.connect();
		pub1.sendObject(new Pub(topic));
		assertTrue(doesPubSubTopicExist(pub1.getSessionId(), topic, 1000L));

		pub1.sendObject(new Content(content));
		pub1.sendObject(new Epoch(window.toString()));

		Queue<String> received = pub1.getReceivedMessages();
		assertEquals(1, received.size());
		SessionId sessionId = JsonUtil.fromJsonString(received.poll(), SessionId.class);
		pub1.setSessionId(sessionId.getSessionId());

		return pub1;
	}

	private TestWebSocketClient createSubscriber(String topic, TimeWindow window, String sessionId) throws Exception {
		TestWebSocketClient sub1 = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT, null);
		sub1.connect();
		sub1.sendObject(new Sub(topic, window.toString(), sessionId));
		pause(500);

		Queue<String> received = sub1.getReceivedMessages();
		assertEquals(0, received.size());
		return sub1;
	}

	private void checkSubMessageCount(TestWebSocketClient pub, Queue<String> received, List<String> timeTicks,
			List<Integer> receiveCountAtTick) throws Exception {
		for (int i = 0; i < timeTicks.size(); i++) {
			pub.sendObject(new TimeStamp(timeTicks.get(i)));
			pause(500);
			assertEquals("Receive sub message count off at tick " + (i + 1), receiveCountAtTick.get(i).intValue(),
					received.size());
		}
	}

	private boolean doesPubSubTopicExist(String session, String topic, long maxWait) throws Exception {
		TestWebSocketClient client = new TestWebSocketClient(TestWebSocketClient.WS_ENDPOINT, null);
		client.connect();
		GetPubSubTopics request = new GetPubSubTopics("getPubSubTopics", session);

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
