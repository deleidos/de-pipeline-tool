package com.deleidos.analytics.stream.api;

import java.util.Collection;

/**
 * Wrap a list of topics in an object that can be serialized to JSON for easy frontent (js) consumption.
 * 
 * @author vernona
 */
public class TopicsWrapper {

	private Collection<String> topics;
	
	public TopicsWrapper() {
	}

	public TopicsWrapper(Collection<String> topics) {
		this.topics = topics;
	}

	public Collection<String> getTopics() {
		return topics;
	}

	public void setTopics(Collection<String> topics) {
		this.topics = topics;
	}
}
