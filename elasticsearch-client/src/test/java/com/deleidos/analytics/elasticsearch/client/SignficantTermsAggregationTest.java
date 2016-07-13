package com.deleidos.analytics.elasticsearch.client;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.deleidos.analytics.elasticsearch.client.query.DateRangeCriterion;
import com.deleidos.analytics.elasticsearch.client.query.MatchCriterion;
import com.deleidos.analytics.elasticsearch.client.query.MatchType;
import com.deleidos.analytics.elasticsearch.client.query.QueryType;

/**
 * Test significant terms aggregation.
 * 
 * @author vernona
 */
public class SignficantTermsAggregationTest extends ElasticsearchClientTestBase {

	@Test
	public void testGetSignificantTermScores() {
		String contentField = "LineItemComment";
		String keyword = "carefully";
		Map<String, String> fieldValues = new HashMap<String, String>();
		fieldValues.put(contentField, keyword);

		Collection<MatchCriterion> matchCriteria = Collections
				.singleton(new MatchCriterion(QueryType.must, MatchType.match, fieldValues));
		DateRangeCriterion dateRangeCriterion = null;
		String aggField = contentField;
		int size = 100;

		client.getSignificantTermScores("salesjson", matchCriteria, dateRangeCriterion, null, aggField, size);
	}
}
