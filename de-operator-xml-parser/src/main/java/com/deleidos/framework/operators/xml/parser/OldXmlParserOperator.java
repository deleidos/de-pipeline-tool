package com.deleidos.framework.operators.xml.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.common.util.BaseOperator;

public class OldXmlParserOperator extends BaseOperator {

	//private static final Logger log = Logger.getLogger(XmlParserOperator.class);
	private Map<String, Integer> paths = new HashMap<String, Integer>();
	private List<String> parents = new ArrayList<String>();

	private final String OPEN_ARRAY_CHARACTER = "[";
	private final String CLOSE_ARRAY_CHARACTER = "]";
	private final String OBJECT_SEPERATOR = ".";
	
	private boolean retainTuples = false;
	private boolean debug = false;
	
	public transient DefaultInputPort<Object> input = new DefaultInputPort<Object>() {
		@Override
		public void process(Object inputTuple) {
			
			ArrayList<byte[]> in = new ArrayList<byte[]>();
			if (inputTuple instanceof String) {
				in.add(null);
				in.add(((String)inputTuple).getBytes());
			} else if (inputTuple instanceof byte[]) {
					in.add(null);
					in.add((byte[]) inputTuple);
			} else if (inputTuple instanceof ArrayList<?> && ((ArrayList<?>)inputTuple).get(1) instanceof byte[]) {
				// don't run else
			} else {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Type", inputTuple.getClass().getName());
				map.put("Value", inputTuple.toString());
				output.emit(map);
				return;
			}
			
			// Skipped if else is reached
			try {
				processTuple(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	
	public List<Map<String, String>> retainedTuples;
	protected int count;

	public transient DefaultOutputPort<Map<String, String>> output = new DefaultOutputPort<Map<String, String>>();

	public void processTuple(ArrayList<byte[]> tuple) throws IOException {
		
		if (tuple != null) {
			HashMap <String, String> map = new HashMap<String, String>();
			loadXML(map, new ByteArrayIterator(tuple.get(1)));
			if (retainTuples) retainedTuples.add(map);
			if (debug) count += map.size();
			output.emit(map);
		}
	}
	
	private String toPath(List<String> list) {
		String path = list.stream().reduce("", new BinaryOperator<String>() {

			@Override
			public String apply(String arg0, String arg1) {
				return arg0 + OBJECT_SEPERATOR + arg1;
			}
			
		});
		
		return path.substring(1);
	}
	
	private String readTag(Iterator<Integer> xml) {
		String ret = "";
		int i = xml.next();
		if (i == '/') { // closing tag
			ret = "/";
		}
		while (i != '>' && i != ' ' && i != '/') {
			ret += (char)i;
			i = xml.next();
			if (i == '/') { // self-closing tag
				ret = null;
				break;
			}
		}
		while (i != '>') i = xml.next();
		return ret;
	}
	
	private void loadXML(Map<String, String> map, Iterator<Integer> xml) throws IOException {
		String content = "";
		while (xml.hasNext()) {
			int i = xml.next();
			if (i == '<') {
				// Write content to current path
				if (!parents.isEmpty() && !content.equals("")) {
					String path = toPath(parents);
					if (paths.containsKey(path)) {
						// Convert first entry to array entry
						if (paths.get(path) == 1) {
							map.put(path + OPEN_ARRAY_CHARACTER + "1" + CLOSE_ARRAY_CHARACTER, map.get(path));
							map.remove(path);
						}
						paths.put(path, paths.get(path) + 1);
						path += OPEN_ARRAY_CHARACTER + paths.get(path) + CLOSE_ARRAY_CHARACTER;
					}
					paths.put(path, 1);
					map.put(path, content);
					content = new String();
				}
				String tag = readTag(xml);
				// Tag
				if (tag != null) {
					// Closing tag
					if (tag == "/") {
						parents.remove(parents.size() - 1);
					}
					else {
						parents.add(tag);
					}
				}
				// Ignore self-closing tags
			}
			else {
				content += (char)i;
			}
			
		}
	}
	
	public void retainTuples() {
		retainTuples = true;
		retainedTuples = new ArrayList<Map<String, String>>();
	}
	
	public void debugOn() {
		debug = true;
		count = 0;
	}
	
}
