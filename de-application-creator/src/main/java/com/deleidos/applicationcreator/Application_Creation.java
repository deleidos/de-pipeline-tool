package com.deleidos.applicationcreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import com.deleidos.analytics.common.ssh.SshClient;
import com.deleidos.analytics.config.AnalyticsConfig;
import com.deleidos.framework.model.system.OperatorDescriptor;
import com.deleidos.framework.model.system.SystemDescriptor;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sshtools.j2ssh.session.SessionChannelClient;
import com.sshtools.j2ssh.session.SessionOutputReader;

import org.apache.log4j.Logger;

public class Application_Creation {

	private SystemDescriptor sys;
	private String appBundleName;
	private Map<String, String> mappingFile;
	
	public Application_Creation(SystemDescriptor sys, String appBundleName) {
		this.sys = sys;
		this.appBundleName = appBundleName;
		this.mappingFile = sys.getMappings();
	}

	public void getFiles(File dir, List<File> fileList) {

		File[] files = dir.listFiles();
		for (File file : files) {
			fileList.add(file);
			if (file.isDirectory()) {
				getFiles(file, fileList);
			}
		}
	}

	public void delete(File dir) {
		if (dir.isDirectory()) {
			if (dir.list().length == 0) {
				dir.delete();
			}
			else {
				String files[] = dir.list();
				for (String file : files) {
					File fDel = new File(dir, file);
					delete(fDel);
				}
				if (dir.list().length == 0) {
					dir.delete();
				}
			}
		}
		else {
			dir.delete();
		}
	}

	public void addZip(File dir, File file, ZipOutputStream zos) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		String zipPath = file.getCanonicalPath().substring(dir.getCanonicalPath().length() + 1,
				file.getCanonicalPath().length());
		ZipEntry zipE = new ZipEntry(zipPath);
		zos.putNextEntry(zipE);

