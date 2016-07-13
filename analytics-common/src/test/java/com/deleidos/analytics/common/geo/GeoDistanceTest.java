package com.deleidos.analytics.common.geo;

import org.junit.Test;

import com.deleidos.analytics.common.geo.GeoUtil;

public class GeoDistanceTest {

	@Test
	public void testGetDistanceMiles() {
		System.out.println("testGetDistanceMiles: " + GeoUtil.getDistanceMilesBetweenPoints(45, -65, 25, -80));
	}
	
	@Test
	public void testGetDistanceMilesMinutes() {
		double distanceMiles = GeoUtil.getDistanceMilesBetweenPoints(45, -65, 25, -80);
		double distanceMinutes = Math.abs(1442190833 - 1442212140) / 60d;
		System.out.println("testGetDistanceMilesMinutes:" + Math.sqrt(Math.pow(distanceMiles, 2) + Math.pow(distanceMinutes, 2)));
	}
}
