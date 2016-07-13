package com.deleidos.analytics.common.geo;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * Geo utilities.
 * 
 * Useful site for geo formulas: http://www.movable-type.co.uk/scripts/latlong.html
 * 
 * @author vernona
 */
public class GeoUtil {

	private static final double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
	private static final double mphPerKnot = 1.151;
	private static final double secondsPerHour = 3600;

	/**
	 * Get distance in miles between two lat long points.
	 *
	 * @param lat1
	 *            in degrees
	 * @param lon1
	 *            in degrees
	 * @param lat2
	 *            in degrees
	 * @param lon2
	 *            in degrees
	 * @return
	 */
	public static double getDistanceMilesBetweenPoints(double lat1, double lon1, double lat2, double lon2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lon2 - lon1);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2)
				+ Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		return dist;
	}

	/**
	 * Convert the lat/lon values to a single long value using high order bytes for lat and low order bytes for lon.
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public static long latLonToLong(double latitude, double longitude) {
		// Multiply before converting to int to preserve the lat/lon decimal place values.
		int latInt = (int) (latitude * 100000);
		int lonInt = (int) (longitude * 100000);
		byte[] latBytes = Ints.toByteArray(latInt);
		byte[] lonBytes = Ints.toByteArray(lonInt);
		byte[] longBytes = ArrayUtils.addAll(latBytes, lonBytes);
		return Longs.fromByteArray(longBytes);
	}

	/**
	 * Get distance traveled in miles from speed in knots and duration in seconds.
	 * 
	 * @param groundspeed
	 *            in knots
	 * @param seconds
	 *            duration of travel time in seconds at the given speed
	 * @return
	 */
	public static double getMilesTraveled(int groundspeed, int seconds) {
		return groundspeed * mphPerKnot * (seconds / secondsPerHour);
	}

	/**
	 * Calculate a destination point given a starting point, a heading in degrees, speed in knots and duration in
	 * seconds.
	 * 
	 * @param lat
	 *            in degrees
	 * @param lon
	 *            in degrees
	 * @param heading
	 *            degrees clockwise from North
	 * @param speedKnots
	 *            speed in knots
	 * @param seconds
	 *            duration of travel time in seconds at the given speed
	 */
	public static GeoPoint calculateDestination(double lat, double lon, int heading, int speedKnots, int seconds) {
		// Convert to radians.
		double latR = Math.toRadians(lat);
		double lonR = Math.toRadians(lon);
		double headingR = Math.toRadians(heading);
		// Calculate angular distance in radians.
		double angularDistance = getMilesTraveled(speedKnots, seconds) / earthRadius;
		// Calculate destination latitude in radians.
		double lat2 = Math.asin((Math.sin(latR) * Math.cos(angularDistance))
				+ (Math.cos(latR) * Math.sin(angularDistance) * Math.cos(headingR)));
		// Calculate destination longitude in radians.
		double lon2 = lonR + Math.atan2(Math.sin(headingR) * Math.sin(angularDistance) * Math.cos(latR),
				Math.cos(angularDistance) - Math.sin(latR) * Math.sin(lat2));
		// Convert results back into degrees.
		return new GeoPoint(Math.toDegrees(lat2), Math.toDegrees(lon2));
	}

}
