package com.deleidos.analytics.websocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Ignore;
import org.junit.Test;

import com.deleidos.analytics.common.datetime.DateTimeUtil;

@Ignore
public class DateTimeUtilTest {

	@Test
	public void testParseDate() throws ParseException {
		String isoDateTime = "2015-09-09T19:02:26.977Z";
		Date date = DateTimeUtil.parseDate(isoDateTime);
		assertNotNull(date);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSX");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date expectedDate = sdf.parse(isoDateTime);
		
		assertEquals(expectedDate.getTime(), date.getTime());
	}
	
	@Test
	public void testParseInvalidDates() throws ParseException {
		// no time zone indicator
		String isoDateTime = "2015-09-09T19:02:26.977";
		Date date = DateTimeUtil.parseDate(isoDateTime);
		assertNull(date);
		
		// no millis
		isoDateTime = "2015-09-09T19:02:26";
		date = DateTimeUtil.parseDate(isoDateTime);
		assertNull(date);
		
		// mm-dd-yyyy
		isoDateTime = "01-09-2015T19:02:26.977";
		date = DateTimeUtil.parseDate(isoDateTime);
		assertNull(date);
	}
}
