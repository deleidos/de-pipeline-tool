package com.deleidos.analytics.elasticsearch.client;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.logging.log4j.ConsoleAppender;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.InternalNested;
import org.elasticsearch.search.aggregations.bucket.significant.SignificantTerms;
import org.elasticsearch.search.aggregations.bucket.significant.SignificantTerms.Bucket;
import org.elasticsearch.search.aggregations.bucket.significant.SignificantTermsBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.deleidos.analytics.elasticsearch.client.aggregation.Metric;
import com.deleidos.analytics.elasticsearch.client.aggregation.SignificantTermScore;
import com.deleidos.analytics.elasticsearch.client.aggregation.TermMetricValue;
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
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/**
 * Elasticsearch client.
 *
 * @author vernona
 */
public class ElasticsearchClient {

	private Logger logger = Logger.getLogger(ElasticsearchClient.class);

	private LRUMap mappingCache = new LRUMap();

	protected TransportClient client;
	protected ElasticsearchClientConfig config;

	/**
	 * Constructor.
	 * 
	 * @param config
	 */
	public ElasticsearchClient(ElasticsearchClientConfig config) {
		this.config = config;
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", config.getClusterName()).build();
		client = new TransportClient(settings);
		for (String hostname : config.getClusterHostnames()) {
			client.addTransportAddress(new InetSocketTransportAddress(hostname, config.getPort()));
		}
	}

	/**
	 * Set debug mode.
	 */
	public void setDebugMode() {
		logger.setLevel(Level.TRACE);
		logger.addAppender(new ConsoleAppender(new PatternLayout("[%p] %m%n"), "System.out"));
	}

