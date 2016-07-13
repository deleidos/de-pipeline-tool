package com.deleidos.analytics.elasticsearch.client;

import java.util.List;

import org.junit.Test;

import com.deleidos.analytics.elasticsearch.client.aggregation.Metric;
import com.deleidos.analytics.elasticsearch.client.aggregation.TermMetricValue;
import com.deleidos.analytics.elasticsearch.client.sort.SortOrdering;

/**
 * Test getting term aggregation metrics.
 * 
 * @author vernona
 */
public class TermMetricValuesTest extends ElasticsearchClientTestBase {

	@Test
	public void testGetTermMetricValues() {
		List<TermMetricValue> values = client.getTermMetricValues("de-index*", "content.user.screen_name",
				"content.user.followers_count", Metric.max, SortOrdering.desc, 200);
		System.out.println(values);
	}
}
