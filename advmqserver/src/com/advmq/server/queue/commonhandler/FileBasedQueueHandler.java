package com.advmq.server.queue.commonhandler;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.advmq.server.queue.filereceiver.WriteClientHandler;
import com.advmq.server.queue.filesender.ReadClientHandler;

import java.io.File;
import java.io.FileOutputStream;

public class FileBasedQueueHandler {
	
	
public static void main(final String[] args) throws IOException, InterruptedException {
	ServerSocket serverSocket = new ServerSocket(8081);
	
	while (true) {
		Socket socket=null;
		try {
		    socket= serverSocket.accept();
			    
			
		    final DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		   
			
		    final DataInputStream dis = new DataInputStream(socket.getInputStream());
		    //System.out.println(dis.readChar());
		    char type = dis.readChar();
		    //if the Connection request is from PostFile.java
		    if(type=='>') {
		    	Thread t = new Thread(new WriteClientHandler(socket,dis,dos));
		    	t.start();
		    }
		  //if the Connection request is from GetFile.java
		    else if(type=='<') {
		    	System.out.println("[X] Sending the message to destination");
		    	Thread t = new Thread(new ReadClientHandler(socket,dis,dos));
		    	t.start();
		    }
		}catch (Exception e) {
			// TODO: handle exception
			socket.close();
			e.printStackTrace();
		}
	}
}
}
