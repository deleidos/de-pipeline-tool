package com.deleidos.framework.operators.abstractsplitter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.lib.io.SimpleSinglePortInputOperator;


public abstract class AbstractSplitter extends SimpleSinglePortInputOperator<String>{

	protected void LineSplitter(InputStream is, DefaultOutputPort<ArrayList<byte[]>> output, HashMap<String,String> props) throws IOException{
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line = null;
		String headRows = props.get("headerRows");
		int headRowsLeft = Integer.parseInt(headRows);
		ArrayList<byte[]> outArr = new ArrayList<byte[]>();
		ArrayList<byte[]> outputArr = new ArrayList<byte[]>();
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
		objectStream.writeObject(props);		
		outArr.add(byteStream.toByteArray());
		try{
		while ((line = reader.readLine()) != null) {
			if (headRowsLeft > 0){
				outArr.add(line.getBytes());
				headRowsLeft --;
			}
			else{
				for(int i = 0; i<outArr.size(); i++){
					outputArr.add(i, outArr.get(i));
				}
				outputArr.add(outArr.size(), line.getBytes());
				
				String data = new String(outputArr.get(1));
				
				
				if (data!=null){
					ArrayList<byte[]> outCopy = new ArrayList<byte[]>();
					for(byte[] elem : outputArr){
						outCopy.add(elem);
					}
					//ArrayList<byte[]> outCopy = new ArrayList<byte[]>(outputArr);		
					outputArr.clear();
					output.emit(outCopy);
					if(outCopy.size() > 5){
						System.out.println("outcopy size: " + outCopy.size());
					}
					
				}
				
			}
		}
	
		}finally{
			objectStream.close();
			byteStream.close();
			
			//reader.close();
		}
	}
	protected void JSONSplitter(InputStream is, DefaultOutputPort<ArrayList<byte[]>> output, HashMap<String,String> props) throws IOException, InterruptedException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String fin_out = "";
		String record = "";
		String line = null;
		int numOpen = 0;
		String rec = "";
		// Whether or not the characters being read are within quotes
		boolean inQuote = false;
		// Finished splitting a json object
		boolean done = false;
		ArrayList<byte[]> outArr = new ArrayList<byte[]>();
		ArrayList<byte[]> outputArr = new ArrayList<byte[]>();
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
		objectStream.writeObject(props);		
		outArr.add(byteStream.toByteArray());
		try{
			int count = 0;
		while ((line = reader.readLine()) != null) {
			
			String temp = line;
			if (count == 100000){
				Thread.sleep(80*1000);
				System.out.println("hit 100000");
				count = 0;
			}
			count ++;
			// Looks at each character in the line individually
			for (int a = 0; a < temp.length(); a++) {
				// Increments the number of open { if the the character was { and wasn't in a quote
				if (temp.charAt(a) == '{' && !inQuote) {
					numOpen++;
				// Decreases the number of open { if the character was } and wasn't in a quote
				} else if (temp.charAt(a) == '}' && !inQuote) {
					numOpen--;
				// Changes the status of being inside a quote if the character was " and didn't have a \ in front of it
				} else if (temp.charAt(a) == '\"') {
					// Looks to the last character in the previous line if the first character is a "
					if (a == 0 && record.charAt(record.length()-1) != '\\') {
						inQuote = !inQuote;
					// Looks to left of the " to see if it is a \
					} else if (a != 0 && temp.charAt(a - 1) != '\\') {
						inQuote = !inQuote;
					}
				}

				// Wraps up the json object and saves the remaining characters on the 
				// line if the first { has been closed by a }
				
				if (numOpen == 0) {
			
					rec= temp.substring(0, a + 1);
					for(int i = 0; i<outArr.size(); i++){
						outputArr.add(i, outArr.get(i));
					}
					outputArr.add(outArr.size(),rec.getBytes());
					ArrayList<byte[]> outCopy = new ArrayList<byte[]>();
					for(byte[] elem : outputArr){
						outCopy.add(elem);
					}
					//ArrayList<byte[]> outCopy = new ArrayList<byte[]>(outputArr);		
					outputArr.clear();
					if(!outCopy.isEmpty() && outCopy != null){
						output.emit(outCopy);
					}else{
						System.out.println("JSON splitter outputs null");
					}
					
					fin_out = rec;
					done = true;
				}
			}
			// Adds the line to the json object if it is not complete
			if (!done) {
				record += temp;
				
			}
		
		}
		if (!fin_out.equals(rec)){
			for(int i = 0; i<outArr.size(); i++){
				outputArr.add(i, outArr.get(i));
			}
			outputArr.add(outArr.size(),record.getBytes());
			ArrayList<byte[]> outCopy = new ArrayList<byte[]>();
			for(byte[] elem : outputArr){
				outCopy.add(elem);
			}
			//ArrayList<byte[]> outCopy = new ArrayList<byte[]>(outputArr);		
			outputArr.clear();
			output.emit(outCopy);
			
		}
		
	
	}finally{
		objectStream.close();
		byteStream.close();
		
		//reader.close();
	}
	}
}
