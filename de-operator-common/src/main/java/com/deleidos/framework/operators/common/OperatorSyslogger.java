package com.deleidos.framework.operators.common;

import com.deleidos.analytics.common.logging.Syslogger;

/**
 * Operator-specific syslogger that includes the system name in the log line in a consistent manner.
 * 
 * @author vernona
 */
public class OperatorSyslogger {

	private String systemName;
	private Syslogger syslogger;

	private static final long errorThrottleTimeout = 2000;
	private long lastErrorTime;

	/**
	 * Constructor.
	 * 
	 * @param systemName
	 */
	public OperatorSyslogger(String systemName, String hostname, int port) {
		this.systemName = systemName;
		syslogger = new Syslogger(hostname, port);
	}

	public void trace(String message) {
		syslogger.trace(buildMessage(message));
	}

	public void debug(String message) {
		syslogger.debug(buildMessage(message));
	}

	public void info(String message) {
		syslogger.info(buildMessage(message));
	}

	public void warn(String message) {
		syslogger.warn(buildMessage(message));
	}

	public void error(String message) {
		if (!throttleErrorLogging()) {
			syslogger.error(buildMessage(message));
		}
	}

	public void error(String message, Throwable t) {
		if (!throttleErrorLogging()) {
			syslogger.error(buildMessage(message), t);
		}
	}

	public void fatal(String message) {
		if (!throttleErrorLogging()) {
			syslogger.fatal(buildMessage(message));
		}
	}

	/**
	 * Prepend the system name to the log message.
	 * 
	 * @param message
	 * @return
	 */
	private String buildMessage(String message) {
		return systemName + " " + message;
	}

	/**
	 * Throttle error logging rate. Errors tend to happen on every record when something is wrong, so this prevents
	 * downstream systems from getting overwhelmed.
	 * 
	 * @return
	 */
	private boolean throttleErrorLogging() {
		boolean throttling = true;
		if (System.currentTimeMillis() - lastErrorTime > errorThrottleTimeout) {
			throttling = false;
		}
		lastErrorTime = System.currentTimeMillis();
		return throttling;
	}
}
