package com.deleidos.analytics.common.datetime;

import java.util.Date;

public class DateRange {
	public Date start;
	public Date end;

	public DateRange() {};

	public DateRange(Date start, Date end) {
		super();
		this.start = start;
		this.end = end;
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
}
