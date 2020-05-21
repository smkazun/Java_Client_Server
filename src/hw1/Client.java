package hw1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

		// TODO Auto-generated method stub
		Socket serverSocket = null;
		String serverHostName = "localhost";
		int serverPortNumber = 1111;
		ServerListener sListener;
		String name;
		PrintWriter out;
		static Scanner in;
		
		
		Client() {
			
			//1. Enter name
			in = new Scanner(System.in);
			
			System.out.print("Please enter your name and press enter: ");
			name = in.nextLine();
			
			//2. Connect to server
			connectToServer();
			
			//3. create listener for server
			sListener = new ServerListener(this, serverSocket);
			new Thread(sListener).start();
			
			//send data to server
			try {
				out = new PrintWriter(new BufferedOutputStream(serverSocket.getOutputStream()));
				out.println(name); //sends name
				out.flush();	
			}
			catch(IOException e) {
				e.printStackTrace();
			}
			
			messagePrompt();
		}
	
	/**
	 * Prompts the user to send a message
	 */
	private void messagePrompt() {
		//System.out.print("Send a message: ");
		//String message = in.nextLine();
		
		while(true) {
			System.out.print("Send a message: ");
			String message = in.nextLine();
			
			message = name + " has sent a message: " + message;
			
			try {
				out = new PrintWriter(new BufferedOutputStream(serverSocket.getOutputStream()));
				out.println(message);
				out.flush();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
		
	/**
	 * Connect to server	
	 */
	private void connectToServer() {
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
		

	public static void main(String[] args) {
		Client c = new Client();
	}	

}

class ServerListener implements Runnable {
	Client c;
	Scanner in;
	Socket socket;
	
	ServerListener(Client c, Socket s) {
			this.c = c;
			socket = s;	
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			in = new Scanner(new BufferedInputStream(socket.getInputStream()));

			/* Ran out of time ~~~~~
			 * TODO: This allows me to send a message to the other clients, BUT it 
				creates a null exception error in the sending client thread, and doesnt send it to the server.
				
			 */
			//String message = in.nextLine(); 
			//System.out.println(message);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
}