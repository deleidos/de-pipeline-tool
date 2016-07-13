package com.deleidos.analytics.common.geo;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test geo destination calculation.
 *  
 * @author vernona
 */
public class GeoDestinationTest {
	
	@Test
	public void testCalculateDestination1() {
		double lat1 = 0;
		double lon1 = 0;
		int heading = 0;
		int speedKnots = 1000;
		int seconds = 600;
		
		runTest(lat1, lon1, heading, speedKnots, seconds);
	}
	
	@Test
	public void testCalculateDestination2() {
		double lat1 = 50;
		double lon1 = 50;
		int heading = 0;
		int speedKnots = 1000;
		int seconds = 600;
		
		runTest(lat1, lon1, heading, speedKnots, seconds);
	}
	
	@Test
	public void testCalculateDestination3() {
		double lat1 = -50;
		double lon1 = -50;
		int heading = 0;
		int speedKnots = 1000;
		int seconds = 600;
		
		runTest(lat1, lon1, heading, speedKnots, seconds);
	}
	
	@Test
	public void testCalculateDestination4() {
		double lat1 = -50;
		double lon1 = 50;
		int heading = 0;
		int speedKnots = 1000;
		int seconds = 600;
		
		runTest(lat1, lon1, heading, speedKnots, seconds);
		
	}
	
	private void runTest(double lat1, double lon1, int heading, int speedKnots, int seconds) {
		GeoPoint point = GeoUtil.calculateDestination(lat1, lon1, heading, speedKnots, seconds);
		System.out.println("testCalculateDestination: " + point);
		
		double milesTraveled = GeoUtil.getMilesTraveled(speedKnots, seconds);
		double milesBetweenPoints = GeoUtil.getDistanceMilesBetweenPoints(lat1, lon1, point.getLatitude(), point.getLongitude());
		double diff = Math.abs(milesTraveled - milesBetweenPoints);
		System.out.println(milesTraveled);
		System.out.println(milesBetweenPoints);
		assertTrue(diff < .00001);
	}
}
