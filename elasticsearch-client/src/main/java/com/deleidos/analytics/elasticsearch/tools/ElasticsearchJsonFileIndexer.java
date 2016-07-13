package com.deleidos.analytics.elasticsearch.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.deleidos.analytics.common.util.FileUtil;
import com.deleidos.analytics.elasticsearch.client.ElasticsearchClient;
import com.deleidos.analytics.elasticsearch.client.ElasticsearchData;
import com.deleidos.analytics.elasticsearch.client.config.ElasticsearchClientConfig;

/**
 * Index JSON files in the file system.
 * 
 * @author vernona
 */
public class ElasticsearchJsonFileIndexer {

	private String indexName;
	private String typeName;
	private ElasticsearchClientConfig config;

	/**
	 * Constructor.
	 * 
	 * @param config
	 * @param indexName
	 * @param typeName
	 */
	public ElasticsearchJsonFileIndexer(ElasticsearchClientConfig config, String indexName, String typeName) {
		this.config = config;
		this.indexName = indexName;
		this.typeName = typeName;
	}

	/**
	 * Index JSON files in elasticsearch. Each file must contain a single JSON object. The ID of the file's content will
	 * be the file name without the extension.
	 * 
	 * @param baseDirectoryPath
	 * @param jsonFileExtension
	 * @param recursive
	 */
	public void indexFiles(String baseDirectoryPath, String jsonFileExtension, boolean recursive) throws Exception {
		List<ElasticsearchData> esData = new ArrayList<ElasticsearchData>();
		Collection<File> files = FileUtil.getFilesByExtension(baseDirectoryPath, jsonFileExtension, true);
		for (File file : files) {
			ElasticsearchData data = new ElasticsearchData(indexName, typeName,
					FileUtil.getFileNameWithoutExtension(file), FileUtil.getFileContentsAsString(file));
			esData.add(data);
		}

		ElasticsearchClient client = new ElasticsearchClient(config);
		client.indexData(esData);
	}

	/**
	 * Index JSON objects from a file. Each line in the file must contain a single JSON object. Each object will be
	 * given a new random UUID.
	 * 
	 * Uses a buffered reader, so it can handle large files.
	 * 
	 * @param filePath
	 * @throws Exception
	 */
	public void indexFile(String filePath) throws Exception {
		indexFile(filePath, null);
	}

	/**
	 * Index JSON objects from a file. Each line in the file must contain a single JSON object. Each object will be
	 * given a new random UUID.
	 * 
	 * Uses a buffered reader, so it can handle large files.
	 * 
	 * @param filePath
	 * @param numLines
	 *            the number of lines in the file to index
	 * @throws Exception
	 */
	public void indexFile(String filePath, Integer numLines) throws Exception {
		final int indexBatchSize = 100;
		ElasticsearchClient client = new ElasticsearchClient(config);

		int lineCount = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
			List<ElasticsearchData> esData = new ArrayList<ElasticsearchData>();
			for (String line; (line = br.readLine()) != null;) {
				ElasticsearchData data = new ElasticsearchData(indexName, typeName, UUID.randomUUID().toString(), line);
				esData.add(data);

				// Index a batch every N records.
				if (esData.size() == indexBatchSize) {
					client.indexData(esData);
					esData.clear();
				}

				lineCount++;
				if (numLines != null && lineCount >= numLines) {
					break;
				}
			}

			// Index the leftovers.
			client.indexData(esData);
		}

	}
}