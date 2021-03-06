package com.deleidos.analytics.common.geo;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Geo point object.
 * 
 * @author vernona
 */
public class GeoPoint {

	private double latitude;
	private double longitude;

	/**
	 * Constructor.
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public GeoPoint(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
