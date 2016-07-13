package com.deleidos.analytics.pubsub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.datetime.TimeWindow;
import com.deleidos.analytics.pubsub.query.PubSubQueryCallable;
import com.deleidos.analytics.pubsub.query.PubSubQueryParams;
import com.deleidos.analytics.pubsub.query.PubSubQueryRunner;

public class PubSubManager {

	private static final Logger logger = Logger.getLogger(PubSubManager.class);
	
	private final CopyOnWriteArrayList<PubSubQueryCallable> pubSubQueryCallableList = 
			new CopyOnWriteArrayList<PubSubQueryCallable>();
	private final PubSubQueryRunner pubSubQueryRunner = new PubSubQueryRunner();
	
	private static class SingletonHolder {
		private static final PubSubManager INSTANCE = new PubSubManager();
	}

	public static PubSubManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	// sessionId|PubSubSession
	private final Map<String, PubSubSession> pubSubSessionMap = 
			new ConcurrentHashMap<String, PubSubSession>(16, 0.9f, 1);
	
	// sessionId|(Topic|Windows Map)
	private final Map<String, Map<String, List<TimeWindow>>> sessionTopicWindowCache = 
			new ConcurrentHashMap<String, Map<String, List<TimeWindow>>>(16, 0.9f, 1);
	
	private PubSubManager() {};
	
	public void addPublisher(String webSocketId, String topic) {
		if (!pubSubSessionMap.containsKey(webSocketId)) {
			PubSubSession session = new PubSubSession(webSocketId);
			pubSubSessionMap.put(webSocketId, session);
		}
		PubSubSession session = pubSubSessionMap.get(webSocketId);
		if (!session.getPublisher().getTopics().contains(topic)) {
			session.getPublisher().getTopics().add(topic);
		}
		sessionTopicWindowCache.remove(webSocketId);
	}
	
	public void addSubscriber(String webSocketId, String topic, TimeWindow window, String sessionId) {
		if (pubSubSessionMap.containsKey(sessionId)) {
			PubSubSession session = pubSubSessionMap.get(sessionId);
			Subscriber subscriber = new Subscriber(webSocketId, topic, window, sessionId);
			if (!session.getSubscribers().contains(subscriber)) {
				session.getSubscribers().add(subscriber);
			} else {
				logger.warn("Ignoring duplicate sub message: " + subscriber);
			}
			sessionTopicWindowCache.remove(sessionId);
		} else {
			logger.warn("Tried to sub to a non-existing sessionId: " + sessionId);
		}
	}
	
	public void removePublisherSubscriber(String webSocketId) {
		if (pubSubSessionMap.containsKey(webSocketId)) {
			pubSubSessionMap.remove(webSocketId);
			sessionTopicWindowCache.remove(webSocketId);
		}
	}
	
	public Set<String> getUniqueTopics(String sessionId) {
		return getUniqueTopicWindowCombinations(sessionId).keySet();
	}
	
	public Map<String, List<TimeWindow>> getUniqueTopicWindowCombinations(String sessionId) {
		Map<String, List<TimeWindow>> topicWindowMap = sessionTopicWindowCache.get(sessionId);
		if (topicWindowMap == null) {
			PubSubSession session = pubSubSessionMap.get(sessionId);
			if (session != null) {
				if (session.getSubscribers() != null && session.getSubscribers().size() > 0) {
					topicWindowMap = new HashMap<String, List<TimeWindow>>();
					for (Subscriber subscriber: session.getSubscribers()) {
						if (!topicWindowMap.containsKey(subscriber.getTopic())) {
							topicWindowMap.put(subscriber.getTopic(), new ArrayList<TimeWindow>());
						}
						if (!topicWindowMap.get(subscriber.getTopic()).contains(subscriber.getWindow())) {
							topicWindowMap.get(subscriber.getTopic()).add(subscriber.getWindow());
						}
					}
					sessionTopicWindowCache.put(sessionId, topicWindowMap);
				}
			}
		}
		return topicWindowMap;
	}
	
