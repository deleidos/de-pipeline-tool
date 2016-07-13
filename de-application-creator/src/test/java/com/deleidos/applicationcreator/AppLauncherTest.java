package com.deleidos.applicationcreator;

import org.junit.Test;

public class AppLauncherTest {

	@Test
	public void testAppLauncher() {
		AppLauncher.getInstance().launchApp(new AppLaunchConfig(null, "testAppBundle"));
	}
}
