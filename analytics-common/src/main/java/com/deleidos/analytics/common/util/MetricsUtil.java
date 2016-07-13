package com.deleidos.analytics.common.util;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;

/**
 * Utility class for dropwizard style metrics.  For now this is really just a holder of the various
 * Metrics registries.  See https://dropwizard.github.io/metrics/3.1.0/getting-started/
 */
public class MetricsUtil {

	public static final HealthCheckRegistry HEALTH_CHECK_REGISTRY = new HealthCheckRegistry();
	public static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();
	
}
