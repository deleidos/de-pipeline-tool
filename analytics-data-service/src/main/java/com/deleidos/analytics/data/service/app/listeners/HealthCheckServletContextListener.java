package com.deleidos.analytics.data.service.app.listeners;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.deleidos.analytics.common.util.MetricsUtil;

public class HealthCheckServletContextListener extends HealthCheckServlet.ContextListener {

    @Override
    protected HealthCheckRegistry getHealthCheckRegistry() {
        return MetricsUtil.HEALTH_CHECK_REGISTRY;
    }

}
