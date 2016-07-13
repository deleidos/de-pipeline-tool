package com.deleidos.analytics.common.util;

import java.util.Base64;

/**
 * Base64 encode/decode utility. Requires Java 8.
 * 
 * @author vernona
 */
public class Base64Util {

	public static String decodeToString(String src) {
		return new String(Base64.getDecoder().decode(src));
	}

	public static byte[] decode(String src) {
		return Base64.getDecoder().decode(src);
	}

	public static String encodeToString(byte[] src) {
		return new String(encode(src));

	}

	public static byte[] encode(byte[] src) {
		return Base64.getEncoder().encode(src);
	}
}
