package com.deleidos.analytics.pubsub.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.datetime.DateRange;
import com.deleidos.analytics.common.datetime.DateTimeUtil;
import com.deleidos.analytics.common.datetime.TimeWindow;
import com.deleidos.analytics.pubsub.PubSubManager;
import com.deleidos.analytics.pubsub.query.PubSubQueryParams;
import com.deleidos.analytics.websocket.api.BaseWebSocketMessage;

/**
 * Part of the pub-sub endpoint, this message is passed in by the Common Time Controller to manage how often
 * subscription data is sent to subscribers.
 */
public class TimeStamp extends BaseWebSocketMessage {

	private static final Logger logger = Logger.getLogger(TimeStamp.class);

	public String timeStamp;

	public TimeStamp() {}

	public TimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	@Path("/time_stamp")
	@GET
	public void processMessage() {
		String prevTime = PubSubManager.getInstance().getPublisherTimeStamp(webSocketId);
		Date prev = DateTimeUtil.parseDate(prevTime);
		Date now = DateTimeUtil.parseDate(timeStamp);
		// need to support moving forward or backward in time, need 2 ticks before we can
		// figure what direction, so prev must not be null
		if (prev != null && now != null) {
			Map<String, List<TimeWindow>> topicWindowMap = PubSubManager.getInstance().
					getUniqueTopicWindowCombinations(webSocketId);
			if (topicWindowMap != null) {
				String content = PubSubManager.getInstance().getPublisherContent(webSocketId);
				for (Map.Entry<String, List<TimeWindow>> entry : topicWindowMap.entrySet()) {
					String topic = entry.getKey();
					for (TimeWindow window: entry.getValue()) {
						if (DateTimeUtil.doesTimeCrossWindowBoundary(prev, now, window)) {
							logger.info(window.toString() + " boundary crossed, prev: "
									+ DateTimeUtil.formatDate(prev) + " now:" + DateTimeUtil.formatDate(now));
							// now calculating the date range based on the current time tick (now)
							// so the general rule is calculate the date range for the epoch we have just entered
							// into
							DateRange range = DateTimeUtil.calculateDateRange(now, window);
							PubSubQueryParams queryData = new PubSubQueryParams(topic,content,range.getStart(),
									range.getEnd(),window,webSocketId);
							PubSubManager.getInstance().runPubSubQuery(queryData);
						}
						else {
							logger.debug(window.toString() + " boundary NOT crossed, prev: "
									+ DateTimeUtil.formatDate(prev) + " now:" + DateTimeUtil.formatDate(now));
						}
					}
				}
			}
		}
		PubSubManager.getInstance().setPublisherTimeStamp(webSocketId, timeStamp);
	}
}
