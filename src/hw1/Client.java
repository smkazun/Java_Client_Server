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
	String name;
	PrintWriter out;
	static Scanner cmdLineScanner;
	Scanner in;


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


		//setup I/O streams
		try{

			out = new PrintWriter(new BufferedOutputStream(serverSocket.getOutputStream()));
			in = new Scanner(new BufferedInputStream(serverSocket.getInputStream()));
		}
		catch(IOException e) {
			e.printStackTrace();
		}

		Thread sendMessage = new Thread(new Runnable()
		{
			@Override
			public void run() {
				while (true) {
					//get message to send
					System.out.println("Send a message: ");
					String message = cmdLineScanner.nextLine();

					message = name + " has sent a message: " + message;

					//write message to server
					out.println(message);
					out.flush();
				}
			}
		});

		Thread readMessage = new Thread(new Runnable()
		{
			@Override
			public void run() {
				while (true) {

					String message = in.nextLine();
					System.out.println(message);

				}
			}
		});

		sendMessage.start();
		readMessage.start();

	}


	public static void main(String[] args)
	{
		Client c = new Client();
	}	

}
