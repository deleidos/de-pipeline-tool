package com.deleidos.analytics.common.datetime;

import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * JodaTime utility methods. Returns JodaTime objects instead of java.util objects.
 * 
 * @author vernona
 */
public class JodaTimeUtil {

	/**
	 * Get an interval that spans a full day.
	 * 
	 * @param day
	 *            Any time within the day.
	 * @return
	 */
	public static Interval getDayInterval(long timestamp) {
		DateTime begin = (new DateTime(timestamp)).withTimeAtStartOfDay();
		DateTime end = begin.plusDays(1).withTimeAtStartOfDay();
		return new Interval(begin, end);
	}
}
