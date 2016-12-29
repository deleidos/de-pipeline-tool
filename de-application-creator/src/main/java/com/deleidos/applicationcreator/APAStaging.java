package com.deleidos.applicationcreator;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class APAStaging
{
	public static void createApaStage(String appBundleName) throws IOException {
		// create folders app,conf,lib,META-INF
		// copy to META-INF MANIFEST.MF from /opt/META-INF
		// move jars from /opt/lib to /tmp/apex-staging/new-app-bundle-name/lib
		new File("/opt/apex-staging/" + appBundleName + "/app").mkdirs();
		new File("/opt/apex-staging/" + appBundleName + "/conf").mkdir();

		new File("/opt/apex-staging/" + appBundleName + "/META-INF").mkdir();
		
		FileUtils.copyFileToDirectory(new File("/opt/apex-deployment/META-INF/MANIFEST.MF"),
				new File("/opt/apex-staging/" + appBundleName + "/META-INF"));
		FileUtils.copyDirectoryToDirectory(new File("/opt/apex-deployment/lib"),
				new File("/opt/apex-staging/" + appBundleName));
	}
	
	public static void deleteApaStage(String appBundleName)
	{
		File appFile = new File("/opt/apex-deployment/" + appBundleName + ".apa");
		appFile.delete();
	}
}