package hw1;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import static org.mockito.Mockito.*;


public class ClientTest {

    private static Scanner cmdLineScanner;
    private static Scanner in;
    private static PrintWriter out;

    static Thread serverThread;
    static ServerSocket server;

    private OutputStream serverOut;
    private InputStream serverIn;




    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }


    @Before //@BeforeClass
    public void setUp() throws Exception {

       try{
           server = new ServerSocket(1111);
           listen(server);
       }
       catch(IOException e){
           e.printStackTrace();
       }

    }

    /*
    @After
    public void tearDown() throws Exception {
        in.close();
        out.close();
        cmdLineScanner.close();
        s.close();
    } */


    @Test
    public void testClientStart() {

        //SendMessage mockSendMessage = Mockito.mock(SendMessage.class);
        //when(mockSendMessage.run()).thenReturn(true);

        //setup
        String data = "Ben" + "\nHow are you im good" + "\nDown to play tomorrow?";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        Client c = new Client(reader);
        c.connect();
        c.start();

        Client spy = Mockito.spy(c);
        Mockito.doNothing().when(spy).start();

        //act


        //assert
        assertEquals("How are you im good", c.readMessage.readMessageFromServer());



    }


    @Test
    public void testSendMessage() {


        SendMessage mockSendMessage = Mockito.mock(SendMessage.class);
        //when(mockSendMessage.run()).thenReturn(true);


       // verify(readMessage, times(1)).run();
        //verify(sendMessage, times(1)).run();


    }



    @Test
    public void testReadMessageFromServer(){

        BufferedReader mockReader = mock(BufferedReader.class);
        Socket mockSocket = mock(Socket.class);

        try {
            when(mockReader.readLine()).thenReturn("Hello", "Im well", "and you?");
        }
        catch(IOException e){
            e.printStackTrace();
        }

        //Act
        ReadMessage read = new ReadMessage(mockSocket, mockReader);
        String testMessage = read.readMessageFromServer();
        String testMessage2 = read.readMessageFromServer();
        String testMessage3 = read.readMessageFromServer();

        // Assert
        assertEquals("Hello", testMessage);
        assertEquals("Im well", testMessage2);
        assertEquals("and you?", testMessage3);

    }

    @Test
    public void testSendMessageToServer(){

        BufferedReader mockReader = mock(BufferedReader.class);
        Socket mockSocket = mock(Socket.class);
        PrintWriter mockWriter = mock(PrintWriter.class);
        final String name = "Ben";

        try {
            when(mockReader.readLine()).thenReturn("Hello", "Im well", "and you?");
            when(mockWriter.println(" ")).thenReturn("Hello").
        }
        catch(IOException e){
            e.printStackTrace();
        }

        //Act
        SendMessage send = new SendMessage(mockSocket, mockReader, mockWriter, name);
        //String testMessage = send.createMessage();
        //String testMessage2 = send.createMessage();
        //String testMessage3 = send.createMessage();

        send.sendMessageToServer();
        send.sendMessageToServer();
        send.sendMessageToServer();

        /*
        try {
            //printWrite(mockWriter, testMessage);
            //printWrite(mockWriter, testMessage2);
            //printWrite(mockWriter, testMessage3);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }*/

        //assert
        assertEquals();

    }

    @Test
    public void testCreateMessage(){

        BufferedReader mockReader = mock(BufferedReader.class);
        Socket mockSocket = mock(Socket.class);
        PrintWriter mockWriter = mock(PrintWriter.class);
        final String name = "Ben";

        try {
            when(mockReader.readLine()).thenReturn("Hello", "Im well", "and you?");
        }
        catch(IOException e){
            e.printStackTrace();
        }

        //Act
        SendMessage send = new SendMessage(mockSocket, mockReader, mockWriter, name);
        String testMessage = send.createMessage();
        String testMessage2 = send.createMessage();
        String testMessage3 = send.createMessage();

        // Assert
        assertEquals("Ben has sent a message: Hello", testMessage);
        assertEquals("Ben has sent a message: Im well", testMessage2);
        assertEquals("Ben has sent a message: and you?", testMessage3);

    }


    //Integration
    @Test
    public void testConnection() {

        //setup
        String data = "Ben";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        Client c = new Client(reader);
        c.connect();

        //assert
        assertEquals(c.serverPortNumber, server.getLocalPort());
        assertEquals(c.name, data);

        //cleanup
        try{
            //stdin.close();
            reader.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }


    /** Helpers */
    private void write(OutputStream out, String str) throws IOException {
        out.write(str.getBytes());
        out.flush();
    }

    /**
     * Writes to an OutputStream. Used for both server and client output streams.
     */
    private void printWrite(PrintWriter out, String str) throws IOException {
        out.println(str);
        out.flush();
    }

    /**
     * Reads from an InputStream. Used for both server and client input streams.
     */
    private void assertRead(BufferedReader in, String expected) throws IOException {
        //assertEquals("Too few bytes available for reading: ", expected.length(), in);

        //byte[] buf = new byte[expected.length()];
        in.readLine();
        assertEquals(expected, new String(buf));
    }

    /**
     * Listens for and accepts one incoming request server side on a separate
     * thread. When a request is received, grabs its IO streams and "signals" to
     * the client side above through the shared lock object.
     */
    private void listen(ServerSocket server) {
        new Thread(() -> {
            try {
                Socket socket = server.accept();
                System.out.println("Incoming connection: " + socket);

                serverOut = socket.getOutputStream();
                serverIn = socket.getInputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}