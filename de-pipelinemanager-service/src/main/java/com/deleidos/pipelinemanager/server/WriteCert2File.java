package com.deleidos.pipelinemanager.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;

public class WriteCert2File{
	@SuppressWarnings("restriction")
	public static void write(KeyStore ks, String alias)
	{
		System.out.println("Writing cert");
		try {
		    X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
//		    System.out.println(cert);
		    File file = new File("server.cert");
		    byte[] buf = cert.getEncoded();

		    FileOutputStream os = new FileOutputStream(file);
		    os.write(buf);

		    Writer wr = new OutputStreamWriter(os, Charset.forName("UTF-8"));
		    wr.write(new sun.misc.BASE64Encoder().encode(buf));
		    wr.flush();

		    wr.close();
		    os.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
}