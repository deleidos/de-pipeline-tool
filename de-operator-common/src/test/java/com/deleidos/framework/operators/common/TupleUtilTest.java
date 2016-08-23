package com.deleidos.framework.operators.common;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.deleidos.analytics.common.util.FileUtil;

/**
 * Test converting a JSON file to a tuple map.
 * 
 * @author vernona
 */
public class TupleUtilTest {

	/**
	 * Unit test with the resource file test1.json. Changing that file will break this test.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test1() throws Exception {
		String json = FileUtil.getResourceFileContentsAsString("test1.json");
		Map<String, Object> map = TupleUtil.jsonToTupleMap(json);
		Long a = (Long) map.get("a");
		assertEquals(new Long(1), a);
		Double b = (Double) map.get("b");
		assertEquals(new Double(1.5), b);
		String c = (String) map.get("c");
		assertEquals("cstring", c);
		
		assertEquals(new Double(1.5), b);
		Map<String, Object> d = (Map<String, Object>) map.get("d");
		Long e = (Long) d.get("e");
		assertEquals(new Long(2), e);
		String f = (String) d.get("f");
		assertEquals("fstring", f);
		Double g = (Double) d.get("g");
		assertEquals(new Double(2.000001), g);

		List<Object> h = (List<Object>) map.get("h");
		assertEquals("harraystring1", (String) h.get(0));
		assertEquals("harraystring2", (String) h.get(1));
		assertEquals("harraystring3", (String) h.get(2));

		List<Object> i = (List<Object>) map.get("i");
		Map<String, Object> i0 = (Map<String, Object>) i.get(0);
		Long i01 = (Long) i0.get("i1");
		assertEquals(new Long(100), i01);
		Double i02 = (Double) i0.get("i2");
		assertEquals(new Double(100.9), i02);
		String i03 = (String) i0.get("i3");
		assertEquals("i3arrayobjectstring1", i03);

		List<Object> i04 = (List<Object>) i0.get("i4");
		assertEquals("i41-1", (String)i04.get(0));
		assertEquals("i42-1", (String)i04.get(1));
		assertEquals("i43-1", (String)i04.get(2));
		
		Map<String, Object> j = (Map<String, Object>) map.get("j");
		List<Object> j2 = (List<Object>) j.get("j2");
		assertEquals(new Long(1), (Long)j2.get(0));
		assertEquals(new Long(2), (Long)j2.get(1));
		assertEquals(new Long(3), (Long)j2.get(2));
		
		Map<String, Object> k = (Map<String, Object>) map.get("k");
		List<Object> k1 = (List<Object>) k.get("k1");
		Map<String, Object> k10 = (Map<String, Object>) k1.get(0);
		List<Object> k1array = (List<Object>) k10.get("k1array");
		Map<String, Object> k1array0 = (Map<String, Object>) k1array.get(0);
		List<Object> k1array1_0 = (List<Object>)k1array0.get("k1array1");
		assertEquals("1", (String)k1array1_0.get(0));
		assertEquals("2", (String)k1array1_0.get(1));
		assertEquals("3", (String)k1array1_0.get(2));
	}
}