	public List<String> getSubscriberIds(String sessionId, String topic, TimeWindow window) {
		List<String> subscriberIds = null;
		PubSubSession session = pubSubSessionMap.get(sessionId);
		if (session != null) {
			List<Subscriber> subscribers = session.getSubscribers();
			if (subscribers != null && !subscribers.isEmpty()) {
				subscriberIds = new ArrayList<String>();
				for (Subscriber subscriber: subscribers) {
					if (subscriber.getTopic().equals(topic) && subscriber.getWindow().equals(window)) {
						subscriberIds.add(subscriber.getWebSocketId());
					}
				}
			}
		}
		return subscriberIds;
	}
	
	public void setPublisherTimeStamp(String webSocketId, String timeStamp) {
		PubSubSession session = getPubSubSession(webSocketId);
		if (session != null) {
			Publisher publisher = session.getPublisher();
			if (publisher != null) {
				publisher.setTimeStamp(timeStamp);
			}
		} else {
			logger.warn("No session found for websocket: " + webSocketId + ", cannot set Timestamp");
		}
	}
	public String getPublisherTimeStamp(String webSocketId) {
		PubSubSession session = pubSubSessionMap.get(webSocketId);
		if (session != null) {
			Publisher publisher = session.getPublisher();
			if (publisher != null) {
				return publisher.getTimeStamp();
			}
		}
		return null;
	}
	
	public void setPublisherContent(String webSocketId, String content) {
		PubSubSession session = getPubSubSession(webSocketId);
		if (session != null) {
			Publisher publisher = session.getPublisher();
			if (publisher != null) {
				publisher.setContent(content);
			}
		} else {
			logger.warn("No session found for websocket: " + webSocketId + ", cannot set Content");
		}
	}
	
	public String getPublisherContent(String webSocketId) {
		PubSubSession session = pubSubSessionMap.get(webSocketId);
		if (session != null) {
			Publisher publisher = session.getPublisher();
			if (publisher != null) {
				return publisher.getContent();
			}
		}
		return null;
	}
	
	public void setPublisherEpoch(String webSocketId, TimeWindow window) {
		PubSubSession session = getPubSubSession(webSocketId);
		if (session != null) {
			Publisher publisher = session.getPublisher();
			if (publisher != null) {
				publisher.setWindow(window);
			}
			List<Subscriber> subscribers = session.getSubscribers();
			if (subscribers != null) {
				for (Subscriber subscriber: session.getSubscribers()) {
					subscriber.setWindow(window);
				}
			}
		} else {
			logger.warn("No session found for websocket: " + webSocketId + ", cannot set Epoch");
		}
	}
	
	public TimeWindow getPublisherEpoch(String webSocketId) {
		PubSubSession session = pubSubSessionMap.get(webSocketId);
		if (session != null) {
			Publisher publisher = session.getPublisher();
			if (publisher != null) {
				return publisher.getWindow();
			}
		}
		return null;
	}
	
	public PubSubSession getPubSubSession(String sessionId) {
		PubSubSession session = pubSubSessionMap.get(sessionId);
		int retry = 0;
		// so with the ConcurrentHashMap used by pubSubSessionMap a pub which adds to the map
		// followed immediately by a get on the Map may result in the get not seeing the add
		// the solution is locking the whole map, which we'd rather not do, or add this retry
		while (session == null && (retry < 5)) {
			try { Thread.sleep(100); } catch (InterruptedException e) {}
			session = pubSubSessionMap.get(sessionId);
			retry++;
		}
		return session;
	}
	
	// TODO remove when subs start sending there own sessionId
	public String getFirstSessionId() {
		if (pubSubSessionMap.size() > 0) {
			return pubSubSessionMap.entrySet().iterator().next().getKey();
		} else {
			return null;
		}
	}
	
	public void registerPubSubQueryCallable(PubSubQueryCallable query) {
		if (query != null) {
			logger.info("Registering PubSubQueryCallable: " + query.getClass().getSimpleName());
			pubSubQueryCallableList.add(query);
		}
	}
	
	public void runPubSubQuery(PubSubQueryParams data) {
		for (PubSubQueryCallable query: pubSubQueryCallableList) {
			query.setQueryParams(data);
			pubSubQueryRunner.run(query);
		}
	}
}
