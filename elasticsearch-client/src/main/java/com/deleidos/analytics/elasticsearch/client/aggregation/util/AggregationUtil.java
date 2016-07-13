package com.deleidos.analytics.elasticsearch.client.aggregation.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.deleidos.analytics.common.util.Normalizer;
import com.deleidos.analytics.elasticsearch.client.aggregation.SignificantTermScore;
import com.deleidos.analytics.elasticsearch.client.aggregation.SignificantTermScoreComparator;

/**
 * Utilities for working worth aggregation data.
 * 
 * @author vernona
 */
public class AggregationUtil {

	/**
	 * Normalize the range of term score result values, sort them, then return a number less than or equal to the
	 * size parameter.
	 * 
	 * If normalizeOverFullRange is true, gets the normalization value range over the full result list, including the
	 * keywords, which will be bigger than the final result size. This way, we will not get the exact same min/max in
	 * every result set. We will only get the max value if there are words as popular as the keywords, and we will only
	 * get the smallest size if the result size is the same as the input size or if the "tail" has a flat curve.
	 * 
	 * @param scores
	 *            the term score results
	 * @param size
	 *            the size of the result list
	 * @param min
	 *            the min normalized value
	 * @param max
	 *            the max normalized value
	 * @param excludeKeywordSet
	 *            the keywords to exclude from the final results; should be lower case; optional
	 * @param normalizeOverFullRange
	 * @return
	 */
	public static List<NormalizedScore> normalizeSignificantTermScores(List<SignificantTermScore> scores, int size,
			int min, int max, Set<String> excludeKeywordSet, boolean normalizeOverFullRange) {

		// If not normalizing over the full range, sort and trim the list first.
		List<SignificantTermScore> localScores = new ArrayList<SignificantTermScore>(scores);
		if (!normalizeOverFullRange) {
			Collections.sort(localScores, new SignificantTermScoreComparator());
			if (localScores.size() > size) {
				localScores = localScores.subList(0, size); // start is inclusive, end is exclusive
			}
		}

		// Get normalization min/max values.
		List<NormalizedScore> normalizedScores = new ArrayList<NormalizedScore>();
		if (localScores != null && !localScores.isEmpty()) {
			double minScore = Integer.MAX_VALUE;
			double maxScore = 0;
			double minCount = Integer.MAX_VALUE;
			double maxCount = 0;
			for (SignificantTermScore aggCount : localScores) {
				double score = Math.max(aggCount.getScore(), 0);
				minScore = Math.min(score, minScore);
				maxScore = Math.max(score, maxScore);

				double count = Math.max(aggCount.getDocCount(), 0);
				minCount = Math.min(count, minCount);
				maxCount = Math.max(count, maxCount);
			}

			// Normalization will not change order, so no need to re-sort normalized list.
			Normalizer scoreNormalizer = new Normalizer(minScore, maxScore, min, max);
			Normalizer countNormalizer = new Normalizer(minCount, maxCount, min, max);
			for (SignificantTermScore termScore : localScores) {
				if (excludeKeywordSet == null || !excludeKeywordSet.contains(termScore.getTerm().toLowerCase())) {
					int normalScore = scoreNormalizer.normalize(termScore.getScore());
					int normalCount = countNormalizer.normalize(termScore.getDocCount());
					normalizedScores.add(new NormalizedScore(termScore.getTerm(), normalScore, normalCount));
				}
			}

			// If we normalized over the full range, sort and trim the normalized list.
			if (normalizeOverFullRange) {
				Collections.sort(normalizedScores, new NormalizedCountScoreComparator());
				if (normalizedScores.size() > size) {
					normalizedScores = normalizedScores.subList(0, size); // start is inclusive, end is exclusive
				}
			}
		}
		return normalizedScores;
	}
}
