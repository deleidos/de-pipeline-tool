package com.deleidos.analytics.pubsub.query;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.analytics.pubsub.api.PubSubResponseWrapper;

/**
 * Part of the pub-sub endpoint, this message is created and populated when a Timestamp message from the Common
 * Controller crosses a given subscriber's TimeWindow of interest. This QueryRunner is responsible for retrieving the
 * data for the given topic and time window before sending it out to the interested subscribers. All processing in the
 * run method of this class is performed from a background ThreadPool.
 */
public class TestPubSubQueryCallable extends BasePubSubQueryCallable {

	private static final Logger logger = Logger.getLogger(TestPubSubQueryCallable.class);

	public static final String UNIT_TEST_TOPIC = "unitTest";

	public TestPubSubQueryCallable() {}

	@Override
	public PubSubResult call() throws Exception {

		if (UNIT_TEST_TOPIC.equals(queryParams.getTopic())) {
			logger.info("Running TestPubSubQueryCallable for unitTest topic");
			Object result = "unitTest query result";
			String message = JsonUtil.toJsonString(new PubSubResponseWrapper(queryParams.getContent(),
					queryParams.getStart(), queryParams.getEnd(), result));
			queryResult.setMessage(message);

			return queryResult;
		}
		else {
			return null;
		}

	}
}