package com.deleidos.applicationcreator;

import java.io.File;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.deleidos.analytics.config.AnalyticsConfig;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient;
import com.sshtools.j2ssh.session.SessionChannelClient;
import com.sshtools.j2ssh.session.SessionOutputReader;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile;

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
			
			Thread.sleep(1000 * 20);
			out.write(("launch " + appBundleName + ".apa\n").getBytes());
			Thread.sleep(1000 * 40);
			out.write("exit\n".getBytes());
			Thread.sleep(1000*2);
			out.write("exit\n".getBytes());
			Thread.sleep(1000*2);
			
			out.close();
		}
		session.close();
	}

	/**
	 * Runnable application launch executor. Blocks on the queue until a launch config is available. Will continue to
	 * launch configs until the queue is empty, at which time it will block again. Waits a minute between launches.f
	 * 
	 * @author vernona
	 */
	private class AppLaunchExecutor implements Runnable {

		@Override
		public void run() {

			while (true) {
				try {
					doLaunch(queue.take());
					Thread.sleep(60000); // Wait a minute.
				}
				catch (Throwable t) {
					logger.error("Error launching application", t);
				}
			}
		}

	}
}
