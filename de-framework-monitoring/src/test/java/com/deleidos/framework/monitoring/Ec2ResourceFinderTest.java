package com.deleidos.framework.monitoring;

import static org.junit.Assert.*;

import org.junit.Test;

public class Ec2ResourceFinderTest {

	@Test
	public void misc() throws Exception {
		assertEquals("Name", "tag:Name".split(":")[1]);
	}

}
