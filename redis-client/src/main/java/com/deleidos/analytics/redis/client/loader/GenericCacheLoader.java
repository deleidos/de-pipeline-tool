package com.deleidos.analytics.redis.client.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import com.deleidos.analytics.common.logging.LogUtil;
import com.deleidos.analytics.common.util.FileUtil;
import com.deleidos.analytics.common.util.GsonFactory;
import com.deleidos.analytics.common.util.NumberUtil;
import com.deleidos.analytics.redis.client.RedisClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Generic Redis data cache loader.
 * 
 * File types supports are .csv and .json.
 * 
 * Files must have one record per line. CSV files must have the header on the first line.
 * 
 * @author vernona
 */
public class GenericCacheLoader {

	private static final Logger log = Logger.getLogger(GenericCacheLoader.class);

	private static final char csvDelimiter = ',';

	@SuppressWarnings("unused")
	private String redisHost;
	private String filePath;
	private String namespace;
	private String keyField;

	private Gson gson;
	private RedisClient redisClient;

	/**
	 * Constructor.
	 * 
	 * @param redisHost
	 */
	public GenericCacheLoader(String redisHost, String filePath, String namespace, String keyField) {
		this.redisHost = redisHost;
		this.filePath = filePath;
		this.namespace = namespace;
		this.keyField = keyField;

		gson = GsonFactory.getInstance().getGsonWithCollectionDeserializers();
		redisClient = new RedisClient(redisHost);
	}

	/**
	 * Load a file and cache its contents.
	 * 
	 * @throws Exception
	 */
	public void loadFile() throws Exception {
		if (filePath.endsWith("csv")) {
			String data = FileUtil.getFileContentsAsString(new File(filePath));
			try (CSVParser parser = CSVParser.parse(data,
					CSVFormat.DEFAULT.withFirstRecordAsHeader().withDelimiter(csvDelimiter))) {
				for (CSVRecord record : parser) {
					Map<String, Integer> headerMap = parser.getHeaderMap();
					Map<String, Object> map = new LinkedHashMap<String, Object>();
					for (String field : headerMap.keySet()) {
						String value = record.get(field);
						Number num = NumberUtil.parseNumber(value);
						map.put(field, num == null ? value : num);
					}
					cacheObject(map, namespace, keyField);
				}
			}
		}
		else if (filePath.endsWith("json")) {
			Type type = new TypeToken<Map<String, Object>>() {
			}.getType();

			try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
				for (String line; (line = br.readLine()) != null;) {
					cacheObject(gson.fromJson(line, type), namespace, keyField);
				}
			}
		}
	}

	/**
	 * Cache the given object in Redis.
	 * 
	 * @param map
	 * @param namespace
	 * @param keyField
	 */
	private void cacheObject(Map<String, Object> map, String namespace, String keyField) {
		Object keyValue = map.get(keyField);
		if (keyValue != null && (keyValue instanceof String || keyValue instanceof Number)) {
			String key = keyValue.toString();
			log.info("caching " + keyValue.toString() + ":" + gson.toJson(map));
			redisClient.setValue(namespace, key, gson.toJson(map));
			log.info("retrieved value from cache " + key + ":" + redisClient.getValue(namespace, key));
		}
		else {
			log.warn("skipping record, key field did not have a non-null primitive value:" + keyValue);
		}
	}

	public static void main(String[] args) throws Exception {
		LogUtil.initializeLog4jConsoleAppender();

		validateArgs(args);
		String redisHost = args[0];
		String filePath = args[1];
		String namespace = args[2];
		String keyField = args[3];

		GenericCacheLoader gcl = new GenericCacheLoader(redisHost, filePath, namespace, keyField);
		gcl.loadFile();
	}

	private static void validateArgs(String[] args) {
		if (args == null || args.length != 4) {
			throw new IllegalArgumentException();
		}
		for (String arg : args) {
			log.debug(arg);
		}
	}
}
