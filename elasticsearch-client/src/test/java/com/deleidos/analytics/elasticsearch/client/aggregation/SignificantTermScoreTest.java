package com.deleidos.analytics.elasticsearch.client.aggregation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/**
 * Test count aggregation result class and comparators/sorting.
 * 
 * @author vernona
 */
public class SignificantTermScoreTest {

	@Test
	public void testNormalizedCount() {
		SignificantTermScore score = new SignificantTermScore("test1", 1.2, 2000);
		assertEquals("test1", score.getTerm());
		assertEquals(1.2, score.getScore(), 0.001);
		assertEquals(2000, score.getDocCount());

		score.setTerm("test2");
		score.setScore(10.8);
		score.setDocCount(200);
		assertEquals("test2", score.getTerm());
		assertEquals(10.8, score.getScore(), 0.001);
		assertEquals(200, score.getDocCount());

	}

	@Test
	public void testComparators() {
		SignificantTermScore score1 = new SignificantTermScore("test1", 10.0, 2000000);
		SignificantTermScore score2 = new SignificantTermScore("test2", 100.0, 200000);
		SignificantTermScore score3 = new SignificantTermScore("test3", 1000.0, 20000);
		SignificantTermScore score4 = new SignificantTermScore("test4", 10000.0, 2000);
		SignificantTermScore score5 = new SignificantTermScore("test5", 100000.0, 200);

		List<SignificantTermScore> scores = new ArrayList<SignificantTermScore>();
		scores.add(score1);
		scores.add(score2);
		scores.add(score3);
		scores.add(score4);
		scores.add(score5);
		
		Collections.sort(scores, new SignificantTermScoreComparator());
		assertEquals(scores.get(0).getTerm(), score5.getTerm());
		assertEquals(scores.get(4).getTerm(), score1.getTerm());

		Collections.sort(scores, new SignificantTermDocCountComparator());
		assertEquals(scores.get(0).getTerm(), score1.getTerm());
		assertEquals(scores.get(4).getTerm(), score5.getTerm());

	}
}
