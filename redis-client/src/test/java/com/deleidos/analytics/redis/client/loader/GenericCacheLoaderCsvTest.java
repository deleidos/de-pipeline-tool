package com.deleidos.analytics.redis.client.loader;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;

import org.junit.Test;

import com.deleidos.analytics.common.logging.LogUtil;
import com.deleidos.analytics.common.util.GsonFactory;
import com.deleidos.analytics.redis.client.RedisClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Test loading and querying cache data from a CSV file.
 * 
 * @author vernona
 */
public class GenericCacheLoaderCsvTest {

	private String redisHost = "ec2-54-205-46-50.compute-1.amazonaws.com";
	private String filename = "STATE_ABR.csv";
	private String namespace = "state_abbreviation";
	private String keyField = "CODE";
	private String descField = "DESCRIPTION";

	@Test
	public void testCsv() throws Exception {
		LogUtil.initializeLog4jConsoleAppender();
		String path = new File(GenericCacheLoaderCsvTest.class.getClassLoader().getResource(filename).toURI()).getAbsolutePath();
		System.out.println("path=" + path);
		GenericCacheLoader gcl = new GenericCacheLoader(redisHost, path, namespace, keyField);
		gcl.loadFile();

		Gson gson = GsonFactory.getInstance().getGson();
		RedisClient client = new RedisClient(redisHost);
		Type type = new TypeToken<Map<String, Object>>() {
		}.getType();

		Map<String, Object> map = gson.fromJson(client.getValue(namespace, "AB"), type);
		assertEquals("AB", map.get(keyField).toString());
		assertEquals("Alberta,Canada", map.get(descField).toString());
		
		map = gson.fromJson(client.getValue(namespace, "MD"), type);
		assertEquals("MD", map.get(keyField).toString());
		assertEquals("Maryland", map.get(descField).toString());
		
		map = gson.fromJson(client.getValue(namespace, "ZZ"), type);
		assertEquals("ZZ", map.get(keyField).toString());
		assertEquals("Foreign Country", map.get(descField).toString());
	}
}
