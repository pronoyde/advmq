package com.advmq.client.queue.fileget;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class GetFile {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		String address = "192.168.0.12";
		int port = 8081;
		String directoryPath = "/home/pronoyde/Desktop/receivedFiles/";
		String fileName = "0.jpeg";
		
		Socket socket = new Socket(address,port);
		
		
		DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
		//Character indicating this is a get() request and FileBasedQueueHandler should route this to ReadClientHandler
		dataOutputStream.writeChar('<');
		//Pass the queue name
		dataOutputStream.writeUTF("imageProcessorQueueTest");
		
		/*
		 * dataOutputStream.close(); socket.close(); //open another socket as the
		 * previous one must be closed socket = new Socket(address,port);
		 */
		DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
		
		//System.out.println(dataInputStream.readUTF());
		//Read the current queue status 
		
		String queueStatus = dataInputStream.readUTF(); 
		//get the part file count for the files one by one
		
		String[] queueStatusArray = queueStatus.split("\n");
		int fileCount = queueStatusArray.length;
		int tempFileCount=0;
		int partFileCount=0;
		fileName = queueStatusArray[tempFileCount].split("!")[1].split(":")[1];
		
		File file=null;
		FileOutputStream fileOutputStream=null;
		int count =0;
		int byteCount=0;
		System.out.println(fileCount);
		file = new File(directoryPath+(++partFileCount));
		fileOutputStream = new FileOutputStream(file);
		int startPartFile=1;
		int endPartFile=1;
		while(tempFileCount<fileCount) {
			try {
				
				if(count== 10240|| byteCount==Integer.parseInt(queueStatusArray[tempFileCount].split("!")[3].split(":")[1]) ) 
				{
					
					count=0;
					
					dataOutputStream.writeBoolean(true);
					file = new File(directoryPath+(++partFileCount));
					fileOutputStream = new FileOutputStream(file);
					
					if(byteCount==Integer.parseInt(queueStatusArray[tempFileCount].split("!")[3].split(":")[1]) ) { 
						
						byteCount=0;
						
						String outputFile = "/home/pronoyde/Desktop/"+queueStatusArray[tempFileCount++].split("!")[1].split(":")[1]; 
						String partFiles = "/home/pronoyde/Desktop/receivedFiles/";
						System.out.println("here");
					
						new GenerateFIle().generateFile(outputFile, partFiles, startPartFile, endPartFile);
							  System.out.println("after"); 
							  startPartFile=endPartFile+1;
							  
					}
					endPartFile++;
				}
				
				byte c = dataInputStream.readByte();
				fileOutputStream.write(c);
				count++;
				byteCount++;
				
			}catch(EOFException ex)
			{
				System.out.println("[X] End of file reached...Exiting...!");
				//All parts received now prepare the file
				break;
				
				//partFileCount++;
				
				//break;
				
			}
			
		}
		
		fileOutputStream.close();
		dataInputStream.close();
		dataOutputStream.close();
		System.out.println();
		
	}

}
