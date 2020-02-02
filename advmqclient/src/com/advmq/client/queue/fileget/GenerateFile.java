package com.advmq.client.queue.fileget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class GenerateFile {

	public void generateFile(String outputfile,String partFiles,int startPartFile, int endPartFile) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("method called");
		System.out.println(outputfile +" "+partFiles +" " +startPartFile +" "+endPartFile);
		int count = startPartFile;
		
		FileInputStream fileInputStream=null;
		File generatedFile = new File(outputfile);
		FileOutputStream fileOutputStream = new FileOutputStream(generatedFile);
		byte[] byteArray=null;
		File file=null;
		
		while (count <= endPartFile) {
			
		file = new File(partFiles+count);
		
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
