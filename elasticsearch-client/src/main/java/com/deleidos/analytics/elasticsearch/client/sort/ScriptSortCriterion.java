package com.deleidos.analytics.elasticsearch.client.sort;

/**
 * Script sort criterion.
 * 
 * @author vernona
 */
public class ScriptSortCriterion extends SortCriterion {

	private SortFieldType sortFieldType;

	/**
	 * Constructor.
	 * 
	 * @param fieldName
	 * @param sortOrdering
	 * @param sortFieldType
	 */
	public ScriptSortCriterion(String fieldName, SortOrdering sortOrdering, SortFieldType sortFieldType) {
		super(fieldName, sortOrdering);
		this.sortFieldType = sortFieldType;
	}

	public SortFieldType getSortFieldType() {
		return sortFieldType;
	}

	public void setSortFieldType(SortFieldType sortFieldType) {
		this.sortFieldType = sortFieldType;
	}
}
