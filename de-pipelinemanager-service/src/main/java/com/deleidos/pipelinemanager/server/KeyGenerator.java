package com.deleidos.pipelinemanager.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertInfo;
import sun.security.x509.CertificateExtensions;
import sun.security.x509.BasicConstraintsExtension;
import sun.security.x509.X509CertImpl;

@SuppressWarnings("restriction")
public class KeyGenerator {
	public static void generate()
	{
      try{
            CertAndKeyGen keyGen=new CertAndKeyGen("RSA","SHA512withRSA",null);
            keyGen.generate(1024);
            Key pubKey = keyGen.getPublicKey();
            PrivateKey rootPrivateKey=keyGen.getPrivateKey();
            //Generate self signed certificate
            X509Certificate[] chain=new X509Certificate[1];
            X509Certificate rootCertificate = keyGen.getSelfCertificate(new X500Name("CN=ROOT"), (long) 365 * 24 * 60 * 60);
            rootCertificate   = createSignedCertificate(rootCertificate,rootCertificate,rootPrivateKey);
            chain[0] = rootCertificate;
//            System.out.println("Certificate : "+chain[0].toString());

            String alias = "serverKey";
            char[] password = "password".toCharArray();
            String keystore = "serverKeys.jks";
            
            KeyStore ks = KeyStore.getInstance("jks");
            ks.load(null, null);
            ks.setKeyEntry(alias, rootPrivateKey, "password".toCharArray(), chain);
            
            storeKeyAndCertificateChain(alias, password, keystore,rootPrivateKey, chain);
        
            //Reload the keystore and display key and certificate chain info
//            loadAndDisplayChain(alias, password, keystore);

            //Clear the keystore
//            clearKeyStore(alias, password, keystore);
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
	}
	private static void storeKeyAndCertificateChain(String alias, char[] password, String keystore, Key key, X509Certificate[] chain) throws Exception{
        KeyStore keyStore=KeyStore.getInstance("jks");
        keyStore.load(null,null);
         
        keyStore.setKeyEntry(alias, key, password, chain);
        keyStore.store(new FileOutputStream(keystore),password);
        WriteCert2File.write(keyStore, alias);
    }
	 private static void loadAndDisplayChain(String alias,char[] password, String keystore) throws Exception{
	        //Reload the keystore
	        KeyStore keyStore=KeyStore.getInstance("jks");
	        keyStore.load(new FileInputStream(keystore),password);
	         
	        Key key=keyStore.getKey(alias, password);
	         
	        if(key instanceof PrivateKey){
	            System.out.println("Get private key : ");
	            System.out.println(key.toString());
	             
	            Certificate[] certs=keyStore.getCertificateChain(alias);
	            System.out.println("Certificate chain length : "+certs.length);
	            for(Certificate cert:certs){
	                System.out.println(cert.toString());
	            }
	        }else{
	            System.out.println("Key is not private key");
	        }
	    }
    private static void clearKeyStore(String alias,char[] password, String keystore) throws Exception{
        KeyStore keyStore=KeyStore.getInstance("jks");
        keyStore.load(new FileInputStream(keystore),password);
        keyStore.deleteEntry(alias);
        keyStore.store(new FileOutputStream(keystore),password);
    }
    private static X509Certificate createSignedCertificate(X509Certificate cetrificate,X509Certificate issuerCertificate,PrivateKey issuerPrivateKey){
        try{
            Principal issuer = issuerCertificate.getSubjectDN();
            String issuerSigAlg = issuerCertificate.getSigAlgName();
              
            byte[] inCertBytes = cetrificate.getTBSCertificate();
            X509CertInfo info = new X509CertInfo(inCertBytes);
            info.set(X509CertInfo.ISSUER, (X500Name) issuer);
             
            //No need to add the BasicContraint for leaf cert
            if(!cetrificate.getSubjectDN().getName().equals("CN=ROOT")){
                CertificateExtensions exts=new CertificateExtensions();
                BasicConstraintsExtension bce = new BasicConstraintsExtension(true, -1);
                exts.set(BasicConstraintsExtension.NAME,new BasicConstraintsExtension(false, bce.getExtensionValue()));
                info.set(X509CertInfo.EXTENSIONS, exts);
            }
            X509CertImpl outCert = new X509CertImpl(info);
            outCert.sign(issuerPrivateKey, issuerSigAlg);
             
//            System.out.println(outCert);
            return outCert;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
	
    

	public static void read()
	{
		String fn = System.getProperty("user.dir")+"/server.cert";
		File f = new File(fn);
		System.out.println(fn + " " + f.exists());
		try{
		    CertificateFactory cf = CertificateFactory.getInstance("X.509");
		    Certificate cert = cf.generateCertificate(new FileInputStream(fn));
		    System.out.println(cert);
		}catch(Exception ex){
		    ex.printStackTrace();
		}
	}
	public static void main(String[] args)
	{
		generate();
		
		read();
	}
}
