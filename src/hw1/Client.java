package hw1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {


	Socket serverSocket;
	String serverHostName = "localhost";
	int serverPortNumber = 1111;
	String name;
	PrintWriter out;
	static Scanner cmdLineScanner;
	Scanner in;

    SendMessage sendMessage;
    ReadMessage readMessage;


    Client(Scanner cmdLineScanner){
        this.cmdLineScanner = cmdLineScanner;
    }

    public void connect(){
        //1. Enter name
        System.out.print("Please enter your name and press enter: ");
        name = cmdLineScanner.nextLine();

        //2. Connect to server
        try {
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

            out = new PrintWriter(new BufferedOutputStream(serverSocket.getOutputStream()));
            in = new Scanner(new BufferedInputStream(serverSocket.getInputStream()));
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        name = "test"; //TODO: delete
        sendMessage = new SendMessage(serverSocket, cmdLineScanner, out, name);
        readMessage = new ReadMessage(serverSocket, in);

        Thread sendMessageThread = new Thread(sendMessage);
        Thread readMessageThread = new Thread(readMessage);


        sendMessageThread.start();
        readMessageThread.start();
    }

    public void close() {
        in.close();
        out.close();
        cmdLineScanner.close();

    }


	public static void main(String[] args)
	{
        Scanner in = new Scanner(System.in);
		Client c = new Client(in);
		c.connect();
		c.start();
	}	

}

class ReadMessage extends Thread {

    Socket socket;
    Scanner in;

    public ReadMessage(Socket socket, Scanner in) {
        this.socket = socket;
        this.in = in;
    }

    @Override
    public void run() {
        while (true) {

            String message = in.nextLine();
            System.out.println(message);

        }
    }
}

class SendMessage extends Thread {

    Socket socket;
    Scanner in;
    PrintWriter out;
    String name;

    public SendMessage(Socket socket, Scanner in, PrintWriter out, String name){
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.name = name;
    }

    @Override
    public void run() {
        while (true) {
            //get message to send
            System.out.println("Send a message: ");
            String message = in.nextLine();

            message = name + " has sent a message: " + message;

            //write message to server
            out.println(message);
            out.flush();
        }
    }
}
