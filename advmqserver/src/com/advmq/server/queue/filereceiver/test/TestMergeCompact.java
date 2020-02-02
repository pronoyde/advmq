package com.advmq.server.queue.filereceiver.test;
	
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestMergeCompact {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int count = 36;

		FileInputStream fileInputStream=null;
		File outputFile = new File("/Users/pronoy/Documents/ClientServer/java/queue/imageProcessorQueue.jpeg");
		FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
		byte[] byteArray=null;
		File file=null;
		
		while (count <=37) {
		file = new File("/Users/pronoy/Documents/ClientServer/java/queue/imageProcessorQueueTest/"+count);
		
		byteArray = new byte[(int) file.length()];
		
		fileInputStream = new FileInputStream(file);
		fileInputStream.read(byteArray);
		
		fileOutputStream.write(byteArray);
		
		
		
		
		count++;
		}
		fileInputStream.close();
		fileOutputStream.close();
	}

}
