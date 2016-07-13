package com.deleidos.analytics.websocket;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * A main that uses a WebSocket Client to send test messages or messages from a file.
 */
public class StreamingClient {

	public static void main(String[] args) throws Exception {
		if (args.length != 4) {
			System.out.println(
					"com.deleidos.analytics.websocket.service.StreamingClient <serverURI> <(dataFile | message)> <topic> <delay>");
			System.exit(-1);
		}

		String serverUri = args[0];
		String dataFile = args[1];
		String topic = args[2];
		Integer delay = Integer.parseInt(args[3]);
		String streamRequest = "{\"stream\":\"%s\"}";

		WebSocketClient client = new WebSocketClient(serverUri);
		client.setInitRequest(String.format(streamRequest, topic));
		client.connect();

		File dir = new File(".");
		File file = new File(dir.getCanonicalPath() + File.separator + dataFile);
		
		if (file.exists()) {
			List<String> lines = FileUtils.readLines(file, "UTF-8");
			boolean stop = false;
			while (!stop) {
				for (String message : lines) {
					client.sendString(message);
					Thread.sleep(delay);
				}
				Thread.sleep(delay);
			}
			client.close();
		} else {
			// 2nd arg is not a file, assuming just a simple message to send
			String msg = dataFile;
			boolean stop = false;
	    	int i = 0;
	    	while (!stop) {
	    		String message = String.format("{\"message\":\"%s %d\"}", msg, ++i);
	    		client.sendString(message);
	    		Thread.sleep(delay);
	    	}
	    	client.close();
		}
	}
}
