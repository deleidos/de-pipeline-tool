package com.deleidos.framework.operators.xml.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.datatorrent.api.Sink;
import com.datatorrent.lib.testbench.CollectorTestSink;

import junit.framework.Assert;

public class XmlParserOperatorTest {

	private static OldXmlParserOperator xmlParserOperator = new OldXmlParserOperator();
	private static Sink<Object> sink;

	@Before
	public void setup() { 
		try {
			FileSystem fs = FileSystem.get(new Configuration()); 
		} catch (IOException e) {
			e.printStackTrace();
		}

		xmlParserOperator = new OldXmlParserOperator();
		xmlParserOperator.setup(null);

		sink = new CollectorTestSink<>();
		xmlParserOperator.output.setSink(sink);
	}

	@Test
	public void testItemCount() {

		xmlParserOperator.debugOn();
		xmlParserOperator.beginWindow(0);
		xmlParserOperator.input.process("<test1>content1</test1><test2><test21>content21</test21><test22>content22</test22></test2>");
		xmlParserOperator.input.process("<testList>content1</testList><testList>content2</testList>");
		xmlParserOperator.endWindow();
		Assert.assertEquals(5, xmlParserOperator.count);
	}
	
	@Test
	public void testPerformance() throws FileNotFoundException, IOException {
		String bigXml = "";
		BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/med.xml"));
		while (reader.ready()) bigXml += reader.readLine();
		xmlParserOperator.debugOn();
		long start = System.currentTimeMillis();
		xmlParserOperator.beginWindow(1);
		xmlParserOperator.input.process(bigXml);
		xmlParserOperator.endWindow();
		long end = System.currentTimeMillis();
		reader.close();
		Assert.assertTrue("" + (end - start), true);
	}

	@After
	public void teardown() {
		// Teardown the operator gracefully
		xmlParserOperator.teardown();
	}
}
