package com.deleidos.analytics.common.ssh;

import java.nio.file.FileSystems;
import java.util.List;

import org.junit.Test;

/**
 * SshClient unit test.
 * 
 * @author John Yoon
 */
public class SshClientTest {

	@Test
	public void testSshClient() throws Exception {
		SshClient client = new SshClient("ubuntu",
				FileSystems.getDefault().getPath("src/main/resources/aws-interns-20150615.ppk"));
		client.setKnownHosts(FileSystems.getDefault().getPath("src/main/resources/known_hosts"));
		client.connect("54.243.234.230");
		System.out.println("Executing");
		List<String> children = client.listChildrenNames("/home");
		System.out.println("Size: " + children.size());
		client.execute("whoami");
		System.out.println("Done");
		client.close();
	}
}
