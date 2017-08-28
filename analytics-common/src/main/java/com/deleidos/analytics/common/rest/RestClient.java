package com.deleidos.analytics.common.rest;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import com.deleidos.analytics.common.util.JsonUtil;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

/**
 * REST web service API client using latest Apache HTTP Components library. Assumes JSON media type.
 * 
 * @author vernona
 */
public class RestClient {

	private static final Logger logger = Logger.getLogger(RestClient.class);

	private static final int defaultTimeoutMillis = 30 * 1000;

	private String baseUri;
	private RequestConfig requestConfig;

	/**
	 * Constructor.
	 * 
	 * @param baseUri
	 */
	public RestClient(String baseUri) {
		this(baseUri, defaultTimeoutMillis);
	}

	/**
	 * Constructor.
	 * 
	 * @param baseUri
	 * @param timeoutMillis
	 */
	public RestClient(String baseUri, int timeoutMillis) {
		this.baseUri = baseUri;

		requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeoutMillis)
				.setConnectTimeout(timeoutMillis).setSocketTimeout(timeoutMillis).build();
	}

	/**
	 * Execute a GET request, returning a raw response string.
	 * 
	 * @param uriPath
	 * @return
	 * @throws Exception
	 */
	public String get(String uriPath) throws Exception {
		logger.debug("get, uri=" + baseUri + uriPath);

		HttpGet request = new HttpGet(baseUri + uriPath);
		request.setConfig(requestConfig);
		String response = execute(request);
		return response;
	}

	/**
	 * Execute a GET request, returning the response object.
	 * 
	 * Stop using this - it's better to let the client control their JSON unmarshalling.
	 * 
	 * @param uriPath
	 * @param classOfT
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public <T> T getObject(String uriPath, Class<T> classOfT) throws Exception {
		String response = get(uriPath);
		T t = JsonUtil.fromJsonString(response, classOfT);
		return t;
	}

	/**
	 * Execute a POST request on a String.
	 * 
	 * @param uriPath
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String post(String uriPath, String data) throws Exception {
		logger.debug("post, uri=" + baseUri + uriPath);
		HttpPost request = new HttpPost(baseUri + uriPath);
		request.setConfig(requestConfig);

		logger.debug("post, data=" + data);
		if (data != null) {
			request.setEntity(new StringEntity(data));
		}
		return execute(request);
	}

	/**
	 * Execute a PUT request on a String.
	 * 
	 * @param uriPath
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String put(String uriPath, String data) throws Exception {
		logger.debug("put, uri=" + baseUri + uriPath);
		HttpPut request = new HttpPut(baseUri + uriPath);
		request.setConfig(requestConfig);

		logger.debug("put, data=" + data);
		if (data != null) {
			request.setEntity(new StringEntity(data));
		}
		return execute(request);
	}

	/**
	 * Execute a DELETE request.
	 * 
	 * @param uriPath
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String delete(String uriPath) throws Exception {
		logger.debug("put, uri=" + baseUri + uriPath);
		HttpDelete request = new HttpDelete(baseUri + uriPath);
		request.setConfig(requestConfig);
		return execute(request);
	}
	
	//
	// Private methods:
	//

	/**
	 * Execute the HTTP request.
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private String execute(HttpUriRequest request) throws Exception {
		String mediaType = "application/json";
		String characterSet = "UTF-8";
		request.addHeader("Accept", mediaType);
		request.addHeader("Accept-Charset", characterSet);
		request.addHeader("Content-Type", mediaType + "; charset=" + characterSet);

		HttpResponse response = HttpClientBuilder.create().build().execute(request);
		boolean parseResponse = true;
		int status = response.getStatusLine().getStatusCode();
		if (status < 200 || status > 299) {
			// Handle non-2xx responses.
			String message = request.getURI().toString() + ":" + response.getStatusLine().getStatusCode() + " "
					+ response.getStatusLine().getReasonPhrase();
			logger.error(message);
			// It's not an error per se if the object is not found on a get. Just return null in that case and let the
			// client decide what to do.
			if ((request instanceof HttpGet || request instanceof HttpDelete) && response.getStatusLine().getStatusCode() == 404) {
				parseResponse = false;
			}
			else {
				logger.debug(response.getEntity().toString());
				throw new RuntimeException(
						response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
			}
		}

		String content = null;
		if (parseResponse && response.getEntity() != null && response.getEntity().getContent() != null) {
			InputStream stream = response.getEntity().getContent();
			content = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8)); // guava
			stream.close();
		}
		logger.debug("content=" + content);
		return content;
	}
}
