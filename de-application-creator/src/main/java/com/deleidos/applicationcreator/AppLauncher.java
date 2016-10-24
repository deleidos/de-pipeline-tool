package com.deleidos.applicationcreator;

import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.deleidos.analytics.common.rest.RestClient;
import com.deleidos.analytics.config.AnalyticsConfig;
import com.deleidos.framework.model.event.SystemEventBus;
import com.deleidos.framework.monitoring.response.AppsResponse;
import com.deleidos.framework.monitoring.response.App;
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
	private RestClient rc;
	private int waitTime = 500;
	/**
	 * Private no-arg constructor enforces the singleton pattern.
	 */
	private AppLauncher() {
		rc = new RestClient(String.format("http://%s:8088", AnalyticsConfig.getInstance().getApexNameNodeHostname()));
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
		logger.info("first hostname: " + analyticsConfig.getApexClientNodeHostname() + " path: "
				+ analyticsConfig.getApexKeyFilePath());
		com.sshtools.j2ssh.SshClient ssh = SshUtil.authenticateSsh(analyticsConfig.getApexClientNodeHostname(),
				analyticsConfig.getApexHostUsername(), analyticsConfig.getApexKeyFilePath());
		SessionChannelClient session = ssh.openSessionChannel();
		SessionOutputReader sor = new SessionOutputReader(session);
		session.requestPseudoTerminal("vt100", 80, 25, 0, 0, "");
		if (session.startShell()) {

			String appBundleName = config.getAppBundleName();
			OutputStream out = session.getOutputStream();
			out.write("sudo su\n".getBytes());
			out.flush();
			out.write(("docker cp /tmp/" + appBundleName + ".apa hadoop-client:/tmp/" + appBundleName + ".apa\n")
					.getBytes());
			out.flush();
			out.write("docker exec -it hadoop-client bash\n".getBytes());
			out.flush();
			out.write(". /etc/profile.d/apex_env.sh\ndtcli \n".getBytes());
			out.flush();

			String read = "";
			while (!read.contains("dt>")) {
				read = sor.getOutput();
				Thread.sleep(waitTime);
			}
			out.write(("launch " + appBundleName + ".apa\n").getBytes());
			out.flush();

			read = "";
			while (!read.contains("dt (")) {
				read = sor.getOutput();
				Thread.sleep(waitTime);
			}
			out.write("exit\n".getBytes());
			out.flush();

			read = "";
			while (!read.contains("exit")) {
				read = sor.getOutput();
				Thread.sleep(waitTime);
			}
			out.write("exit\n".getBytes());
			out.flush();

			read = "";
			while (!read.contains("exit")) {
				read = sor.getOutput();
				Thread.sleep(waitTime);
			}

			out.close();
		}
		session.close();
	}

	/**
	 * Runnable application launch executor. Blocks on the queue until a launch config is available. Will continue to
	 * launch configs until the queue is empty, at which time it will block again. Waits between launches until the
	 * newly launched app is actually running.
	 */
	private class AppLaunchExecutor implements Runnable {

		private static final long maxWaitTime = 30000; // 30 seconds

		@Deprecated
		public void waitForApp(String appName) throws Exception {
			logger.info("Beginning wait for app " + appName);
			boolean finished = false;
			long startTime = System.currentTimeMillis();
			while (!finished) {
				// Don't wait longer than the max wait time.
				logger.info("Elapsed time waiting for app: " + (System.currentTimeMillis() - startTime));
				if (System.currentTimeMillis() - startTime >= maxWaitTime) {
					logger.info("Timed out waiting for app " + appName);
					finished = true;
				}
				else {
					logger.info("Gettings apps in waitForApp...");
					long rcStart = System.currentTimeMillis();
					AppsResponse appsResponse = rc.getObject(AppsResponse.PATH, AppsResponse.class, true);
					logger.info("Got Apps in waitForApp, took " + (System.currentTimeMillis() - rcStart) + " ms");
					for (App app : appsResponse.getApps().getApp()) {
						if (app.getName().equals(appName)) {
							switch (app.getState()) {
							case "RUNNING":
								logger.info("app is running: " + appName);
								finished = true;
								break;
							}

							if (finished) {
								break; // Exit the loop.
							}
						}
					}

					if (!finished) {
						logger.info("Sleeping 1 minute waiting for app " + appName);
						Thread.sleep(60000);
					}
				}
			}
		}

		/**
		 * Check to see if the app is already running.
		 * 
		 * @param appName
		 * @return
		 * @throws Exception
		 */
		private boolean isAppRunning(String appName) throws Exception {
			boolean response = false;
			AppsResponse appsResponse = rc.getObject(AppsResponse.PATH, AppsResponse.class, true);
			for (App app : appsResponse.getApps().getApp()) {
				if (app.getName().equals(appName) && app.getState().equalsIgnoreCase("RUNNING")) {
					response = true;
				}
			}
			return response;
		}

		@Override
		public void run() {

			while (true) {
				try {
					logger.info("Taking from queue");
					AppLaunchConfig app = queue.take();
					logger.info("Queue size after take: " + queue.size());

					if (isAppRunning(app.getAppBundleName())) {
						logger.warn("App " + app.getAppBundleName() + " is already running. Aborting launch.");
					}
					else {
						logger.info("Launching app " + app.getAppBundleName());
						doLaunch(app);
					}
					// waitForApp(app.getAppBundleName());

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
