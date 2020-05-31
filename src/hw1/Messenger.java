package hw1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;


enum Connection {
    CLIENT,
    SERVER
}

/**
 * Handles the sending and reading of messages. Uses a PrintWriter to send out messages
 * and a BufferedReader to read the incoming messages. The connection enum is used to
 * print output to the server console
 */
public class Messenger {

    private PrintWriter out;
    private BufferedReader in;
    private Connection connection;

    public Messenger(PrintWriter out, BufferedReader in, Connection connection){
        this.out = out;
        this.in = in;
        this.connection = connection;
    }

    /**
     * Reads incoming messages from the server/client
     * @return a message
     */
    public String readMessage()
    {
        String message = "";
        try {

            message = in.readLine();

            if(connection == Connection.SERVER){
                System.out.println(message);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return message;
    }


    /**
     * Iterate through the list of clients and sends message to all other clients
     * @param clients a map of the connected clients
     * @param message the message to send
     * @param num the sending clients identifying number
     */
    public synchronized void broadcast(String message, ConcurrentHashMap<Integer, ClientHandler> clients, int num)
    {
        for(ConcurrentHashMap.Entry<Integer, ClientHandler> client : clients.entrySet())
        {
            if(client.getKey() != num)
            {
                client.getValue().getMessenger().sendMessage(message);
            }
        }
    }


    /**
     * Creates a user made message
     * @return the message to send to the server
     */
    public String createMessage(String nameOfSender){

        String message = "";
        System.out.println("Send a message: ");

        try {
            message = in.readLine();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        message = nameOfSender + " has sent a message: " + message;

        return message;
    }

    /**
     * Sends a user created message to the server
     */
    public void sendMessage(String message){
        out.println(message);
        out.flush();
    }

}
