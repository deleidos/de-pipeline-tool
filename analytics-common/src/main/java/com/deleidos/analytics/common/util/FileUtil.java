package com.deleidos.analytics.common.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 * File utilities.
 * 
 * @author vernona
 */
public class FileUtil {

	/**
	 * Get all files under a base directory with a particular extension. If recursive is true, sub-directories will be
	 * searched.
	 * 
	 * @param baseDir
	 * @param extension
	 * @param recursive
	 * @return
	 */
	public static Collection<File> getFilesByExtension(String baseDir, String extension, boolean recursive) {
		return FileUtils.listFiles(new File(baseDir), new SuffixFileFilter(extension),
				recursive ? TrueFileFilter.INSTANCE : null);
	}

	/**
	 * Get the contents of a file as a string.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String getFileContentsAsString(File file) throws Exception {
		return FileUtils.readFileToString(file);
	}

	/**
	 * Get the contents of a file as a byte[].
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] getFileContentsAsBytes(File file) throws Exception {
		return FileUtils.readFileToByteArray(file);
	}

	/**
	 * Get the contents of a file in resources as a string.
	 * 
	 * @param resourcePath
	 * @return
	 * @throws IOException
	 */
	public static String getResourceFileContentsAsString(String resourcePath) throws Exception {
		return IOUtils.toString(FileUtil.class.getClassLoader().getResourceAsStream(resourcePath));
	}

	/**
	 * Get the contents of a file in resources as a byte[].
	 * 
	 * @param resourcePath
	 * @return
	 * @throws IOException
	 */
	public static byte[] getResourceFileContentsAsBytes(String resourcePath) throws Exception {
		return IOUtils.toByteArray(FileUtil.class.getClassLoader().getResourceAsStream(resourcePath));
	}

	/**
	 * Get the absolute path to a resources file.
	 * 
	 * @param resourceFilename
	 * @return
	 * @throws Exception
	 */
	public static String getResourceFileAbsolutePath(String resourceFilename) throws Exception {
		URL resource = FileUtil.class.getClassLoader().getResource(resourceFilename);
		if (resource == null) {
			throw new FileNotFoundException(resourceFilename);
		}

		File file = Paths.get(resource.toURI()).toFile();
		return file.getAbsolutePath();
	}

	/**
	 * Get the file name without the extension. Everything after the first dot is excluded.
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileNameWithoutExtension(File file) {
		return file.getName().substring(0, file.getName().indexOf("."));
	}

	/**
	 * Write a byte array to a file.
	 * 
	 * @param file
	 * @param bytes
	 * @throws IOException
	 */
	public static void writeByteArrayToFile(File file, byte[] bytes) throws Exception {
		FileUtils.writeByteArrayToFile(file, bytes);
	}

	/**
	 * Append a String to a file.
	 * 
	 * @param file
	 * @param data
	 * @param append
	 * @throws Exception
	 */
	public static void writeStringToFile(File file, String data, boolean append) throws Exception {
		FileUtils.writeStringToFile(file, data, true);
	}

	/**
	 * Write bytes to a tmp file.
	 * 
	 * Don't use this - it simply does not work on Windows. The file does not get created. I left it here as a reminder.
	 * 
	 * @param filename
	 * @param bytes
	 */
	@Deprecated
	public static File writeTempFile(String filename, byte[] bytes) throws Exception {
		File file = null;
		FileOutputStream stream = null;
		try {
			file = File.createTempFile(FilenameUtils.getBaseName(filename), "." + FilenameUtils.getExtension(filename));
			stream = new FileOutputStream(file);
			stream.write(bytes);
		}
		finally {
			if (stream != null) {
				stream.close();
			}
			if (file != null) {
				file.delete();
			}
		}
		return file;
	}
}
