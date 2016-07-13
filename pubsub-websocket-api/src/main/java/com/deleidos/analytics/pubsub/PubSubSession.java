package com.deleidos.analytics.pubsub;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PubSubSession {

	private Publisher publisher;
	private List<Subscriber> subscribers;
	
	public PubSubSession(String webSocketId) {
		publisher = new Publisher(webSocketId);
		subscribers = new CopyOnWriteArrayList<Subscriber>();
	}
	
	public Publisher getPublisher() {
		return publisher;
	}
	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}
	public List<Subscriber> getSubscribers() {
		return subscribers;
	}
	public void setSubscribers(List<Subscriber> subscribers) {
		this.subscribers = subscribers;
	}
	
}
