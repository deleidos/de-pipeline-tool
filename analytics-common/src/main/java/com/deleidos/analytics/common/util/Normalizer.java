package com.deleidos.analytics.common.util;

/**
 * Utility class for normalizing numbers over an integer range.
 * 
 * @author vernona
 */
public class Normalizer {

	private double xmin, xmax, min, max;

	/**
	 * Constructor. Simplifies parameter passing if using the same normalization parameters across multiple calls.
	 * 
	 * @param xmin
	 *            the min value to be normalized
	 * @param xmax
	 *            the max value to be normalized
	 * @param min
	 *            the normalized min value
	 * @param max
	 *            the normalized max value
	 */
	public Normalizer(double xmin, double xmax, double min, double max) {
		this.xmin = xmin;
		this.xmax = xmax;
		this.min = min;
		this.max = max;
	}

	/**
	 * Normalize a value.
	 * 
	 * @param x
	 * @return
	 */
	public int normalize(double x) {
		return Normalizer.normalize(x, xmin, xmax, min, max);
	}

	/**
	 * Statically normalize a value using the given parameters.
	 * 
	 * @param x
	 *            the current value to be normalized
	 * @param xmin
	 *            the min value to be normalized
	 * @param xmax
	 *            the max value to be normalized
	 * @param min
	 *            the normalized min value
	 * @param max
	 *            the normalized max value
	 * @return
	 */
	public static int normalize(double x, double xmin, double xmax, double min, double max) {
		double d = min + ((x - xmin) * (max - min)) / (xmax - xmin);
		return (int) d;
	}
}