		byte[] bytes = new byte[1024];
		int len;
		while ((len = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, len);
		}
		zos.closeEntry();
		fis.close();
	}

	public void writeFile(File directory, List<File> fileList) throws IOException {
		FileOutputStream fos = new FileOutputStream("/opt/apex-deployment/" + directory.getName() + ".apa");
		ZipOutputStream zos = new ZipOutputStream(fos);

		for (File file : fileList) {
			if (!file.isDirectory()) {
				addZip(directory, file, zos);
			}
		}

		zos.close();
		fos.close();
	}

	public File getFile(String path) throws IOException {
		URL url = Application_Creation.class.getClassLoader().getResource(path);
		if (url == null) {
			throw new FileNotFoundException("couldn't find " + path);
		}
		URI uri = null;
		try {
			uri = url.toURI();
		}
		catch (java.net.URISyntaxException ex) {
			IOException ioe = new IOException();
			ioe.initCause(ex);
			throw ioe;
		}
		return new File(uri);

	}

	public static void updateJarFile(File srcJarFile, String targetPackage, File filesToAdd) throws IOException {
		File tmpJarFile = File.createTempFile("tempJar", ".tmp");
		JarFile jarFile = new JarFile(srcJarFile);
		boolean jarUpdated = false;

		try {
			JarOutputStream tempJarOutputStream = new JarOutputStream(new FileOutputStream(tmpJarFile));

			try {
				// Added the new files to the jar.

				File file = filesToAdd;
				FileInputStream fis = new FileInputStream(file);
				try {
					byte[] buffer = new byte[1024];
					int bytesRead = 0;
					JarEntry entry = new JarEntry(targetPackage + File.separator + file.getName());
					tempJarOutputStream.putNextEntry(entry);
					while ((bytesRead = fis.read(buffer)) != -1) {
						tempJarOutputStream.write(buffer, 0, bytesRead);
					}

				}
				finally {
					fis.close();
				}

				// Copy original jar file to the temporary one.
				Enumeration<JarEntry> jarEntries = jarFile.entries();
				while (jarEntries.hasMoreElements()) {
					JarEntry entry = jarEntries.nextElement();
					InputStream entryInputStream = jarFile.getInputStream(entry);
					tempJarOutputStream.putNextEntry(entry);
					byte[] buffer = new byte[1024];
					int bytesRead = 0;
					while ((bytesRead = entryInputStream.read(buffer)) != -1) {
						tempJarOutputStream.write(buffer, 0, bytesRead);
					}
				}

				jarUpdated = true;
			}
			catch (Exception ex) {
				ex.printStackTrace();
				tempJarOutputStream.putNextEntry(new JarEntry("stub"));
			}
			finally {
				tempJarOutputStream.close();
			}
		}
		finally {
			jarFile.close();

			if (!jarUpdated) {
				tmpJarFile.delete();
			}
		}

		if (jarUpdated) {
			srcJarFile.delete();
			tmpJarFile.renameTo(srcJarFile);
		}
	}

	public void createApaStage(String appBundleName) throws IOException {
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

	public void launchApp() throws IOException, InterruptedException {
		AppLauncher.getInstance().launchApp(new AppLaunchConfig(sys, appBundleName));
	}

	public static void stopApp(String appID) throws IOException, InterruptedException {
		com.sshtools.j2ssh.SshClient ssh = authenticateSsh();
		SessionChannelClient session = ssh.openSessionChannel();
		SessionOutputReader sor = new SessionOutputReader(session);
		session.requestPseudoTerminal("vt100", 80, 25, 0, 0, "");
		if (session.startShell()) {
			OutputStream out = session.getOutputStream();
			out.write("sudo su\n".getBytes());
			out.write("docker exec -it hadoop-client bash\n".getBytes());
			out.write(". /etc/profile.d/apex_env.sh\ndtcli \n".getBytes());

			Thread.sleep(1000 * 20);
			out.write(("shutdown-app " + appID + "\n").getBytes());
			Thread.sleep(1000 * 40);
			out.write("exit\n".getBytes());
			Thread.sleep(1000*2);
			out.write("exit\n".getBytes());
			Thread.sleep(1000*2);
			out.close();
		}
		session.close();
	}

	public static String killApp(String appID) throws IOException, InterruptedException {
		com.sshtools.j2ssh.SshClient ssh = authenticateSsh();
		SessionChannelClient session = ssh.openSessionChannel();
		SessionOutputReader sor = new SessionOutputReader(session);
		session.requestPseudoTerminal("vt100", 80, 25, 0, 0, "");
		String ret = null;
		if (session.startShell()) {
			OutputStream out = session.getOutputStream();
			out.write("sudo su\n".getBytes());
			out.write("docker exec -it hadoop-client bash\n".getBytes());
			out.write(". /etc/profile.d/apex_env.sh\ndtcli \n".getBytes());

			Thread.sleep(1000 * 20);
			out.write(("kill-app " + appID + "\n").getBytes());
			Thread.sleep(1000 * 40);
			Thread.sleep(1000 * 40);
			out.write("exit\n".getBytes());
			Thread.sleep(1000*2);
			out.write("exit\n".getBytes());
			Thread.sleep(1000*2);
			out.close();
		}
		session.close();
		return ret;
	}

	public String run() throws Exception {
		ClassMappings classMap = new ClassMappings();
		Logger logger = Logger.getLogger(Application_Creation.class);
		createApaStage(this.appBundleName);// create app structure (folders)
		List<OperatorDescriptor> operators = this.sys.getApplication().getOperators();
		String className = "";
		String jarName = "";
		File destDir = new File("/opt/apex-staging/" + appBundleName + "/lib");

		String jsonString = sys.getApplication().getApexAppJson(); // projectconfig.json
		JsonElement jelement = new JsonParser().parse(jsonString);
		JsonObject jObject = jelement.getAsJsonObject();

		for (OperatorDescriptor op : operators) {
			// copy the jars of the operators used into the lib folder
			className = op.getClassName();
			logger.info("className: " + className);
			jarName = classMap.getMappedVal(className);
			File jar = new File("/opt/apex-deployment/operators/" + jarName);

			FileUtils.copyFileToDirectory(jar, destDir, false);

			if (op.getClassName().contains("JSONMappingOperator")) {
				// If json operator then copy mapping file into jar
				File map = new File("/opt/apex-deployment/mappings/" + op.getName() + ".json");
				FileWriter mapWrite = new FileWriter(map);
				mapWrite.write(mappingFile.get(op.getName()));
				mapWrite.close();

				String jarFile = "/opt/apex-staging/" + appBundleName + "/lib/de-operator-mapping-0.0.1-SNAPSHOT.jar";
				String mappingFile = op.getName() + ".json";
				Runtime.getRuntime().exec("jar uf " + jarFile + " -C /opt/apex-deployment/mappings " + mappingFile);
				// Add mapping path of file

				JsonArray jArray = jObject.get("operators").getAsJsonArray();
				for (int i = 0; i < jArray.size(); i++) {
					JsonElement temp = jArray.get(i);
					JsonObject tempObj = temp.getAsJsonObject();
					if (tempObj.get("class").toString().contains("JSONMappingOperator")) {
						JsonObject tempProps = tempObj.get("properties").getAsJsonObject();
						tempProps.addProperty("modelPath", "/" + op.getName() + ".json");
					}
				}
			}
		}

		String jsonOut = jObject.toString();
		FileWriter jsonFile = new FileWriter("/opt/apex-staging/" + appBundleName + "/app/" + appBundleName + ".json");
		jsonFile.write(jsonOut);
		jsonFile.close();

		// json file copy to app folder
		// zip directory and now have an apa
		File appDirectory = new File("/opt/apex-staging/" + appBundleName);
		List<File> fileList = new ArrayList<File>();

		getFiles(appDirectory, fileList);
		writeFile(appDirectory, fileList);

		delete(appDirectory);
		// Delete staging area

		// Delete mapping files

		File mappingDir = new File("/opt/apex-deployment/mappings");

		FileUtils.cleanDirectory(mappingDir);

		AnalyticsConfig analyticsConfig = AnalyticsConfig.getInstance();
		SshClient client = new SshClient(analyticsConfig.getApexHostUsername(),
				FileSystems.getDefault().getPath(analyticsConfig.getApexKeyFilePath()));

		// client.setKnownHosts(FileSystems.getDefault().getPath("/root/.ssh/known_hosts"));

		client.connect(analyticsConfig.getApexHostname());
		client.upload(FileSystems.getDefault().getPath("/opt/apex-deployment/" + appBundleName + ".apa"),
				"/tmp/" + appBundleName + ".apa");

		client.close();

		// Remove file locally now that its on the client node
		File appFile = new File("/opt/apex-deployment/" + appBundleName + ".apa");
		appFile.delete();

		launchApp();

		return appBundleName + ".apa";
	}

	/**
	 * Authenticate SSH using configured properties.
	 * 
	 * @return
	 * @throws IOException
	 */
	private static com.sshtools.j2ssh.SshClient authenticateSsh() throws IOException {
		AnalyticsConfig analyticsConfig = AnalyticsConfig.getInstance();
		return SshUtil.authenticateSsh(analyticsConfig.getApexHostname(), analyticsConfig.getApexHostUsername(),
				analyticsConfig.getApexKeyFilePath());
	}
}
