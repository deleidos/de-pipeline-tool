package com.deleidos.applicationcreator;

import java.io.File;
import java.io.IOException;

import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile;

/**
 * SSH utilities.
 */
public class SshUtil {

	/**
	 * Authenticate SSH.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static com.sshtools.j2ssh.SshClient authenticateSsh(String hostname, String username, String keyFilePath)
			throws IOException {
		com.sshtools.j2ssh.SshClient ssh = new com.sshtools.j2ssh.SshClient();
		ssh.connect(hostname, new IgnoreHostKeyVerification());

		PublicKeyAuthenticationClient pk = new PublicKeyAuthenticationClient();
		pk.setUsername(username);
		SshPrivateKeyFile file = SshPrivateKeyFile.parse(new File(keyFilePath));
		SshPrivateKey key = file.toPrivateKey(null);
		pk.setKey(key);

		int result = ssh.authenticate(pk);
		if (result != AuthenticationProtocolState.COMPLETE) {
			throw new RuntimeException("Authentication failed.");
		}

		return ssh;
	}

}
