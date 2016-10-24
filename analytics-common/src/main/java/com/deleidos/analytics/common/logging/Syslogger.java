package com.deleidos.analytics.common.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.net.SyslogAppender;

/**
 * Singleton syslog logger. Encapsulates underlying implementation. Logs at all levels.
 * 
 * Self-contained; will not log to any other appenders/loggers.
 * 
 * The rsyslog service needs to be configured to enable remote logging. Instructions for ubuntu:
 * 
 * <pre>
 * Enable remote logging:
 * sudo vi /etc/rsyslog.conf
 * 
 * Uncomment these lines:
 * # provides UDP syslog reception
 * $ModLoad imudp
 * $UDPServerRun 514
 *
 * # provides TCP syslog reception
 * $ModLoad imtcp
 * $InputTCPServerRun 514
 *
 * Resart the rsyslog service:
 *
 * sudo service rsyslog restart
 * </pre>
 * 
 * @author vernona
 */
public class Syslogger {

	private static final String patternLayout = "%d [%p] %m%n";

	private SyslogAppender appender;
	private Logger logger;

	/**
	 * Constructor for localhost 514.
	 */
	public Syslogger() {
		this("localhost", 514);
	}

	/**
	 * Constructor.
	 * 
	 * @param hostname
	 * @param port
	 */
	public Syslogger(String hostname, int port) {
		appender = new SyslogAppender(new PatternLayout(patternLayout), hostname + ":" + port,
				SyslogAppender.LOG_SYSLOG);
		appender.setName("syslog");

		logger = Logger.getLogger(Syslogger.class);
		logger.setLevel(Level.TRACE);
		logger.addAppender(appender);
		logger.setAdditivity(false); // Don't log to ancestor logs.
	}

	public void trace(String message) {
		logger.trace(message);
	}

	public void debug(String message) {
		logger.debug(message);
	}

	public void info(String message) {
		logger.info(message);
	}

	public void warn(String message) {
		logger.warn(message);
	}

	public void error(String message) {
		logger.error(message);
	}
	public void error(String message, Throwable t){
		logger.error(message, t);
	}

	public void fatal(String message) {
		logger.fatal(message);
	}
}
