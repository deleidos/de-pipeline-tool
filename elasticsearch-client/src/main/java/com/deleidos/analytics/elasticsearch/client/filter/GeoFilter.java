package com.deleidos.analytics.elasticsearch.client.filter;

/**
 * Geo query criterion.
 * 
 * @author vernona
 */
public class GeoFilter implements Filter {

	private String locationFieldName;
	private double nwLat, nwLon, seLat, seLon;

	/**
	 * Bounding box geo query criterion constructor.
	 * 
	 * @param nwLat
	 * @param nwLon
	 * @param seLat
	 * @param seLon
	 */
	public GeoFilter(String locationFieldName, double nwLat, double nwLon, double seLat, double seLon) {
		this.locationFieldName = locationFieldName;
		this.nwLat = nwLat;
		this.nwLon = nwLon;
		this.seLat = seLat;
		this.seLon = seLon;
	}

	public String getLocationFieldName() {
		return locationFieldName;
	}

	public void setLocationFieldName(String locationFieldName) {
		this.locationFieldName = locationFieldName;
	}

	public double getNwLat() {
		return nwLat;
	}

	public void setNwLat(double nwLat) {
		this.nwLat = nwLat;
	}

	public double getNwLon() {
		return nwLon;
	}

	public void setNwLon(double nwLon) {
		this.nwLon = nwLon;
	}

	public double getSeLat() {
		return seLat;
	}

	public void setSeLat(double seLat) {
		this.seLat = seLat;
	}

	public double getSeLon() {
		return seLon;
	}

	public void setSeLon(double seLon) {
		this.seLon = seLon;
	}

}
