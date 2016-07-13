package com.deleidos.analytics.stream;

public interface StreamFilter {

	/**
	 * Return the topic this filter should be applied to 
	 * @return the topic name
	 */
	public String getTopic();
	
	/**
	 * Filter the message.  Return the original message if it passes your filter criteria.
	 * Return null to filter out (remove) the message from the outgoing stream.
	 * @param message the message to be filtered
	 * @return the original message if it passes the filter test, null otherwise
	 */
	public String filterMessage(String message);
}
