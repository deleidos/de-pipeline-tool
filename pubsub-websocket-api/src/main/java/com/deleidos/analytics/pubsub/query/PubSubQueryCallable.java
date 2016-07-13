package com.deleidos.analytics.pubsub.query;

import java.util.concurrent.Callable;

/**
 * The Interface to implement by API plugins to process PubSub style queries. PubSubQueryParams are set before the
 * WebSocketServer invokes the call() method. Implementors should run a query, and return a PubSubResult.
 */
public interface PubSubQueryCallable extends Callable<PubSubResult> {

	public void setQueryParams(PubSubQueryParams params);

	public PubSubResult call() throws Exception;
}
