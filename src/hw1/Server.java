package hw1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Server {


	ServerSocket serverSocket;
	int clientNum; //keeps track of how many clients were created
	int serverPortNumber;
	ConcurrentHashMap<Integer, ClientHandler> clients;
	Socket clientSocket;
		
	Server(int serverPortNumber) {
		this.serverPortNumber = serverPortNumber;
		serverSocket = null;
		clientNum = 0;
		clients = new ConcurrentHashMap<>();
		clientSocket = null;
	}


	public void listen()
    {
    	//create socket
		try {

			serverSocket = new ServerSocket(serverPortNumber); //opens socket at port 1111
			System.out.println(serverSocket);
		}
		catch(Exception e)
		{
			System.out.println("Could not listen on port: " + serverPortNumber); //socket cant connect
			System.exit(-1);
		}

        //loop forever, server is always providing a service
        while(true) {

            try {
                //waits for client to connect
                System.out.println("Waiting for client " + (++clientNum) + " to connect");
                clientSocket = serverSocket.accept();

                //creates thread to handle client request
                System.out.println("Server connected to client " + clientNum);

                //create streams
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream());

                ClientHandler client = new ClientHandler(clientNum, this, out, in);
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

    public void close() {
	    try{
            serverSocket.close();
        }
	    catch(IOException e){
	        e.printStackTrace();
        }
    }



	public static void main(String[] args)
	{
		Server s = new Server(1111);
		s.listen();

	}

} //end class Server


class ClientHandler implements Runnable {

	int num; //tracks number for identification purposes
	BufferedReader in;
	PrintWriter out;
	Server messageServer;
	
	ClientHandler( int n, Server server, PrintWriter out, BufferedReader in) {
		num = n;
		messageServer = server;
		this.out = out;
		this.in = in;
	}

	
	@Override
	public void run() {
		while(true) {

			String message = readMessageFromClient();
			broadcastToClients(message, messageServer.clients);
		}
	}

	public String readMessageFromClient()
	{
		String message = "";
		try {

			message = in.readLine();
			System.out.println(message);
		}
		catch (IOException e){
			e.printStackTrace();
		}

		return message;
	}

	public void sendMessageToClient(String message) {
		out.println(message);
		out.flush();
	}

	/**
	 * Iterate through the list of clients and sends message to all other clients
	 * @param
	 * @param message
	 *
	 */
	public synchronized void broadcastToClients(String message, ConcurrentHashMap<Integer, ClientHandler> clients)
	{
		for(ConcurrentHashMap.Entry<Integer, ClientHandler> client : clients.entrySet())
		{
			if(client.getKey() != num)
			{
				client.getValue().sendMessageToClient(message);
			}
		}
	}
	
	
} //end class ClientHandler