	/**
	 * Index data in elasticsearch.
	 * 
	 * @param dataList
	 */
	public void indexData(List<ElasticsearchData> dataList) {
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		for (ElasticsearchData data : dataList) {
			bulkRequest.add(client.prepareIndex(data.getIndexName(), data.getTypeName(), data.getId())
					.setSource(data.getJsonContent()));
		}
		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			// Use this code to iterate over response items for debugging.
			// Iterator<BulkItemResponse> i = bulkResponse.iterator();

			throw new RuntimeException(bulkResponse.buildFailureMessage());
		}
	}

	/**
	 * Index a single record into elasticsearch.
	 * 
	 * @param data
	 */
	public void indexData(ElasticsearchData data) {
		client.index((new IndexRequest(data.getIndexName(), data.getTypeName(), data.getId()))
				.source(data.getJsonContent()));
	}

	/**
	 * Query results from elasticsearch using any combination of match queries, filters and a date range.
	 * 
	 * Match criteria, dange range criterion and filter criteria are all optional. If nothing is passed, all records
	 * will be matched (but the max result set size will still limit results).
	 * 
	 * @param index
	 * @param matchCriteria
	 * @param dateRangeCriterion
	 * @param filterCriteria
	 * @param nestedQueryPath
	 * @param fields
	 * @param size
	 *            the number of results to return
	 * @return
	 * @deprecated
	 */
	public List<ElasticsearchQueryResult> query(String index, Collection<MatchCriterion> matchCriteria,
			DateRangeCriterion dateRangeCriterion, FilterCriteria filterCriteria, SortCriterion sortCriterion,
			String nestedQueryPath, String[] fields, String[] partialFields, int size) {
		return getQueryResults(index, buildQuery(matchCriteria, dateRangeCriterion, filterCriteria, nestedQueryPath),
				sortCriterion, fields, partialFields, size);
	}

	/**
	 * [New version using QueryParams instead of parameter list.]
	 * 
	 * Query results from elasticsearch using any combination of match queries, filters and a date range.
	 * 
	 * Match criteria, dange range criterion and filter criteria are all optional. If nothing is passed, all records
	 * will be matched (but the max result set size will still limit results).
	 * 
	 * @param params
	 * @return
	 */
	public List<ElasticsearchQueryResult> query(QueryParams params) {
		return getQueryResults(params.getIndex(),
				buildQuery(params.getMatchCriteria(), params.getDateRangeCriterion(), params.getFilterCriteria(),
						params.getNestedQueryPath()),
				params.getSortCriterion(), params.getFields(), params.getPartialFields(), params.getSize());
	}

	/**
	 * Get a single field value count aggregation result. Counts occurrences of the aggregation field for all results
	 * matching the query/filters.
	 * 
	 * @param index
	 * @param matchCriteria
	 * @param dateRangeCriterion
	 * @param filterCriteria
	 * @param aggField
	 */
	public long getValueCount(String index, Collection<MatchCriterion> matchCriteria,
			DateRangeCriterion dateRangeCriterion, FilterCriteria filterCriteria, String aggField) {
		ValueCountBuilder aggregationBuilder = AggregationBuilders.count("count");
		aggregationBuilder.field(aggField);
		Aggregations aggregations = getAggregations(index, aggregationBuilder, matchCriteria, dateRangeCriterion,
				filterCriteria);

		long count = 0;
		for (Aggregation aggregation : aggregations) {
			count = ((ValueCount) aggregation).getValue();
		}
		return count;
	}

	/**
	 * Get a single field sum aggregation result. Sums the value of the field for all results matching the
	 * query/filters.
	 * 
	 * @param index
	 * @param matchCriteria
	 * @param dateRangeCriterion
	 * @param filterCriteria
	 * @param aggField
	 */
	public double getSum(String index, Collection<MatchCriterion> matchCriteria, DateRangeCriterion dateRangeCriterion,
			FilterCriteria filterCriteria, String aggField) {
		SumBuilder aggregationBuilder = AggregationBuilders.sum("sum");
		aggregationBuilder.field(aggField);
		Aggregations aggregations = getAggregations(index, aggregationBuilder, matchCriteria, dateRangeCriterion,
				filterCriteria);

		double sum = 0;
		for (Aggregation aggregation : aggregations) {
			sum = ((Sum) aggregation).getValue();
		}
		return sum;
	}

	/**
	 * Get significant terms scores for a query.
	 * 
	 * @param index
	 * @param matchCriteria
	 * @param aggField
	 * @param size
	 */
	public List<SignificantTermScore> getSignificantTermScores(String index, Collection<MatchCriterion> matchCriteria,
			DateRangeCriterion dateRangeCriterion, FilterCriteria filterCriteria, String aggField, int size) {
		SignificantTermsBuilder aggregationBuilder = AggregationBuilders.significantTerms("significanttermscores");
		aggregationBuilder.field(aggField);
		aggregationBuilder.size(size);

		SearchRequestBuilder searchBuilder = new SearchRequestBuilder(client);
		searchBuilder.setIndices(index);
		searchBuilder.setQuery(buildQuery(matchCriteria, dateRangeCriterion, filterCriteria, null));
		searchBuilder.addAggregation(aggregationBuilder);
		searchBuilder.setSize(size);

		searchBuilder.execute();
		SearchResponse response = searchBuilder.get();

		// Log the raw request string:
		logger.debug(searchBuilder.toString());

		List<SignificantTermScore> scores = new ArrayList<SignificantTermScore>();
		Aggregations aggregations = response.getAggregations();
		for (Aggregation aggregation : aggregations) {
			SignificantTerms terms = (SignificantTerms) aggregation;
			for (Bucket bucket : terms.getBuckets()) {
				scores.add(
						new SignificantTermScore(bucket.getKey(), bucket.getSignificanceScore(), bucket.getDocCount()));
			}
		}

		return scores;
	}

	/**
	 * Get a map of unique terms and their value counts.
	 * 
	 * @param index
	 * @param termField
	 * @param maxSize
	 * @return
	 */
	public Map<String, Long> getTermValueCounts(String index, String termField, String nestedQueryPath, int maxSize,
			DateRangeCriterion dateRangeCriterion) {
		Map<String, Long> valueCounts = new LinkedHashMap<String, Long>();

		TermsBuilder terms = AggregationBuilders.terms("termsagg").field(termField).size(maxSize);
		AbstractAggregationBuilder aggBuilder = terms;

		if (nestedQueryPath != null) {
			aggBuilder = AggregationBuilders.nested("nestedagg").path(nestedQueryPath).subAggregation(terms);
		}

		SearchRequestBuilder searchBuilder = new SearchRequestBuilder(client);
		searchBuilder.setIndices(index);
		searchBuilder.addAggregation(aggBuilder);

		if (dateRangeCriterion != null) {
			searchBuilder.setQuery(buildRangeQuery(dateRangeCriterion));
		}

		logger.debug(searchBuilder.toString());
		searchBuilder.execute();
		SearchResponse response = searchBuilder.get();

		Aggregations aggregations = response.getAggregations();
		for (Aggregation aggregation : aggregations) {
			if (aggregation instanceof InternalNested) {
				Aggregations nestedAggs = ((InternalNested) aggregation).getAggregations();
				for (Aggregation nestedAgg : nestedAggs) {
					StringTerms stringTerms = (StringTerms) nestedAgg;
					for (Terms.Bucket bucket : stringTerms.getBuckets()) {
						valueCounts.put(bucket.getKey(), bucket.getDocCount());
					}
				}
			}
			else {
				StringTerms stringTerms = (StringTerms) aggregation;
				for (Terms.Bucket bucket : stringTerms.getBuckets()) {
					valueCounts.put(bucket.getKey(), bucket.getDocCount());
				}
			}
		}

		return valueCounts;
	}

	/**
	 * Get ordered term metric values for a query.
	 * 
	 * Return type could be changed to a LinkedHashMap to preserve key ordering and also allow fast lookup by term if
	 * needed.
	 * 
	 * @param index
	 * @param termField
	 * @param metricField
	 * @param size
	 * @return a map of terms to metric values
	 */
	public List<TermMetricValue> getTermMetricValues(String index, String termField, String metricField, Metric metric,
			SortOrdering sortOrdering, int size) {
		boolean orderAsc = sortOrdering.equals(SortOrdering.asc) ? true : false;

		TermsBuilder terms = AggregationBuilders.terms("termsagg").field(termField);

		switch (metric) {
		case min:
			terms.subAggregation(AggregationBuilders.min("metricagg").field(metricField));
			break;
		case max:
			terms.subAggregation(AggregationBuilders.max("metricagg").field(metricField));
			break;
		case avg:
			terms.subAggregation(AggregationBuilders.avg("metricagg").field(metricField));
			break;
		}

		terms.order(Terms.Order.aggregation("metricagg", "value", orderAsc));
		terms.size(size);

		SearchRequestBuilder searchBuilder = new SearchRequestBuilder(client);
		searchBuilder.setIndices(index);
		searchBuilder.addAggregation(terms);

		logger.debug(searchBuilder.toString());
		searchBuilder.execute();
		SearchResponse response = searchBuilder.get();

		Aggregations aggregations = response.getAggregations();
		List<TermMetricValue> values = new ArrayList<TermMetricValue>();
		for (Aggregation aggregation : aggregations) {
			StringTerms stringTerms = (StringTerms) aggregation;
			for (Terms.Bucket bucket : stringTerms.getBuckets()) {
				values.add(new TermMetricValue(bucket.getKey(),
						((NumericMetricsAggregation.SingleValue) bucket.getAggregations().asList().get(0)).value()));
			}
		}

		return values;
	}

	/**
	 * Creates an ElasticSearch Index with the given Mappings and Settings
	 * 
	 * @param indexName
	 *            the name of the index to create
	 * @param mappings
	 *            a Map of <type name, name of file containing mapping for type (file must be on classpath)>
	 * @param settingsFile
	 *            name of a file containing the settings to be applied to the index (file must be on classpath)
	 */
	public synchronized void createIndex(String indexName, Map<String, String> mappings, String settingsFile) {
		try {
			if (mappingCache.get(indexName) == null) {

				IndicesExistsRequest indexExistsRequest = new IndicesExistsRequest(indexName);
				ActionFuture<IndicesExistsResponse> future = client.admin().indices().exists(indexExistsRequest);
				IndicesExistsResponse indexExistsResponse = future.actionGet();

				if (!indexExistsResponse.isExists()) {
					String settings = getFileAsString(settingsFile);

					logger.info(String.format("Creating index {%s} with settings: %s and mappings: %s", indexName,
							settings, mappings));

					CreateIndexRequestBuilder builder = client.admin().indices().prepareCreate(indexName);
					if (settings != null) {
						builder = builder.setSettings(settings);
					}
					if (mappings != null) {
						for (Map.Entry<String, String> entry : mappings.entrySet()) {
							String mappingKey = entry.getKey();
							String mappingFile = entry.getValue();
							String mappingJson = getFileAsString(mappingFile);
							builder = builder.addMapping(mappingKey, mappingJson);
						}
					}
					builder.execute().actionGet();
				}
			}
		}
		catch (ElasticsearchException e) {
			logger.error(e);
		}
		finally {
			mappingCache.put(indexName, "true");
		}
	}

	/**
	 * Cleanly shutdown the elasticsearch client.
	 */
	public void shutdown() {
		client.close();
	}

	//
	// Private methods:
	//

	private String getFileAsString(String fileName) {
		if (fileName == null || fileName.trim().isEmpty()) {
			return null;
		}
		else {
			try {
				URL url = Resources.getResource(fileName);
				return Resources.toString(url, Charsets.UTF_8);
			}
			catch (Exception e) {
				logger.error("Setting/Mapping file: " + fileName + " not found!", e);
				return null;
			}
		}
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
	 * Build a boolean query from a set of match and (optional) date range criteria. At least one criterion must be
	 * passed.
	 * 
	 * @param matchCriteria
	 * @param dateRangeCriterion
	 * @param filterCriterion
	 * @param geoCriterion
	 * @param nestedQueryPath
	 * @return
	 */
	private QueryBuilder buildQuery(Collection<MatchCriterion> matchCriteria, DateRangeCriterion dateRangeCriterion,
			FilterCriteria filterCriteria, String nestedQueryPath) {

		// QueryBuilders.geoShapeQuery(geoCriterion.getLocationFieldName(), ShapeBuilder.)
		// if (geoCriterion != null) {
		// QueryBuilder qb = geoBoundingBoxQuery(geoCriterion.getLocationFieldName())
		// .topLeft(40.73, -74.1)
		// .bottomRight(40.717, -73.99);
		// }

		QueryBuilder queryBuilder = null;
		QueryBuilder baseQuery = null;
		if ((matchCriteria != null && !matchCriteria.isEmpty()) || dateRangeCriterion != null) {
			baseQuery = new BoolQueryBuilder();
			BoolQueryBuilder boolQuery = (BoolQueryBuilder) baseQuery;
			boolQuery.minimumNumberShouldMatch(1);

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
								boolQuery.must(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()));
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
	 * Build filters.
	 * 
	 * @param filterFields
	 * @return
	 */
	private FilterBuilder[] buildFilters(Collection<Filter> criteria) {
		List<FilterBuilder> filters = new ArrayList<FilterBuilder>();
		for (Filter filter : criteria) {
			if (filter instanceof TermFilter) {
				for (Entry<String, String> entry : ((TermFilter) filter).getFieldValues().entrySet()) {
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
	private List<ElasticsearchQueryResult> getQueryResults(String index, QueryBuilder queryBuilder,
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

		// Don't make the size too big. Paginate if very large result set sizes
		// are needed. This is ok for now.
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

		SearchResponse response = searchBuilder.get();

		// Log the raw request string:
		logger.debug(searchBuilder.toString());

		SearchHits hits = response.getHits();
		List<ElasticsearchQueryResult> results = new ArrayList<ElasticsearchQueryResult>();
		for (SearchHit hit : hits) {
			Map<String, List<Object>> fieldValueMap = new HashMap<String, List<Object>>();
			ElasticsearchQueryResult result = new ElasticsearchQueryResult(hit.getId(), fieldValueMap);
			results.add(result);

			for (Entry<String, SearchHitField> entry : hit.getFields().entrySet()) {
				List<Object> values = entry.getValue().getValues();
				if (values != null && values.size() > 0) {
					String key = entry.getKey().toString();
					for (Object value : values) {
						if (fieldValueMap.containsKey(key)) {
							fieldValueMap.get(key).add(value);
						}
						else {
							List<Object> list = new ArrayList<Object>();
							list.add(value);
							fieldValueMap.put(key, list);
						}
					}
				}
			}
		}
		return results;
	}

	/**
	 * A reusable block of code to get aggregations.
	 * 
	 * @param index
	 * @param aggregationBuilder
	 * @param matchCriteria
	 * @param dateRangeCriterion
	 * @param filterCriteria
	 * @return
	 */
	private Aggregations getAggregations(String index, AbstractAggregationBuilder aggregationBuilder,
			Collection<MatchCriterion> matchCriteria, DateRangeCriterion dateRangeCriterion,
			FilterCriteria filterCriteria) {
		SearchRequestBuilder searchBuilder = new SearchRequestBuilder(client);
		searchBuilder.setIndices(index);
		searchBuilder.setQuery(buildQuery(matchCriteria, dateRangeCriterion, filterCriteria, null));
		searchBuilder.addAggregation(aggregationBuilder);

		searchBuilder.execute();
		SearchResponse response = searchBuilder.get();

		// Log the raw request string:
		logger.debug(searchBuilder.toString());

		return response.getAggregations();
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