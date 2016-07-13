package com.deleidos.analytics.common.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.deleidos.analytics.common.util.StopwordHelper;

/**
 * Stopword helper unit test.
 * 
 * @author vernona
 */
public class StopwordHelperTest {

	@Test
	public void testStopwords() {
		assertFalse(StopwordHelper.isStopword("test"));
		assertFalse(StopwordHelper.isStopword("aadfasdfasdf"));
		
		assertTrue(StopwordHelper.isStopword("a"));
		assertTrue(StopwordHelper.isStopword("there's"));
		assertTrue(StopwordHelper.isStopword("rt"));
		assertTrue(StopwordHelper.isStopword("we've"));
	}
}
