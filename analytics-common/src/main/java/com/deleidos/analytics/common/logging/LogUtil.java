package com.deleidos.analytics.common.logging;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.RootLogger;

/**
 * Simple reusable log utilities.
 * 
 * @author vernona
 */
public class LogUtil {

	/**
	 * Programmatically initialize a console appender to the root logger. This method may be called from unit tests and
	 * main functions as an alternative to placing duplicate log4j.properties files in various places. Don't use in code
	 * intended to run on a server.
	 */
	public static void initializeLog4jConsoleAppender() {
		ConsoleAppender console = new ConsoleAppender(new PatternLayout("%d [%p|%c|%C{1}] %m%n"));
		console.setThreshold(Level.TRACE);
		console.activateOptions();
		RootLogger.getRootLogger().addAppender(console);
	}
}
