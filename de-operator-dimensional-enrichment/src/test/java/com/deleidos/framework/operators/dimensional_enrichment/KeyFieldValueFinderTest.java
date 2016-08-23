package com.deleidos.framework.operators.dimensional_enrichment;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.deleidos.framework.operators.common.KeyFieldValueFinder;

public class KeyFieldValueFinderTest {

	private Map<String, Object> mapA;
	private KeyFieldValueFinder finder;
	private String value = "d";

	@Before
	public void setup() {
		mapA = new LinkedHashMap<String, Object>();
		Map<String, Object> mapB = new LinkedHashMap<String, Object>();
		Map<String, Object> mapC = new LinkedHashMap<String, Object>();
		mapC.put("c", value);
		mapB.put("b", mapC);
		mapA.put("a", mapB);
		finder = new KeyFieldValueFinder();
	}

	@Test
	public void testKeyFieldStringValueFinder() {
		String result = (String) finder.findValue("a.b.c", mapA);
		System.out.print(result);
		assertEquals(value, result);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testKeyFieldMapValueFinder() {
		Object o = finder.findValue("a.b", mapA);
		assertTrue(o instanceof Map);
		String result = (String) finder.findValue("c", (Map<String, Object>)o);
		assertEquals(value, result);
	}
}
