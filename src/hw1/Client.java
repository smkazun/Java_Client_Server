package hw1;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 *  A client that takes in the user's name and attempts to make a connection with the server,
 *  so that multiple clients may chat with each other.
 */
public class Client {


	Socket serverSocket;
	String serverHostName;
	int serverPortNumber;
	String name;
	PrintWriter out;
	BufferedReader cmdLineReader;
	BufferedReader in;

    SendMessage sendMessage;
    ReadMessage readMessage;


    Client(BufferedReader cmdLineReader, String serverHostName, int serverPortNumber){
        this.cmdLineReader = cmdLineReader;
        serverSocket = null;
        this.serverHostName = serverHostName;
        this.serverPortNumber = serverPortNumber;

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
     * Starts the necessary streams for communication. Uses one thread to read incoming messages via a BufferedReader,
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

        sendMessage = new SendMessage(serverSocket, cmdLineReader, out, name);
        readMessage = new ReadMessage(serverSocket, in);

        Thread sendMessageThread = new Thread(sendMessage);
        Thread readMessageThread = new Thread(readMessage);


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
class ReadMessage extends Thread {

    Socket socket;
    BufferedReader in;

    public ReadMessage(Socket socket, BufferedReader in) {
        this.socket = socket;
        this.in = in;
    }

    /**
     * Reads incoming messages from the server
     * @return the message from the server
     */
    public String readMessageFromServer(){

        String message = "";
        try {
            message = in.readLine();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return message;
    }

    /**
     * Prints the messages from the server
     */
    public void printReadMessage()
    {
        String message = readMessageFromServer();
        System.out.println(message);
    }

    @Override
    public void run() {
        while (true) {
            printReadMessage();
        }
    }
}

/**
 * This class helps the client send messages to other clients via the server.
 */
class SendMessage extends Thread {

    Socket socket;
    BufferedReader in;
    PrintWriter out;
    String name;

    public SendMessage(Socket socket, BufferedReader in, PrintWriter out, String name){
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.name = name;
    }

    /**
     * Creates a user made message
     * @return the message to send to the server
     */
    public String createMessage(){

        String message = "";
        System.out.println("Send a message: ");

        try {
            message = in.readLine();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        message = name + " has sent a message: " + message;

        return message;

    }

    /**
     * Sends a user created message to the server
     */
    public void sendMessageToServer(){
        String message = createMessage();
        out.println(message);
        out.flush();
    }

    @Override
    public void run() {
        while (true) {
            sendMessageToServer();
        }
    }
}
