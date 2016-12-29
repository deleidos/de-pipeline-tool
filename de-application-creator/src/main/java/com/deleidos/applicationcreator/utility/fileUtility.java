package com.deleidos.applicationcreator.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class fileUtility
{	
	private static final fileUtility instance = new fileUtility();
	public static fileUtility getInstance()
	{
		return instance;
	}
	public void savefile(InputStream uploadedStream, String filePath)
	{
		try {
		      OutputStream out = new FileOutputStream(new File(
		              filePath));
		      int read = 0;
		      byte[] bytes = new byte[1024];
		      out = new FileOutputStream(new File(filePath));
		      while ((read = uploadedStream.read(bytes)) != -1) {
		          out.write(bytes, 0, read);
		      }
		      out.flush();
		      out.close();
		  } catch (IOException e) {
		      e.printStackTrace();
		  }
	}
	//Get Individual File
	public void getFile(File dir, List<File> fileList)
	{
		File[] files = dir.listFiles();
		for (File file : files) {
			fileList.add(file);
			if (file.isDirectory()) {
				getFile(file, fileList);
			}
		}
	}
	
	//Get File's
	public void getFiles(File dir, List<File> fileList) {

		File[] files = dir.listFiles();
		for (File file : files) {
			fileList.add(file);
			if (file.isDirectory()) {
				getFiles(file, fileList);
			}
		}
	}
	
	//Delete File/Directory
	public void delete(File dir) {
		if (dir.isDirectory()) {
			if (dir.list().length == 0) {
				dir.delete();
			} else {
				String files[] = dir.list();
				for (String file : files) {
					File fDel = new File(dir, file);
					delete(fDel);
				}
				if (dir.list().length == 0) {
					dir.delete();
				}
			}
		} else {
			dir.delete();
		}
	}

	
	public void updateJarFile(File srcJarFile, String targetPackage, File filesToAdd) throws IOException {
		File tmpJarFile = File.createTempFile("tempJar", ".tmp");
		JarFile jarFile = new JarFile(srcJarFile);
		boolean jarUpdated = false;

		try {
			JarOutputStream tempJarOutputStream = new JarOutputStream(new FileOutputStream(tmpJarFile));

			try {
				// Added the new files to the jar.

				File file = filesToAdd;
				FileInputStream fis = new FileInputStream(file);
				try {
					byte[] buffer = new byte[1024];
					int bytesRead = 0;
					JarEntry entry = new JarEntry(targetPackage + File.separator + file.getName());
					tempJarOutputStream.putNextEntry(entry);
					while ((bytesRead = fis.read(buffer)) != -1) {
						tempJarOutputStream.write(buffer, 0, bytesRead);
					}

				} finally {
					fis.close();
				}

				// Copy original jar file to the temporary one.
				Enumeration<JarEntry> jarEntries = jarFile.entries();
				while (jarEntries.hasMoreElements()) {
					JarEntry entry = jarEntries.nextElement();
					InputStream entryInputStream = jarFile.getInputStream(entry);
					tempJarOutputStream.putNextEntry(entry);
					byte[] buffer = new byte[1024];
					int bytesRead = 0;
					while ((bytesRead = entryInputStream.read(buffer)) != -1) {
						tempJarOutputStream.write(buffer, 0, bytesRead);
					}
				}

				jarUpdated = true;
			} catch (Exception ex) {
				ex.printStackTrace();
				tempJarOutputStream.putNextEntry(new JarEntry("stub"));
			} finally {
				tempJarOutputStream.close();
			}
		} finally {
			jarFile.close();

			if (!jarUpdated) {
				tmpJarFile.delete();
			}
		}

		if (jarUpdated) {
			srcJarFile.delete();
			tmpJarFile.renameTo(srcJarFile);
		}
	}
	public void writeFile(File directory, List<File> fileList) throws IOException {
		FileOutputStream fos = new FileOutputStream("/opt/apex-deployment/" + directory.getName() + ".apa");
		ZipOutputStream zos = new ZipOutputStream(fos);

		for (File file : fileList) {
			if (!file.isDirectory()) {
				addZip(directory, file, zos);
			}
		}

		zos.close();
		fos.close();
	}
	public void addZip(File dir, File file, ZipOutputStream zos) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		String zipPath = file.getCanonicalPath().substring(dir.getCanonicalPath().length() + 1,
				file.getCanonicalPath().length());
		ZipEntry zipE = new ZipEntry(zipPath);
		zos.putNextEntry(zipE);

		byte[] bytes = new byte[1024];
		int len;
		while ((len = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, len);
		}
		zos.closeEntry();
		fis.close();
	}

}
