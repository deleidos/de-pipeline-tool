package com.deleidos.analytics.pubsub.query;

/**
 * Abstract implementation of the PubSubQueryCallable interface that populates all values in the PubSubResult return
 * object except for the actually result of the query. Implementors should implemnt the call() method, run a query,
 * populate PubSubResult with the query result and return PubSubResult.
 */
public abstract class BasePubSubQueryCallable implements PubSubQueryCallable {

	protected PubSubQueryParams queryParams;
	protected PubSubResult queryResult;

	public void setQueryParams(PubSubQueryParams params) {
		this.queryParams = params;
		queryResult = new PubSubResult(params.getSessionId(), params.getTopic(), params.getWindow());
	}

}