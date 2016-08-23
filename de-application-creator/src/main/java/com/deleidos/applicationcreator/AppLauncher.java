package com.deleidos.applicationcreator;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.rest.RestClient;
import com.deleidos.analytics.common.util.JsonUtil;
import com.deleidos.analytics.config.AnalyticsConfig;
import com.deleidos.framework.model.event.SystemEventBus;
import com.deleidos.framework.monitoring.Ec2ResourceFinder;
import com.deleidos.framework.monitoring.response.AppsResponse;
import com.deleidos.framework.monitoring.response.AppsResponse.AppWrapper.App;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sshtools.j2ssh.session.SessionChannelClient;
import com.sshtools.j2ssh.session.SessionOutputReader;

/**
 * Queue-based app launcher that guarantees multiple apps will not launch simultaneously.
 * 
 * @author vernona
 * @author doylea - the body of the doLaunch method
 */
public class AppLauncher {

	private Logger logger = Logger.getLogger(AppLauncher.class);
	private static AppLauncher instance = new AppLauncher();
	private AppLaunchExecutor executor;
	private Thread executorThread;
	private LinkedBlockingQueue<AppLaunchConfig> queue;

	/**
	 * Private no-arg constructor enforces the singleton pattern.
	 */
	private AppLauncher() {
		queue = new LinkedBlockingQueue<AppLaunchConfig>();
		executor = new AppLaunchExecutor();
		executorThread = new Thread(executor);
		executorThread.start();
	}

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static AppLauncher getInstance() {
		return instance;
	}

	/**
	 * Queue an app for launch.
	 * 
	 * @param config
	 */
	public void launchApp(AppLaunchConfig config) {
		queue.offer(config);
	}

	/**
	 * Launch the app.
	 * 
	 * @param config
	 * @throws Exception
	 */
	private void doLaunch(AppLaunchConfig config) throws Exception {

		AnalyticsConfig analyticsConfig = AnalyticsConfig.getInstance();
		com.sshtools.j2ssh.SshClient ssh = SshUtil.authenticateSsh(analyticsConfig.getApexHostname(),
				analyticsConfig.getApexHostUsername(), analyticsConfig.getApexKeyFilePath());
		SessionChannelClient session = ssh.openSessionChannel();
		SessionOutputReader sor = new SessionOutputReader(session);
		session.requestPseudoTerminal("vt100", 80, 25, 0, 0, "");
		if (session.startShell()) {

			String appBundleName = config.getAppBundleName();
			OutputStream out = session.getOutputStream();
			out.write("sudo su\n".getBytes());
			out.write(("docker cp /tmp/" + appBundleName + ".apa hadoop-client:/tmp/" + appBundleName + ".apa\n")
					.getBytes());
			out.write("docker exec -it hadoop-client bash\n".getBytes());
			out.write(". /etc/profile.d/apex_env.sh\ndtcli \n".getBytes());

			// Thread.sleep(1000 * 20);
			String read = "";
			while (!read.contains("dt>")) {
				read = sor.getOutput();
				Thread.sleep(1000 * 20);
			}
			out.write(("launch " + appBundleName + ".apa\n").getBytes());
			// Thread.sleep(1000 * 40);
			read = "";
			while (!read.contains("dt (")) {
				read = sor.getOutput();
				Thread.sleep(1000 * 20);
			}
			out.write("exit\n".getBytes());
			// Thread.sleep(1000*2);
			read = "";
			while (!read.contains("exit")) {
				read = sor.getOutput();
				Thread.sleep(1000 * 20);
			}
			out.write("exit\n".getBytes());
			// Thread.sleep(1000*2);
			read = "";
			while (!read.contains("exit")) {
				read = sor.getOutput();
				Thread.sleep(1000 * 20);
			}

			out.close();
		}
		session.close();
	}

	private static RestClient rc;
	private static AppsResponse aResponse;

	/**
	 * Runnable application launch executor. Blocks on the queue until a launch config is available. Will continue to
	 * launch configs until the queue is empty, at which time it will block again. Waits between launches until the
	 * newly launched app is actually running.
	 */
	private class AppLaunchExecutor implements Runnable {

		public void waitForApp(String appList, String appName) throws InterruptedException {
			boolean fin = false;
			while (fin == false) {
				JsonParser parser = new JsonParser();
				JsonObject fullObject = parser.parse(appList).getAsJsonObject();
				JsonArray arr = fullObject.get("apps").getAsJsonArray();
				int val = 0;
				JsonObject finObj = new JsonObject();
				for (int i = 0; i < arr.size(); i++) {
					JsonObject obj = arr.get(i).getAsJsonObject();

					if (obj.get("name").getAsString().equals(appName)) {
						int check = Integer.parseInt(obj.get("id").getAsString().split("_")[2]);

						if (check > val) {
							val = check;
							finObj = obj;
						}
					}
				}
				if (finObj.entrySet().isEmpty()) {
					Thread.sleep(60000);
				}
				else if (finObj.get("state").getAsString().equals("RUNNING")) {

					logger.info("app is running: " + appName);
					fin = true;
				}
				else {
					Thread.sleep(60000);
				}
			}
		}

		@Override
		public void run() {

			while (true) {
				try {
					AppLaunchConfig app = queue.take();
					doLaunch(app);
					String appName = app.getAppBundleName();

					rc = new RestClient(String.format("http://%s:8088",
							Ec2ResourceFinder.instance.lookupPrivateIp("tag:Name", "Hadoop (auto test) - Name Node")));
					aResponse = rc.getObject(AppsResponse.PATH, AppsResponse.class, true);
					JsonNodeFactory factory = JsonNodeFactory.instance;
					ObjectNode root = factory.objectNode();
					ArrayNode data = root.putArray("apps");
					if (aResponse != null && aResponse.apps != null) {
						for (App a : aResponse.apps.app) {
							ObjectNode o = data.addObject();
							o.setAll((ObjectNode) JsonUtil.parseJson(JsonUtil.toJsonString(a)));
						}
					}
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					new ObjectMapper().writeTree(new JsonFactory().createGenerator(stream), root);
					String appslist = stream.toString(java.nio.charset.StandardCharsets.UTF_8.name());
					waitForApp(appslist, appName);

					// Notify that the deployment is complete.
					SystemEventBus.getInstance().deploymentComplete(app.getSystemDescriptor().get_id());
				}
				catch (Throwable t) {
					logger.error("Error launching application", t);
				}
			}
		}

	}
}
