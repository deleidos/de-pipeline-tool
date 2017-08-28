package com.deleidos.analytics.elasticsearch.client.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.deleidos.analytics.common.util.StringUtil;
import com.deleidos.analytics.elasticsearch.client.QueryParams;
import com.deleidos.analytics.elasticsearch.client.config.ElasticsearchClientConfig;
import com.deleidos.analytics.elasticsearch.client.filter.ExistsFilter;
import com.deleidos.analytics.elasticsearch.client.filter.Filter;
import com.deleidos.analytics.elasticsearch.client.filter.FilterCriteria;
import com.deleidos.analytics.elasticsearch.client.filter.GeoFilter;
import com.deleidos.analytics.elasticsearch.client.filter.MissingFilter;
import com.deleidos.analytics.elasticsearch.client.filter.TermFilter;
import com.deleidos.analytics.elasticsearch.client.query.DateRangeCriterion;
import com.deleidos.analytics.elasticsearch.client.query.MatchCriterion;
import com.deleidos.analytics.elasticsearch.client.sort.ScriptSortCriterion;
import com.deleidos.analytics.elasticsearch.client.sort.SortCriterion;
import com.deleidos.analytics.elasticsearch.client.sort.SortOrdering;

/**
 * Build Elasticsearch JSON requests that can be posted in REST calls.
 * 
 * @author vernona
 */
public class ElasticsearchRequestBuilder {

	protected TransportClient client;
	protected ElasticsearchClientConfig config;

	private static final String _all = "_all";

	/**
	 * Constructor.
	 * 
	 * @param config
	 */
	public ElasticsearchRequestBuilder(ElasticsearchClientConfig config) {
		this.config = config;
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", config.getClusterName()).build();
		client = new TransportClient(settings);
		for (String hostname : config.getClusterHostnames()) {
			client.addTransportAddress(new InetSocketTransportAddress(hostname, config.getPort()));
		}
	}

	/**
	 * Build a JSON search request with default values.
	 * 
	 * @param params
	 * @return
	 */
	public String getSearchRequest(QueryParams params) {
		return getSearchRequest(params, null, null, null);
	}

	/**
	 * Build a JSON search request.
	 * 
	 * @param params
	 * @param sortCriterion
	 * @param fields
	 * @param partialFields
	 * @return
	 */
	public String getSearchRequest(QueryParams params, SortCriterion sortCriterion, String[] fields,
			String[] partialFields) {
		QueryBuilder queryBuilder = getQueryBuilder(params.getMatchCriteria(), params.getDateRangeCriterion(),
				params.getFilterCriteria(), params.getNestedQueryPath());
		SearchRequestBuilder searchRequestBuilder = getSearchRequestBuilder(params.getIndex(), queryBuilder,
				params.getSortCriterion(), params.getFields(), params.getPartialFields(), params.getSize());
		return searchRequestBuilder.toString();
	}

