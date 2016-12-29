package com.deleidos.pipelinemanager;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
/**
 * @author shange
 * <p>
 * embedded jetty runner, if necessary
 * </p>
 *
 */
public class App 
{
    public static void main( String[] args )
    { 

        String rootPath = System.getProperty("user.dir");
        String webAppPath = rootPath + "/src/main/webapp";
        Server server = new Server(80);
         
        WebAppContext context = new WebAppContext();
        context.setResourceBase(webAppPath);
        context.setContextPath("/de-pipelinemanager-service");
        server.setHandler(context);

        try {
			server.start();
	        server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			server.destroy();
		}
    }
}
