package com.deleidos.analytics.elasticsearch.client;

import java.util.Collection;

import com.deleidos.analytics.elasticsearch.client.filter.FilterCriteria;
import com.deleidos.analytics.elasticsearch.client.query.DateRangeCriterion;
import com.deleidos.analytics.elasticsearch.client.query.MatchCriterion;
import com.deleidos.analytics.elasticsearch.client.sort.SortCriterion;

/**
 * Elasticsearch query parameters.
 * 
 * @author vernona
 */
public class QueryParams {

	private String index;
	private Collection<MatchCriterion> matchCriteria;
	private DateRangeCriterion dateRangeCriterion;
	private FilterCriteria filterCriteria;
	private SortCriterion sortCriterion;
	private String nestedQueryPath;
	private String[] fields;
	private String[] partialFields;
	private int size;

	/**
	 * Constructor.
	 * 
	 * @param index
	 * @param size
	 */
	public QueryParams(String index, int size) {
		this(index, size, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param index
	 * @param size
	 * @param fields
	 */
	public QueryParams(String index, int size, String[] fields) {
		this.index = index;
		this.size = size;
		this.fields = fields;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public Collection<MatchCriterion> getMatchCriteria() {
		return matchCriteria;
	}

	public void setMatchCriteria(Collection<MatchCriterion> matchCriteria) {
		this.matchCriteria = matchCriteria;
	}

	public DateRangeCriterion getDateRangeCriterion() {
		return dateRangeCriterion;
	}

	public void setDateRangeCriterion(DateRangeCriterion dateRangeCriterion) {
		this.dateRangeCriterion = dateRangeCriterion;
	}

	public FilterCriteria getFilterCriteria() {
		return filterCriteria;
	}

	public void setFilterCriteria(FilterCriteria filterCriteria) {
		this.filterCriteria = filterCriteria;
	}

	public SortCriterion getSortCriterion() {
		return sortCriterion;
	}

	public void setSortCriterion(SortCriterion sortCriterion) {
		this.sortCriterion = sortCriterion;
	}

	public String getNestedQueryPath() {
		return nestedQueryPath;
	}

	public void setNestedQueryPath(String nestedQueryPath) {
		this.nestedQueryPath = nestedQueryPath;
	}

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public String[] getPartialFields() {
		return partialFields;
	}

	public void setPartialFields(String[] partialFields) {
		this.partialFields = partialFields;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
