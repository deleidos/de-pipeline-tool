package com.deleidos.analytics.elasticsearch.client;

import java.util.Collections;

import com.deleidos.analytics.elasticsearch.client.filter.FilterCriteria;
import com.deleidos.analytics.elasticsearch.client.query.DateRangeCriterion;
import com.deleidos.analytics.elasticsearch.client.query.MatchCriterion;
import com.deleidos.analytics.elasticsearch.client.sort.SortCriterion;

/**
 * Query params factory class.
 * 
 * @author vernona
 */
public class QueryParamsFactory {

	private static QueryParamsFactory instance = new QueryParamsFactory();

	/**
	 * Private no-arg constructor enforces the singleton pattern.
	 * 
	 * @throws Exception
	 */
	private QueryParamsFactory() {}

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static QueryParamsFactory getInstance() {
		return instance;
	}

	public QueryParams getQueryParams(String index, int size, String[] fields) {
		return new QueryParams(index, size, fields);
	}

	public QueryParams getQueryParams(String index, int size, String[] fields, FilterCriteria filters) {
		QueryParams params = getQueryParams(index, size, fields);
		params.setFilterCriteria(filters);
		return params;
	}

	public QueryParams getQueryParams(String index, int size, String[] fields, DateRangeCriterion dateRange,
			FilterCriteria filters) {
		QueryParams params = getQueryParams(index, size, fields);
		params.setDateRangeCriterion(dateRange);
		params.setFilterCriteria(filters);
		return params;
	}

	public QueryParams getQueryParams(String index, int size, String[] fields, DateRangeCriterion dateRange,
			SortCriterion sort, FilterCriteria filters) {
		QueryParams params = getQueryParams(index, size, fields);
		params.setDateRangeCriterion(dateRange);
		params.setFilterCriteria(filters);
		return params;
	}

	public QueryParams getQueryParams(String index, int size, String[] fields, MatchCriterion match,
			FilterCriteria filters) {
		QueryParams params = getQueryParams(index, size, fields);
		if (match != null) {
			params.setMatchCriteria(Collections.singleton(match));
		}
		params.setFilterCriteria(filters);
		return params;
	}

	public QueryParams getQueryParams(String index, int size, String[] fields, MatchCriterion match, SortCriterion sort,
			FilterCriteria filters) {
		QueryParams params = getQueryParams(index, size, fields);
		if (match != null) {
			params.setMatchCriteria(Collections.singleton(match));
		}
		params.setSortCriterion(sort);
		params.setFilterCriteria(filters);
		return params;
	}

	public QueryParams getQueryParams(String index, int size, String[] fields, SortCriterion sort,
			FilterCriteria filters) {
		QueryParams params = getQueryParams(index, size, fields);
		params.setSortCriterion(sort);
		params.setFilterCriteria(filters);
		return params;
	}
}
