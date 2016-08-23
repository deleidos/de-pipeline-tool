package com.deleidos.framework.operators.s3;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.datatorrent.api.Context.OperatorContext;
import com.datatorrent.api.DefaultOutputPort;
import com.deleidos.framework.operators.abstractsplitter.AbstractSplitter;
import com.deleidos.framework.operators.common.InputTuple;

public class S3InputOperator extends AbstractSplitter implements Runnable {

	// @NotNull
	protected String bucketName;
	protected String path;
	// @NotNull
	protected String accessKey;
	// @NotNull
	protected String secretKey;
	protected String endPoint;
	private String splitter;
	private transient AmazonS3 s3Client = null;
	private transient boolean shutdown = false;
	private transient Thread s3Thread;
	private transient AWSCredentials credentials;
	private Double headerRows = 0.0;
	public transient DefaultOutputPort<InputTuple> output = new DefaultOutputPort<InputTuple>();

	public void setSplitter(String splitter) {
		this.splitter = splitter;
	}

	public String getSplitter() {
		return this.splitter;
	}

	public void setHeaderRows(Double headerRows) {
		this.headerRows = headerRows;
	}

	public Double getHeaderRows() {
		return this.headerRows;
	}

	@Override
	public void setup(OperatorContext context) {

		try {
			credentials = new BasicAWSCredentials(accessKey, secretKey);
			s3Client = new AmazonS3Client(credentials);
			s3Thread = new Thread(this);
			s3Thread.start();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void teardown() {
		shutdown = true;
		try {
			if (s3Thread != null) {
				s3Thread.join();
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		super.teardown();
	}

	public void run() {
		while (!shutdown) {
			shutdown = true;
			InputStream is = null;
			TarArchiveInputStream tarIn = null;
			try {

				List<String> files = getFiles(bucketName, path);

				for (String file : files) {
					is = getFileStream(bucketName, file);

					// only tgz for now
					if (file.endsWith(".tgz") || file.endsWith("tar.gz")) {
						BufferedInputStream in = new BufferedInputStream(is);
						GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
						tarIn = new TarArchiveInputStream(gzIn);

						ArchiveEntry entry = null;

						try {
							while ((entry = tarIn.getNextEntry()) != null) {
								if (!entry.isDirectory()) {

									emitLines(tarIn);
								}
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (gzIn != null) {
								gzIn.close();
							}
							if (in != null) {
								in.close();
							}
						}

					} else if (file.endsWith("json") || file.endsWith("csv")) {
						emitLines(is);
					}
				}

			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (tarIn != null) {
					try {
						tarIn.close();
					} catch (IOException e) {
					}
				} else if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}

			}
		}
	}

	protected List<String> getFiles(String bucketName, String path) {
		List<String> files = new ArrayList<String>();
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);
		if (path != null && !path.isEmpty()) {
			if (!path.endsWith("/")) {
				path += "/";
			}
			listObjectsRequest.withPrefix(path);
		}
		ObjectListing objectListing;
		do {
			objectListing = s3Client.listObjects(listObjectsRequest);
			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				if (!objectSummary.getKey().endsWith("/")) {
					files.add(objectSummary.getKey());
				}
			}
			listObjectsRequest.setMarker(objectListing.getNextMarker());
		} while (objectListing.isTruncated());
		return files;
	}

	protected InputStream getFileStream(String bucketName, String key) {
		return s3Client.getObject(bucketName, key).getObjectContent();
	}

	protected void emitLines(InputStream is) throws IOException, InterruptedException {

		if (splitter.equals("JSON")) {
			JSONSplitter(is, output, headerRows.intValue());
		} else if (splitter.equals("Line")) {
			LineSplitter(is, output, headerRows.intValue());
		}
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

}
