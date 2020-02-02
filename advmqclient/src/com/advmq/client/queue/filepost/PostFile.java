package com.advmq.client.queue.filepost;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class PostFile {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String address = "192.168.0.12";
		int port = 8081;
		String directoryPath="/Users/pronoyde/Desktop/";
		String fileName="1.jpeg";
		
		Socket socket = new Socket(address, port);
		File file = new File(directoryPath+fileName);

		byte[] byteArray= null;
		System.out.println(file.length());
		byteArray= new byte[(int) file.length()];
		FileInputStream fileInputStream = new FileInputStream(file);
		fileInputStream.read(byteArray);
		
		DataOutputStream dataOutputStream = new  DataOutputStream(socket.getOutputStream());
		dataOutputStream.writeChar('>');
		
		dataOutputStream.writeUTF("imageProcessorQueueTest");
		dataOutputStream.writeUTF(fileName);
		
		dataOutputStream.writeInt((int)file.length());
		
		dataOutputStream.write(byteArray);
		dataOutputStream.writeUTF("End of file");
		fileInputStream.close();
		dataOutputStream.close();
		socket.close();
	}

}
