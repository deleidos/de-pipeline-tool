package com.deleidos.analytics.elasticsearch.client.filter;

import java.util.Arrays;
import java.util.Collection;

/**
 * Filter criteria for ANDing or ORing filters together.
 * 
 * @author vernona
 */
public class FilterCriteria {

	private FilterType filterType;
	private Collection<Filter> filters;

	/**
	 * Constructor.
	 * 
	 * @param filterType
	 * @param filters
	 */
	public FilterCriteria(FilterType filterType, Filter... filters) {
		this.filterType = filterType;
		this.filters = Arrays.asList(filters);
	}

	/**
	 * Constructor
	 * 
	 * @param filterType
	 * @param filters
	 */
	public FilterCriteria(FilterType filterType, Collection<Filter> filters) {
		this.filterType = filterType;
		this.filters = filters;
	}

	/**
	 * Constructor.
	 * 
	 * @param filters
	 */
	public FilterCriteria(Filter... filters) {
		this.filterType = FilterType.and;
		this.filters = Arrays.asList(filters);
	}

	public FilterType getFilterType() {
		return filterType;
	}

	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}

	public Collection<Filter> getFilters() {
		return filters;
	}

	public void setFilters(Collection<Filter> filters) {
		this.filters = filters;
	}
}
