package hw1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A server that takes in a port number to create a connection with other clients.
 *
 * In order to properly use Server, a Server must be instantiated and then a call to listen
 * must be invoked to start the Server.
 */
public class Server {

	private ServerSocket serverSocket;
	private int clientNum; //keeps track of how many clients were created
	private int serverPortNumber;
	private ConcurrentHashMap<Integer, ClientHandler> clients;
	private Socket clientSocket;
	private Connection connection;
		
	Server(int serverPortNumber) {
		this.serverPortNumber = serverPortNumber;
		serverSocket = null;
		clientNum = 0;
		clients = new ConcurrentHashMap<>();
		clientSocket = null;
		connection = Connection.SERVER;
	}

	/**
	 * Listens for clients trying to connect ot his server. If a connection is established,
	 * then the clients are added to a map to keep track of the number of connected clients and
	 *  two threads are started, one to handle sending and one to handle reading
	 */
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

				Messenger messenger = new Messenger(new PrintWriter(clientSocket.getOutputStream()),
									   new BufferedReader(new InputStreamReader(clientSocket.getInputStream())),
									   connection);

                ClientHandler client = new ClientHandler(clientNum, this, messenger);
                clients.put(clientNum, client); //add clients to the map

                Thread clientThread = new Thread(client);
                clientThread.start();

            }
            catch(IOException e) {
                System.out.println("Failed to accept port: " + serverPortNumber);
                System.exit(-1); // exits program if failed to connect

            }
        }
    }


    public int getPortNumber(){
		return this.serverPortNumber;
	}

    public void close() {
	    try{
            serverSocket.close();
        }
	    catch(IOException e){
	        e.printStackTrace();
        }
    }

	/**
	 * Gets the connected clients
	 * @return a map of the connected clients
	 */
	public ConcurrentHashMap<Integer, ClientHandler> getClients(){
		return clients;
	}

	/**
	 * Starts the application
	 * @param args
	 */
	public static void main(String[] args)
	{
		Server s = new Server(1111);
		s.listen();

	}

}

/**
 * Handles the clients sending messages to the server and then broadcasts
 * those messages to all other connected clients
 */
class ClientHandler implements Runnable {

	private int num; //tracks number for identification purposes
	private Server messageServer;
	private Messenger messenger;
	
	ClientHandler( int n, Server server, Messenger messenger) {
		num = n;
		messageServer = server;
		this.messenger = messenger;
	}

	public Messenger getMessenger(){
		return messenger;
	}

	public int getClientNumber(){
		return this.num;
	}

	@Override
	public void run() {
		while(true) {

			String message = messenger.readMessage();
			messenger.broadcast(message, messageServer.getClients(), num);
		}
	}

}