	/**
	 * Build a query from various search criteria.
	 * 
	 * @param matchCriteria
	 * @param dateRangeCriterion
	 * @param filterCriteria
	 * @param nestedQueryPath
	 * @return
	 */
	private QueryBuilder getQueryBuilder(Collection<MatchCriterion> matchCriteria,
			DateRangeCriterion dateRangeCriterion, FilterCriteria filterCriteria, String nestedQueryPath) {

		QueryBuilder queryBuilder = null;
		QueryBuilder baseQuery = null;
		if ((matchCriteria != null && !matchCriteria.isEmpty()) || dateRangeCriterion != null) {
			baseQuery = new BoolQueryBuilder();
			BoolQueryBuilder boolQuery = (BoolQueryBuilder) baseQuery;
			// 3<75% : If 3 or less clauses are specified, all are required; otherwise, 75% are required.
			// TODO This parameter should probably be exposed in the API for the client to specify.
			boolQuery.minimumShouldMatch("3<75%");

			if (dateRangeCriterion != null) {
				boolQuery.must(buildRangeQuery(dateRangeCriterion));
			}

			if (matchCriteria != null && !matchCriteria.isEmpty()) {
				for (MatchCriterion criterion : matchCriteria) {
					switch (criterion.getQueryType()) {
					case must:
						switch (criterion.getMatchType()) {
						case match:
							for (Entry<String, String> entry : criterion.getFieldValues().entrySet()) {
								if (entry.getKey().equals(_all)) {
									String[] values = StringUtil.splitWhiteSpaceDelimitedString(entry.getValue());
									for (String value : values) {
										boolQuery.must(QueryBuilders.matchQuery(_all, value));
									}
								}
								else {
									boolQuery.must(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()));
								}
							}
							break;
						case matchPhrase:
							for (Entry<String, String> entry : criterion.getFieldValues().entrySet()) {
								boolQuery.must(QueryBuilders.matchPhraseQuery(entry.getKey(), entry.getValue()));
							}
							break;
						}
						break;
					case should:
						switch (criterion.getMatchType()) {
						case match:
							for (Entry<String, String> entry : criterion.getFieldValues().entrySet()) {
								boolQuery.should(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()));
							}
							break;
						case matchPhrase:
							for (Entry<String, String> entry : criterion.getFieldValues().entrySet()) {
								boolQuery.should(QueryBuilders.matchPhraseQuery(entry.getKey(), entry.getValue()));
							}
							break;
						}
						break;
					}
				}
			}
		}
		else {
			baseQuery = QueryBuilders.matchAllQuery();
		}

		if (filterCriteria != null) {
			FilterBuilder filterBuilder = null;
			switch (filterCriteria.getFilterType()) {
			case and:
				filterBuilder = FilterBuilders.andFilter(buildFilters(filterCriteria.getFilters()));
				break;
			case or:
				filterBuilder = FilterBuilders.orFilter(buildFilters(filterCriteria.getFilters()));
				break;
			}

			queryBuilder = QueryBuilders.filteredQuery(baseQuery, filterBuilder);
		}
		else {
			queryBuilder = baseQuery;
		}

		if (nestedQueryPath != null) {
			queryBuilder = QueryBuilders.nestedQuery(nestedQueryPath, queryBuilder);
		}

		return queryBuilder;
	}

	/**
	 * Build a range query for a date range criterion.
	 * 
	 * @param dateRangeCriterion
	 * @return
	 */
	private RangeQueryBuilder buildRangeQuery(DateRangeCriterion dateRangeCriterion) {
		RangeQueryBuilder rangeQuery = null;
		if (dateRangeCriterion != null) {
			rangeQuery = new RangeQueryBuilder(dateRangeCriterion.getFieldName());
			rangeQuery.includeLower(true);
			rangeQuery.includeUpper(true);

			if (dateRangeCriterion.getStartDate() != null) {
				rangeQuery.from(dateRangeCriterion.getStartDate().getTime());
			}

			if (dateRangeCriterion.getEndDate() != null) {
				rangeQuery.to(dateRangeCriterion.getEndDate().getTime());
			}
		}
		return rangeQuery;
	}

	/**
	 * Build filters.
	 * 
	 * @param filterFields
	 * @return
	 */
	private FilterBuilder[] buildFilters(Collection<Filter> criteria) {
		List<FilterBuilder> filters = new ArrayList<FilterBuilder>();
		for (Filter filter : criteria) {
			if (filter instanceof TermFilter) {
				for (Entry<String, Object> entry : ((TermFilter) filter).getFieldValues().entrySet()) {
					filters.add(FilterBuilders.termFilter(entry.getKey(), entry.getValue()));
				}
			}
			else if (filter instanceof MissingFilter) {
				for (String fieldName : ((MissingFilter) filter).getFieldNames()) {
					filters.add(FilterBuilders.missingFilter(fieldName));
				}
			}
			else if (filter instanceof ExistsFilter) {
				for (String fieldName : ((ExistsFilter) filter).getFieldNames()) {
					filters.add(FilterBuilders.existsFilter(fieldName));
				}
			}
			else if (filter instanceof GeoFilter) {
				GeoFilter geoFilter = (GeoFilter) filter;
				filters.add(FilterBuilders.geoBoundingBoxFilter(geoFilter.getLocationFieldName())
						.bottomLeft(geoFilter.getSeLat(), geoFilter.getNwLon())
						.bottomRight(geoFilter.getSeLat(), geoFilter.getSeLon())
						.topLeft(geoFilter.getNwLat(), geoFilter.getNwLon())
						.topRight(geoFilter.getNwLat(), geoFilter.getSeLon()));
			}
		}
		return filters.toArray(new FilterBuilder[filters.size()]);
	}

	/**
	 * Get query results for the given QueryBuilder.
	 * 
	 * @param index
	 * @param queryBuilder
	 * @param fields
	 * @param size
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private SearchRequestBuilder getSearchRequestBuilder(String index, QueryBuilder queryBuilder,
			SortCriterion sortCriterion, String[] fields, String[] partialFields, int size) {
		SearchRequestBuilder searchBuilder = new SearchRequestBuilder(client);
		searchBuilder.setIndices(index);
		searchBuilder.setQuery(queryBuilder);

		// In the java client, passing no fields results in no values being returned.
		if (fields != null) {
			searchBuilder.addFields(fields);
		}

		if (partialFields != null) {
			searchBuilder.addPartialField("partial1", partialFields, null);
		}

		// Don't make the size too big. Paginate if very large result set sizes are needed. This is ok for now.
		searchBuilder.setSize(size);

		if (sortCriterion != null) {
			if (sortCriterion instanceof ScriptSortCriterion) {
				ScriptSortCriterion scriptSort = (ScriptSortCriterion) sortCriterion;
				String script = "doc[field].value";
				searchBuilder.addSort(SortBuilders.scriptSort(script, scriptSort.getSortFieldType().toString())
						.lang("expressions").order(getEsSortOrder(scriptSort.getSortOrdering()))
						.param("field", scriptSort.getFieldName()));

			}
			else {
				SortBuilder sortBuilder = SortBuilders.fieldSort(sortCriterion.getFieldName()).unmappedType("integer")
						.order(getEsSortOrder(sortCriterion.getSortOrdering()));
				searchBuilder.addSort(sortBuilder);
				// searchBuilder.addSort(sortCriterion.getFieldName(), getEsSortOrder(sortCriterion.getSortOrdering()));
			}
		}

		return searchBuilder;
	}

	/**
	 * Get elasticsearch sort order from our internal sort ordering.
	 * 
	 * @param sortType
	 * @return
	 */
	private SortOrder getEsSortOrder(SortOrdering sortOrdering) {
		return sortOrdering == SortOrdering.asc ? SortOrder.ASC : SortOrder.DESC;
	}
}
