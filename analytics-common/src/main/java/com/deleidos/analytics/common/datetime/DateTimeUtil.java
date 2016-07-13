package com.deleidos.analytics.common.datetime;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.log4j.Logger;

public class DateTimeUtil {

	public final static String UTC_TIME_ZONE = "UTC";

	private final static Logger logger = Logger.getLogger(DateTimeUtil.class);
	private final static FastDateFormat sdf = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSSX",
			TimeZone.getTimeZone(UTC_TIME_ZONE));

	public static Date parseDate(String isoDateTimeString) {
		try {
			if (isoDateTimeString != null) {
				return sdf.parse(isoDateTimeString);
			}
		}
		catch (ParseException e) {
			logger.error("TimeStamp: " + isoDateTimeString + " failed to parse with error: " + e.toString());
		}
		return null;
	}

	public static String formatDate(Date date) {
		if (date != null) {
			return sdf.format(date);
		}
		return null;
	}

	public static boolean doesTimeCrossWindowBoundary(Date start, Date end, TimeWindow window) {
		boolean cross = false;
		switch (window) {
		case MINUTE:
			cross = !DateTimeUtil.isSameMinute(start, end);
			break;
		case HOUR:
			cross = !DateTimeUtil.isSameHour(start, end);
			break;
		case DAY:
			cross = !DateTimeUtil.isSameDay(start, end);
			break;
		case MONTH:
			cross = !DateTimeUtil.isSameMonth(start, end);
			break;
		case YEAR:
			cross = !DateTimeUtil.isSameYear(start, end);
			break;
		default:
			break;
		}
		return cross;
	}

	public static DateRange calculateDateRange(Date now, TimeWindow window) {
		Calendar calStart = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIME_ZONE));
		calStart.setTime(now);
		Calendar calEnd = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIME_ZONE));
		calEnd.setTime(now);

		switch (window) {
		case MINUTE:
			calStart.set(Calendar.MILLISECOND, 0);
			calStart.set(Calendar.SECOND, 0);
			calEnd.set(Calendar.MILLISECOND, 999);
			calEnd.set(Calendar.SECOND, 59);
			break;
		case HOUR:
			calStart.set(Calendar.MILLISECOND, 0);
			calStart.set(Calendar.SECOND, 0);
			calStart.set(Calendar.MINUTE, 0);
			calEnd.set(Calendar.MILLISECOND, 999);
			calEnd.set(Calendar.SECOND, 59);
			calEnd.set(Calendar.MINUTE, 59);
			break;
		case DAY:
			calStart.set(Calendar.MILLISECOND, 0);
			calStart.set(Calendar.SECOND, 0);
			calStart.set(Calendar.MINUTE, 0);
			calStart.set(Calendar.HOUR_OF_DAY, 0);
			calEnd.set(Calendar.MILLISECOND, 999);
			calEnd.set(Calendar.SECOND, 59);
			calEnd.set(Calendar.MINUTE, 59);
			calEnd.set(Calendar.HOUR_OF_DAY, 23);
			break;
		case MONTH:
			calStart.set(Calendar.MILLISECOND, 0);
			calStart.set(Calendar.SECOND, 0);
			calStart.set(Calendar.MINUTE, 0);
			calStart.set(Calendar.HOUR_OF_DAY, 0);
			calStart.set(Calendar.DAY_OF_MONTH, 1);
			calEnd.set(Calendar.MILLISECOND, 999);
			calEnd.set(Calendar.SECOND, 59);
			calEnd.set(Calendar.MINUTE, 59);
			calEnd.set(Calendar.HOUR_OF_DAY, 23);
			calEnd.set(Calendar.DAY_OF_MONTH, calEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
			break;
		case YEAR:
			calStart.set(Calendar.MILLISECOND, 0);
			calStart.set(Calendar.SECOND, 0);
			calStart.set(Calendar.MINUTE, 0);
			calStart.set(Calendar.HOUR_OF_DAY, 0);
			calStart.set(Calendar.DAY_OF_MONTH, 1);
			calStart.set(Calendar.MONTH, 0);
			calEnd.set(Calendar.MILLISECOND, 999);
			calEnd.set(Calendar.SECOND, 59);
			calEnd.set(Calendar.MINUTE, 59);
			calEnd.set(Calendar.HOUR_OF_DAY, 23);
			calEnd.set(Calendar.DAY_OF_MONTH, calEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
			calEnd.set(Calendar.MONTH, 11);
			break;
		default:
			break;
		}

		return new DateRange(calStart.getTime(), calEnd.getTime());
	}

	public static boolean isSameMinute(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIME_ZONE));
		Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIME_ZONE));
		cal1.setTime(date1);
		cal2.setTime(date2);
		return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
				&& cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY)
				&& cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE));
	}

	public static boolean isSameHour(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIME_ZONE));
		Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIME_ZONE));
		cal1.setTime(date1);
		cal2.setTime(date2);
		return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
				&& cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY));
	}

	public static boolean isSameDay(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIME_ZONE));
		Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIME_ZONE));
		cal1.setTime(date1);
		cal2.setTime(date2);
		return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
	}

	public static boolean isSameMonth(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIME_ZONE));
		Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIME_ZONE));
		cal1.setTime(date1);
		cal2.setTime(date2);
		return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH));
	}

	public static boolean isSameYear(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIME_ZONE));
		Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIME_ZONE));
		cal1.setTime(date1);
		cal2.setTime(date2);
		return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR));
	}

	/**
	 * Partition a date range into a number of partitions.
	 * 
	 * Only handles HOUR and DAY. Defaults to DAY.
	 * 
	 * @param start
	 * @param end
	 * @param timeWindow
	 * @param partitions
	 * @return
	 */
	public static List<DateRange> partitionDateRange(Date start, Date end, TimeWindow timeWindow, int partitions) {
		long partionSize = 0;
		switch (timeWindow) {
		case HOUR:
			partionSize = (1000 * 60 * 60) / partitions;
			break;
		case DAY:
		default:
			partionSize = (1000 * 60 * 60 * 24) / partitions;
			break;
		}

		List<DateRange> dateRanges = new ArrayList<DateRange>();
		for (int i = 0; i < partitions; i++) {
			long t1 = start.getTime() + partionSize * i;
			long t2 = start.getTime() + partionSize * (i + 1);
			dateRanges.add(new DateRange(new Date(t1), new Date(t2)));
		}
		return dateRanges;
	}
}
