package com.deleidos.analytics.pubsub.api;

import java.util.Date;

/**
 * Wrap a pub-sub response in an object that can be serialized to JSON for easy frontent (js) consumption.
 * 
 * @author vernona
 */
public class PubSubResponseWrapper {

	private String content;
	private Date start;
	private Date end;
	private Object result;

	/** Constructor.
	 * 
	 * @param content
	 * @param start
	 * @param end
	 * @param result
	 */
	public PubSubResponseWrapper(String content, Date start, Date end, Object result) {
		this.content = content;
		this.start = start;
		this.end = end;
		this.result = result;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
