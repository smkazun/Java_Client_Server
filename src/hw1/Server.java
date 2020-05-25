package hw1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Server {


	ServerSocket serverSocket = null;
	int clientNum = 0; //keeps track of how many clients were created
	int serverPortNumber = 1111;
	ConcurrentHashMap<Integer, ClientHandler> clients = new ConcurrentHashMap<>();

		
	Server()
	{

		//Create a new server socket
		try {

			serverSocket = new ServerSocket(serverPortNumber); //opens socket at port 1111
			System.out.println(serverSocket);
		}
		catch(IOException e)
		{
			System.out.println("Could not listen on port: " + serverPortNumber); //socket cant connect
			System.exit(-1);
		}

		//loop forever, server is always providing a service
		while(true) {
			Socket clientSocket = null;

			try {
				//waits for client to connect
				System.out.println("Waiting for client " + (++clientNum) + " to connect");
				clientSocket = serverSocket.accept();

				//creates thread to handle client request
				System.out.println("Server connected to client " + clientNum);

				ClientHandler client = new ClientHandler(clientSocket, clientNum, this);
				clients.put(clientNum, client); //add clients to the table

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
	 * Iterate through the list of clients and sends message to all other clients
	 * @param
	 * @param message
	 * @param clientNum
	 */
	public synchronized void broadcastToClients(String message, int clientNum)
	{
		for(ConcurrentHashMap.Entry<Integer, ClientHandler> client : clients.entrySet())
		{
			if(client.getKey() != clientNum)
			{
				client.getValue().sendMessageToClient(message);
			}
		}
	}


	public static void main(String[] args)
	{
		Server s = new Server();

	}

} //end class Server


class ClientHandler implements Runnable {
	
	Socket socket; //socket on server side that connects to the client
	int num; //tracks number for identification purposes
	Scanner in;
	PrintWriter out;
	Server messageServer;
	
	ClientHandler(Socket s, int n, Server server) {
		socket = s;
		num = n;
		messageServer = server;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				//get socket I/O streams
				in = new Scanner(new BufferedInputStream(socket.getInputStream()));
				out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));

				//read client message
				String message = in.nextLine();
				System.out.println(message); //prints message in server console

				//exit chat
				if(message.equals("exit")){
					this.socket.close();
					break;
				}

				messageServer.broadcastToClients(message, num); //prints message in all other clients consoles

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//close resources
		this.in.close();
		this.out.close();

	}
	public void sendMessageToClient(String message) {
		out.println(message);
		out.flush();
	}
	
	
} //end class ClientHandler
