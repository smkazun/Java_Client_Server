package hw1;


import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import static org.mockito.Mockito.*;


public class ClientTest {


    static ServerSocket server;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }



    @BeforeClass
    public static void setUp() throws Exception {

       server = new ServerSocket(1111);
       listen(server);
    }


    @AfterClass
    public static void tearDown() throws Exception {
        server.close();
    }


    @Test
    public void testClientStart() throws IOException{

        BufferedReader mockReader = mock(BufferedReader.class);
        //ReadMessage mockReadMessage = mock(ReadMessage.class);
        //SendMessage mockSendMessage = mock(SendMessage.class);

        when(mockReader.readLine()).thenReturn("Hello", "Im well", "and you?");

        //Act
        Client c = new Client(mockReader, "localhost", 1111);
        c.connect();
        c.start();

        //Assert
        assertNotNull(c.out);
        assertNotNull(c.in);
        assertNotNull(c.readMessage);
        assertNotNull(c.sendMessage);

        //verify(mockReadMessage).run();
        //verify(mockSendMessage).run();

    }



    //DONE
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

    //DONE
    @Test
    public void testSendMessageToServer() throws IOException {

        BufferedReader mockReader = mock(BufferedReader.class);
        Socket mockSocket = mock(Socket.class);
        PrintWriter mockWriter = mock(PrintWriter.class);
        final String name = "Ben";

        when(mockReader.readLine()).thenReturn("Hello", "Im well", "and you?");

        //Act
        SendMessage send = new SendMessage(mockSocket, mockReader, mockWriter, name); //mockSocket

        send.sendMessageToServer();
        send.sendMessageToServer();
        send.sendMessageToServer();

        //assert
        verify(mockWriter).println("Ben has sent a message: Hello");
        verify(mockWriter).println("Ben has sent a message: Im well");
        verify(mockWriter).println("Ben has sent a message: and you?");

    }

    //DONE
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
    public void testConnection() throws IOException{

        //setup
        final String data = "Ben";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        Client c = new Client(reader, "localhost", 1111);
        c.connect();

        //assert
        assertEquals(c.serverPortNumber, server.getLocalPort());
        assertEquals(c.name, data);

        //cleanup
        reader.close();
    }



    /**
     * Basic server listens for and accepts one incoming request server side on a separate
     * thread.
     */
    private static void listen(ServerSocket server) {
        new Thread(() -> {
            try {
                Socket socket = server.accept();
                System.out.println("Incoming connection: " + socket);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}