package com.deleidos.analytics.common.util;

/**
 * Number utility methods.
 * 
 * @author vernona
 */
public class NumberUtil {

	/**
	 * Parse a number from a string, ensuring that non-decimals are Long and decimals are Double. Returns null if the
	 * string is not a parsable number.
	 * 
	 * @param s
	 * @return
	 */
	public static Number parseNumber(String s) {
		Number num = null;
		try {
			num = Long.parseLong(s);
		}
		catch (Exception e) {
			try {
				num = Double.parseDouble(s);
			}
			catch (Exception e2) {
			}
		}
		return num;
	}
}
