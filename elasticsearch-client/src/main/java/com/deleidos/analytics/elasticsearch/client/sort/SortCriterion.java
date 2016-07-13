package com.deleidos.analytics.elasticsearch.client.sort;

/**
 * Sort criterion class.
 * 
 * @author vernona
 */
public class SortCriterion {

	protected String fieldName;
	protected SortOrdering sortOrdering;

	/**
	 * Constructor.
	 * 
	 * @param fieldName
	 * @param sortOrdering
	 */
	public SortCriterion(String fieldName, SortOrdering sortOrdering) {
		this.fieldName = fieldName;
		this.sortOrdering = sortOrdering;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public SortOrdering getSortOrdering() {
		return sortOrdering;
	}

	public void setSortOrdering(SortOrdering sortOrdering) {
		this.sortOrdering = sortOrdering;
	}
}
