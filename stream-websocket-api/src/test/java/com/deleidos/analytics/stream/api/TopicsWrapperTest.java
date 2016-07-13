package com.deleidos.analytics.stream.api;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.deleidos.analytics.common.util.JsonUtil;

/**
 * Test topics wrapper json serialization.
 * 
 * @author vernona
 */
@Ignore
public class TopicsWrapperTest {

	@Test
	public void testTopicsWrapperJson() throws Exception {
		Set<String> topics = new HashSet<String>();
		topics.add("twitter");
		topics.add("rss");
		TopicsWrapper wrapper = new TopicsWrapper(topics);
		String json = JsonUtil.toJsonString(wrapper);
		assertEquals("{\"topics\":[\"twitter\",\"rss\"]}", json);
	}
}
