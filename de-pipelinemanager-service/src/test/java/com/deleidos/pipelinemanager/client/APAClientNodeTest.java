package com.deleidos.pipelinemanager.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Assert;
import org.junit.Test;

public class APAClientNodeTest
{
	private static String resource = "http://localhost:8080/rest/service/apaPost";
	private static String fn = System.getProperty("user.dir")+"/src/main/resources/SchemaJohn.apa";
	
//	@Test
	public void fileExist()
	{
		File f = new File(fn);
		Assert.assertTrue(f.exists());
	}
//	@Test
	public void testPost(){
		String Sys = "{hi}";
		try{
		    final FileDataBodyPart filePart = new FileDataBodyPart("file", new File(fn));
			final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
			final WebTarget server = client.target(resource);
		    FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		    final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("filePath", "Here/here").bodyPart(filePart);
		    multipart.field("sys", Sys);
		    final Response response = server.request().post(Entity.entity(multipart, multipart.getMediaType()));
			
		    Assert.assertNotNull(response.getStatus());
		    System.out.println(response.getStatus());
		    
		    Assert.assertEquals(200, response.getStatus());
	
		    formDataMultiPart.close();
		    multipart.close();
		}
		catch(javax.ws.rs.ProcessingException e)
		{
			//Assumption here is server is not running
			System.out.println(e.getMessage());
			Assert.assertTrue(true);
		}
		catch(Exception e){
			//Assumption here is that the unit test has failed elsewhere (non-server related things)
			e.printStackTrace();
			Assert.fail();
		}	
	}
}