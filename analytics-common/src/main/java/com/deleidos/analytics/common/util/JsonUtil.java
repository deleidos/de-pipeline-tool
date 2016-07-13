package com.deleidos.analytics.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

public class JsonUtil {

	private final static Logger logger = Logger.getLogger(JsonUtil.class);
	private static final ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
	}

	public static <T> T fromJsonString(String jsonString, Class<T> genericType) throws Exception {
		T jsonObj = null;
		try {
			jsonObj = mapper.readValue(jsonString, genericType);
		}
		catch (Exception e) {
			logger.error("JSON deserialization failed for: " + jsonString);
			logger.error(e.toString());
			throw e;
		}
		return jsonObj;
	}
	
	public static <T> T fromJsonString(String jsonString, Class<T> genericType, boolean camelCaseJson) throws Exception {
		if (camelCaseJson) mapper.setPropertyNamingStrategy(null);
		T ret = fromJsonString(jsonString, genericType);
		if (camelCaseJson) mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		return ret;
	}

	public static String toJsonString(Object obj) throws Exception {
		String json = null;
		try {
			json = mapper.writeValueAsString(obj);
		}
		catch (Exception e) {
			logger.error("JSON serialization failed for: " + obj, e);
			throw e;
		}
		return json;
	}

	public static JsonNode parseJson(String jsonString) throws Exception {
		JsonNode rootNode = null;
		try {
			rootNode = mapper.readTree(jsonString);
		}
		catch (Exception e) {
			logger.error("JSON deserialization failed for: " + jsonString, e);
			throw e;
		}
		return rootNode;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseJsonToMap(String json) throws Exception {
		return (Map<String, Object>) mapper.readValue(json, HashMap.class);
	}
	
	public static void loadFromFile(String fileName, Object obj) throws Exception {
		InputStream is = getFileAsStream(fileName);
		try {
			mapper.readerForUpdating(obj).readValue(is);
		}
		catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	private static InputStream getFileAsStream(String fileName) {
		InputStream is = null;
		File path = new File(".");
		File f = new File(path, fileName);
		logger.info("Attempting to find file " + fileName);
		if (f.exists()) {
			logger.info("Loading file from the file system " + f.getAbsolutePath());
			try {
				is = new FileInputStream(f);
			}
			catch (FileNotFoundException e) {
				logger.warn(e);
			}
		}
		else {
			logger.info("File not found on file system, checking on classpath...");
			is = JsonUtil.class.getClassLoader().getResourceAsStream(fileName);
		}
		if (is == null) {
			logger.error("File " + fileName + " could not be found");
		}
		return is;
	}

}
