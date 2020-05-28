package hw1;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {


	Socket serverSocket;
	String serverHostName = "localhost";
	int serverPortNumber = 1111;
	String name;
	PrintWriter out;
	BufferedReader cmdLineReader;
	BufferedReader in;

    SendMessage sendMessage;
    ReadMessage readMessage;


    Client(BufferedReader cmdLineReader){
        this.cmdLineReader = cmdLineReader;

    }

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

    public void close() {
        try{
            in.close();
            cmdLineReader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        out.close();


    }


	public static void main(String[] args)
	{
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		Client c = new Client(in);
		c.connect();
		c.start();
	}	

}

class ReadMessage extends Thread {

    Socket socket;
    BufferedReader in;

    public ReadMessage(Socket socket, BufferedReader in) {
        this.socket = socket;
        this.in = in;
    }

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
