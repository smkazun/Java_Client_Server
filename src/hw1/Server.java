package hw1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {

		// TODO Auto-generated method stub
		ServerSocket serverSocket = null;
		int clientNum = 0;
		int serverPortNumber = 1111;
		ArrayList<ClientHandler> clients = new ArrayList<>();
		
		public static void main(String[] args)
		{
			Server server = new Server();
		}
		
		Server()
		{
			createServerSocket();
			loop();	
		};
		
		/**
		 * Creates a server socket
		 */
		private void createServerSocket() {	
			try {
				
				serverSocket = new ServerSocket(serverPortNumber); //opens socket at port 1111
				System.out.println(serverSocket);
			}
			catch(IOException e)
			{
				System.out.println("Could not listen for port: " + serverPortNumber); //socket cant connect
				System.exit(-1);
			}	
		}
		
		private void loop()
		{	
			while(true) {
				Socket clientSocket = null;
				
				try {
					//waits for client to connect
					System.out.println("Waiting for client " + (++clientNum) + " to connect");
					clientSocket = serverSocket.accept();
					
					//creates thread to handle client request
					System.out.println("Server connected to client " + clientNum);
					
					ClientHandler client = new ClientHandler(clientSocket, clientNum, this);
					clients.add(client); //add clients to the list
					
					Thread clientThread = new Thread(client);
					clientThread.start();
					
				}
				catch(IOException e) {
					System.out.println("Failed to accept port: " + serverPortNumber);
					System.exit(-1); // exits program if failed to connect
					
				}							
			}		
		}
		
		/**
		 * Goes through the list of clients and sends message to all other clients
		 * @param name
		 * @param message
		 * @param clientNum
		 */
		public synchronized void sendMessageToClients(String message, int clientNum) 
		{
			for(ClientHandler client : clients)
			{
				if(client.num != clientNum)
				{
					client.sendMessageToClient(message);
				}
			}	
		}
		

} //end class Server


class ClientHandler implements Runnable {
	
	Socket serverSocket; //socket on server side that connects to the client
	int num; //track number for identification purposes
	String name;
	Scanner in = null;
	PrintWriter out = null;
	Server messageServer;
	
	ClientHandler(Socket s, int n, Server server) {
		serverSocket = s;
		num = n;
		messageServer = server;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			//read and print what client sent
			in = new Scanner(new BufferedInputStream(serverSocket.getInputStream()));
			out = new PrintWriter(new BufferedOutputStream(serverSocket.getOutputStream()));
			
			in.nextLine();
			String message = in.nextLine();
			System.out.println(message);
			out.println(message);
			messageServer.sendMessageToClients(message, num);
			
			

			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
	public void sendMessageToClient(String message) {
		out.println(message);
		out.flush();
	}
	
	
} //end class ClientHandler
