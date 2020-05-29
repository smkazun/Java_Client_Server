package hw1;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 *  A client that takes in the user's name and attempts to make a connection with the server,
 *  so that multiple clients may chat with each other.
 *
 *  In order to properly used the client, and Client object must be instantiated
 *  and then a call to connect must be made to establish a connection with the server.
 *  To start communications, the start method must be invoked
 */
public class Client {


	private Socket serverSocket;
	private String serverHostName;
	private int serverPortNumber;
	private String name;
	private PrintWriter out;
	private BufferedReader cmdLineReader;
	private BufferedReader in;
    private Connection connection;
    private SendHandler sendHandler;
    private ReadHandler readHandler;


    Client(BufferedReader cmdLineReader, String serverHostName, int serverPortNumber){
        this.cmdLineReader = cmdLineReader;
        serverSocket = null;
        this.serverHostName = serverHostName;
        this.serverPortNumber = serverPortNumber;
        connection = Connection.CLIENT;
        in = null;
        out = null;
        name = null;

    }

    /**
     * Connects with a server, takes in a name
     */
    public void connect(){

        try {
            //1. Enter name
            System.out.print("Please enter your name and press enter: ");
            name = cmdLineReader.readLine();

            //2. Connect to server
            serverSocket = new Socket(serverHostName, serverPortNumber);
        }
        catch(UnknownHostException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        System.out.println("Connected to the server");
    }

    /**
     * Starts the necessary streams, handlers, messengers for communication. Uses one thread to read incoming messages via a BufferedReader,
     * and another thread to send outgoing messages via a PrintWriter
     */
	public void start(){

        //setup I/O streams
        try{

            out = new PrintWriter(serverSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        Messenger sendMessenger = new Messenger(out, cmdLineReader, connection);
        Messenger readMessenger = new Messenger(out, in, connection);

        sendHandler = new SendHandler(sendMessenger, name);
        readHandler = new ReadHandler(readMessenger);

        Thread sendMessageThread = new Thread(sendHandler);
        Thread readMessageThread = new Thread(readHandler);

        sendMessageThread.start();
        readMessageThread.start();
    }

    /**
     * Closes the streams associated with the client
     */
    public void close() {
        try{
            in.close();
            cmdLineReader.close();
            serverSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        out.close();
    }

    /**
     * A variety of getters. Mostly used for ease of testing
     */
    public BufferedReader getClientInputStream(){
        return this.in;
    }

    public PrintWriter getClientOutputStream(){
        return this.out;
    }

    public ReadHandler getReadHandler(){
        return this.readHandler;
    }

    public SendHandler getSendHandler(){
        return this.sendHandler;
    }

    public String getUserName(){
        return this.name;
    }

    public int getPortNumber(){
        return this.serverPortNumber;
    }



    /**
     * Starts the application
     * @param args
     */
	public static void main(String[] args)
	{
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		Client c = new Client(in, "localhost", 1111);
		c.connect();
		c.start();
	}	

}

/**
 * This class helps the clients read messages coming in from the server
 */
class ReadHandler extends Thread {

    private Messenger messenger;

    public ReadHandler(Messenger messenger) {
        this.messenger = messenger;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println(messenger.readMessage());
        }
    }
}

/**
 * This class helps the client send messages to other clients via the server.
 */
class SendHandler extends Thread {

    String name;
    Messenger messenger;

    public SendHandler(Messenger messenger, String name){
        this.name = name;
        this.messenger = messenger;
    }


    @Override
    public void run() {
        while (true) {
            String message = messenger.createMessage(name);
            messenger.sendMessage(message);
        }
    }
}
