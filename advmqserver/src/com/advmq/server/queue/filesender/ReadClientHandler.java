package com.advmq.server.queue.filesender;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;

public class ReadClientHandler implements Runnable{
	
	Socket socket;
	DataInputStream dis; 
	DataOutputStream dos;
	
	public ReadClientHandler(Socket socket, DataInputStream dis, DataOutputStream dos)
	{
		this.socket=socket;
		this.dis=dis;
		this.dos=dos;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		partFileSender(socket, dis, dos);
		
	}

	private void partFileSender(Socket socket, DataInputStream dis, DataOutputStream dos) {
		// TODO Auto-generated method stub
		String directoryPath = "/Users/pronoy/Documents/ClientServer/java/queue/";
		String cfgDirectoryPath = "/Users/pronoy/Documents/ClientServer/java/cfg/";
	    String queueName = "imageProcessorQueue";
	    String queueNameExtension = ".part";
	    String queueStatusFileName = queueName+".queueStatus";
	    File queueStatusFile = new File(cfgDirectoryPath+queueStatusFileName);
	    String fileName="";
	   
	    
	    //test stub
	    
	    try {
	    	//Get the queue name from client
			queueName = dis.readUTF();
			//send queue status to client
			dos.writeUTF(filePartReader(queueStatusFile,queueName));
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		int count =0;
		FileInputStream fileInputStream = null;
		boolean shouldLoop = true;
	    while(shouldLoop) {
		    File file = new File(directoryPath+queueName+"/"+(++count));
		    byte[] arr = new byte[(int)file.length()];
		   
		    
				try {
					fileInputStream = new FileInputStream(file);
					fileInputStream.read(arr);
					dos.write(arr);
					System.out.println("Write complete");
					shouldLoop = dis.readBoolean();
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
				
		    }
		try {
			dos.close();
			fileInputStream.close();
			//socket.close();
		}
		catch(IOException e)
		{
			System.out.println("Not able to close the data/File output stream");
		}
	    
	}
	private synchronized String filePartReader(File file, String queueName) {
		try (BufferedReader bufferedReader=new BufferedReader(new FileReader(file))){
			String line =bufferedReader.readLine();
			
			String queueFileList = "";
			
		 while( (line !=null) && line.contains(queueName+"!"))
		 {
			 
			 if(!line.endsWith(">")) {
					
				 queueFileList+=line+"\n";
			 }
			 
			 line=bufferedReader.readLine();
		 }
		 	return queueFileList;
		 	
		}catch (FileNotFoundException e) {
			System.out.println("[X] No Config file found for this queue. This program will create "+queueName+".queueStatus file");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static File[] listAllPartFiles() {
		String directoryPath = "/Users/pronoy/Documents/ClientServer/java/queue/imageProcessorQueueTest/";
		File file = new File(directoryPath);
		
		return file.listFiles();
	}
	
	public static void main(String[] args) {
		File [] files = listAllPartFiles();
		ArrayList<String> queueFiles = new ArrayList<String>();
		
		for(File fileName:files) {
			if(!fileName.getName().contains("."))
				queueFiles.add(fileName.getName());
		}
		queueFiles.sort(Comparator.comparingInt(Integer::parseInt));
		System.out.println(queueFiles);
	}

}
