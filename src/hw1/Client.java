package hw1;

import java.io.BufferedInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {


	Socket serverSocket = null;
	String serverHostName = "localhost";
	int serverPortNumber = 1111;
	ServerListener sListener;
	String name;
	PrintWriter out;
	static Scanner cmdLineScanner;
	//Scanner in;


	Client() {

		//1. Enter name
		cmdLineScanner = new Scanner(System.in);
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

		//3. create listener for server. This will keep running when a message is received
		sListener = new ServerListener(this, serverSocket);
		new Thread(sListener).start();

		//send data to server
		/*
		try {
			out = new PrintWriter(new BufferedOutputStream(serverSocket.getOutputStream()));
			out.println(name); //sends name
			out.flush();
		}
		catch(IOException e) {
			e.printStackTrace();
		}*/
		try{

			out = new PrintWriter(new BufferedOutputStream(serverSocket.getOutputStream()));
		}
		catch(IOException e) {
			e.printStackTrace();
		}

		//send messages forever
		while(true)
		{
			messagePrompt();
		}


	}

	/**
	 * Prompts the user to send a message to server
	 */
	public void messagePrompt() {

		//try{

			//Client writes a message to send to server and other clients
			System.out.print("Send a message: ");
			String message = cmdLineScanner.nextLine();

			message = name + " has sent a message: " + message;

			out.println(message);
			out.flush();
		/*}
		catch(IOException e) {
			e.printStackTrace();
		}*/

	}

	public static void main(String[] args)
	{
		Client c = new Client();
	}	

}

class ServerListener implements Runnable {
	Client c;
	Socket socket;
	Scanner in;
	
	ServerListener(Client c, Socket s) {

		this.c = c;
		socket = s;

	}

	@Override
	public void run() {

		try{
			in = new Scanner(new BufferedInputStream(socket.getInputStream()));
			System.out.println("Client - waiting to read.");
			if(in.hasNext()){
				String receivedMessage = in.nextLine();
				System.out.println("\n" + receivedMessage);
			}

		}
		catch(IOException e){
			e.printStackTrace();
		}

	}

	
}