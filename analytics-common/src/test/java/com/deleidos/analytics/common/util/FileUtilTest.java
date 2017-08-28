package com.deleidos.analytics.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * File util unit test.
 * 
 * @author vernona
 */
public class FileUtilTest {

	@Test
	public void testGetResourceFileAsString() throws Exception {
		String content = FileUtil.getResourceFileContentsAsString("test.txt");
		assertEquals("test.txt", content);
	}
}
