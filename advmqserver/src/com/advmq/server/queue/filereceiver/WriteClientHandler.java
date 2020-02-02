package com.advmq.server.queue.filereceiver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class WriteClientHandler implements Runnable{
	Socket socket;
	DataInputStream dis; 
	DataOutputStream dos;
	
	public WriteClientHandler(Socket socket, DataInputStream dis, DataOutputStream dos)
	{
		this.socket=socket;
		this.dis=dis;
		this.dos=dos;
	}

	@Override
	public void run() {
	
		streamDistributor( socket,  dis,  dos);
		
	}
	private void streamDistributor(Socket socket, DataInputStream dis, DataOutputStream dos) {
		// TODO Auto-generated method stub

		String parentDirectoryPath = "/Users/pronoy/Documents/ClientServer/java/queue/";
		String cfgparentDirectoryPath = "/Users/pronoy/Documents/ClientServer/java/cfg/";
	    String queueName = "imageProcessorQueue";
	    String queueNameExtension = ".part.";
	    String queueStatusFileName = queueName+".queueStatus";
	    File queueStatusFile = new File(cfgparentDirectoryPath+queueStatusFileName);
	    String fileName="";
	   
	   
	    try {
			queueName = dis.readUTF();
			fileName = dis.readUTF();
			queueStatusFile = new File(cfgparentDirectoryPath+queueStatusFileName);
			System.out.println(fileName);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    int byteCount = 0;
	    int fileCount = filePartCountReader(queueStatusFile,queueName)+1;
	    //create subDirectory for queue
	    File directory = new File(parentDirectoryPath+queueName);
	    
	    if(!directory.exists())
	    	directory.mkdirs();
	    
	    File file = new File(directory+"/"+fileCount);
	    FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new  FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    //Read from client END
	    int loopCounter = 0;
	    int fileLength = 0;
		try {
			fileLength = dis.readInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	while(true){
		try{
			if (loopCounter==fileLength)
				break;
		    byte c = dis.readByte();
		    //System.out.print(c);
		    fileOutputStream.write(c);
		    if(byteCount==10240) {
		    	//Thread.sleep(1000);
		    	byteCount=0;
		    	file= new File(directory+"/"+(++fileCount));
		    	fileOutputStream.close();
		    	fileOutputStream = new  FileOutputStream(file);
		    }
		    byteCount++;
		    loopCounter++;
			}catch(EOFException ex){
				//break from the loop
				System.out.println("Exception Break");
				break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} /*
				 * catch (InterruptedException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); }
				 */

		}
		
	try {
		System.out.println(dis.readUTF());
		fileOutputStream.close();
	


	dos.close();
	
	socket.close();
	
	
	//Here true is to append the content to file
	FileWriter fw = new FileWriter(queueStatusFile,true);
    BufferedWriter queueStatusBufferedWriter = new BufferedWriter(fw);
    
	    synchronized (queueStatusBufferedWriter) {
		    StringBuffer stringBuffer = new StringBuffer();
		    
		    String delimeter = "!";
		    stringBuffer.append(">!fileName:");
		    stringBuffer.append(fileName);
		    stringBuffer.append(delimeter);
		    stringBuffer.append("queueName:");
		    stringBuffer.append(queueName);
		    stringBuffer.append(delimeter);
		    stringBuffer.append("fileSize:");
		    stringBuffer.append(fileLength);
		    stringBuffer.append(delimeter);
		    stringBuffer.append("partFileCount:");
		    stringBuffer.append(fileCount);
		    stringBuffer.append("\n");
		    queueStatusBufferedWriter.append(stringBuffer.toString());
		    queueStatusBufferedWriter.close();
		    
		}
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	
	}
	private synchronized int filePartCountReader(File file, String queueName) {
		try (BufferedReader bufferedReader=new BufferedReader(new FileReader(file))){
			String line =bufferedReader.readLine();
			
		 while( (line !=null) && line.contains(queueName))
		 {
			 
			 if(!line.endsWith(">")) {
				 String arr[] = line.split("!");
				// String fileName=arr[1].split(":")[1];
				// int fileSize=Integer.parseInt(arr[2].split(":")[1]);
				 
				 int partFileCount =Integer.parseInt(arr[4].split(":")[1]);
				 return partFileCount;
			 }
			 line=bufferedReader.readLine();
		 }
		}catch (FileNotFoundException e) {
			System.out.println("[X] No Config file found for this queue. This program will create "+queueName+".queueStatus file");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
