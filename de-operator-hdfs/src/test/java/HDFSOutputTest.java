import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
public class HDFSOutputTest {

	public static void main(String[] args) throws IOException, URISyntaxException {
		Configuration configuration = new Configuration();
		
	
		
		
		configuration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
		FileSystem hdfs = FileSystem.get( new URI( "hdfs://54.234.197.186:8020"), configuration );
		Path file = new Path("/tmp/HDFSTest.txt");
		hdfs.delete( file, true ); 
		/*
		System.out.println("after if");
		OutputStream os = hdfs.create( file, true);
		BufferedWriter br = new BufferedWriter( new OutputStreamWriter( os, "UTF-8" ) );
		br.write("Hello World");
		System.out.println("wrote hello world");
		br.close();*/
		hdfs.close();
	}

}
