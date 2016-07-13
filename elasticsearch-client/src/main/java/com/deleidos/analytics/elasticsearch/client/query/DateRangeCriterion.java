package com.deleidos.analytics.elasticsearch.client.query;

import java.util.Date;

/**
 * Date range criterion object.
 * 
 * @author vernona
 */
public class DateRangeCriterion {

	private String fieldName;
	private Date startDate;
	private Date endDate;

	/**
	 * Constructor.
	 * 
	 * @param fieldName
	 * @param startDate
	 * @param endDate
	 */
	public DateRangeCriterion(String fieldName, Date startDate, Date endDate) {
		this.fieldName = fieldName;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * Build a date range criterion. Returns null if both dates are null.
	 * 
	 * @param field
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static DateRangeCriterion buildDateRangeCriterion(String field, Date startDate, Date endDate) {
		DateRangeCriterion dateRangeCriterion = null;
		if (startDate != null || endDate != null) {
			dateRangeCriterion = new DateRangeCriterion(field, startDate, endDate);
		}
		return dateRangeCriterion;
	}
}
