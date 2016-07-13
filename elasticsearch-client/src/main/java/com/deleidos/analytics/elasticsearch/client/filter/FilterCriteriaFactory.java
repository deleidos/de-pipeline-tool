package com.deleidos.analytics.elasticsearch.client.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter criteria factory containing convenience methods for building filters.
 * 
 * @author vernona
 */
public class FilterCriteriaFactory {
	private static FilterCriteriaFactory instance = new FilterCriteriaFactory();

	/**
	 * Private no-arg constructor enforces the singleton pattern.
	 * 
	 * @throws Exception
	 */
	private FilterCriteriaFactory() {}

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static FilterCriteriaFactory getInstance() {
		return instance;
	}
	
	/**
	 * Get term filters criteria.
	 * 
	 * @param filterType
	 * @param fieldName
	 * @param terms
	 * @return
	 */
	public FilterCriteria getTermFilters(FilterType filterType, String fieldName, List<String> terms) {
		FilterCriteria filterCriteria = null;
		if (terms != null && !terms.isEmpty()) {
			List<Filter> filters = new ArrayList<Filter>();
			for (String term : terms) {
				filters.add(new TermFilter(fieldName, term));
			}
			filterCriteria = new FilterCriteria(filterType, filters);
		}
		
		return filterCriteria;
	}
}
