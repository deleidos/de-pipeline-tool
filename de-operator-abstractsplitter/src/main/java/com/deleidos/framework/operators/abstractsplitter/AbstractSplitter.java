package com.deleidos.framework.operators.abstractsplitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.lib.io.SimpleSinglePortInputOperator;
import com.deleidos.framework.operators.common.InputTuple;
import com.deleidos.framework.operators.common.OperatorSystemInfo;

public abstract class AbstractSplitter extends SimpleSinglePortInputOperator<String> implements OperatorSystemInfo {

	protected void LineSplitter(InputStream is, DefaultOutputPort<InputTuple> output, int headerRows)
			throws IOException, InterruptedException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line = null;
		InputTuple outTuple = new InputTuple();
		ArrayList<String> headers = new ArrayList<String>();
		int headRowsLeft = headerRows;

		int count = 0;
		while ((line = reader.readLine()) != null) {

			if (count == 100000) {
				Thread.sleep(80 * 1000);
				System.out.println("hit 100000");
				count = 0;
			}
			count++;
			if (line.contains("firstrecord")) {
				System.out.println("first record in: " + System.currentTimeMillis());
			}
			else if (line.contains("lastrecord")) {
				System.out.println("last record in: " + System.currentTimeMillis());
			}
			if (headRowsLeft > 0) {
				headers.add(line);
				headRowsLeft--;
			}
			else {

				String data = line;

				if (data != null) {
					outTuple.setData(data);
					outTuple.setHeader(headers);
					output.emit(outTuple);

				}

			}
		}

	}

	protected void JSONSplitter(InputStream is, DefaultOutputPort<InputTuple> output, int headerRows)
			throws IOException, InterruptedException {
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
		List<String> headers = null;
		InputTuple outTuple = new InputTuple();
		outTuple.setHeader(headers);

		int count = 0;
		while ((line = reader.readLine()) != null) {

			String temp = line;
			if (count == 100000) {
				Thread.sleep(80 * 1000);
				System.out.println("hit 100000");
				count = 0;
			}
			count++;
			// Looks at each character in the line individually
			for (int a = 0; a < temp.length(); a++) {
				// Increments the number of open { if the the character was
				// { and wasn't in a quote
				if (temp.charAt(a) == '{' && !inQuote) {
					numOpen++;
					// Decreases the number of open { if the character was }
					// and wasn't in a quote
				}
				else if (temp.charAt(a) == '}' && !inQuote) {
					numOpen--;
					// Changes the status of being inside a quote if the
					// character was " and didn't have a \ in front of it
				}
				else if (temp.charAt(a) == '\"') {
					// Looks to the last character in the previous line if
					// the first character is a "
					if (a == 0 && record.charAt(record.length() - 1) != '\\') {
						inQuote = !inQuote;
						// Looks to left of the " to see if it is a \
					}
					else if (a != 0 && temp.charAt(a - 1) != '\\') {
						inQuote = !inQuote;
					}
				}

				// Wraps up the json object and saves the remaining
				// characters on the
				// line if the first { has been closed by a }

				if (numOpen == 0) {

					rec = temp.substring(0, a + 1);
					outTuple.setData(rec);
					output.emit(outTuple);

					fin_out = rec;
					done = true;
				}
			}
			// Adds the line to the json object if it is not complete
			if (!done) {
				record += temp;

			}

		}
		if (!fin_out.equals(rec)) {
			outTuple.setData(record);

			output.emit(outTuple);

		}

	}
}
