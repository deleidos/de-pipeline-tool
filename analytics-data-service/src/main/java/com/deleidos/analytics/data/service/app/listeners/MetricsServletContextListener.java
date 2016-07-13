package com.deleidos.analytics.data.service.app.listeners;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;
import com.deleidos.analytics.common.util.MetricsUtil;

public class MetricsServletContextListener extends MetricsServlet.ContextListener {

    @Override
    protected MetricRegistry getMetricRegistry() {
    	return MetricsUtil.METRIC_REGISTRY;
    }

}