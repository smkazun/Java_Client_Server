package hw1;

import org.junit.*;

import static org.junit.Assert.*;
import org.mockito.MockitoAnnotations;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import static org.mockito.Mockito.*;


public class ClientTest {

    static ServerSocket mockServer;
    static BufferedReader mockReader;
    static PrintWriter mockWriter;
    static Socket mockSocket;
    final String name = "Ben";

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Before
    public void setUp() throws Exception {
        mockReader = mock(BufferedReader.class);
        mockSocket = mock(Socket.class);
        mockWriter = mock(PrintWriter.class);
        mockServer = new ServerSocket(1111);
        listen(mockServer);
    }


    @After
    public void tearDown() throws Exception {
        mockServer.close();
    }


    @Test
    public void testClientStart() throws IOException{

        //Act
        Client c = new Client(mockReader, "localhost", 1111);
        c.connect();
        c.start();

        //Assert
        assertNotNull(c.out);
        assertNotNull(c.in);
        assertNotNull(c.readMessage);
        assertNotNull(c.sendMessage);

    }


    @Test
    public void testReadMessageFromServer(){

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
    public void testSendMessageToServer() throws IOException {

        when(mockReader.readLine()).thenReturn("Hello", "Im well", "and you?");

        //Act
        SendMessage send = new SendMessage(mockSocket, mockReader, mockWriter, name); //mockSocket
        SendMessage spySend = spy(send);

        spySend.sendMessageToServer();
        spySend.sendMessageToServer();
        spySend.sendMessageToServer();


        //assert
        verify(spySend , times(3)).createMessage();
        verify(mockWriter).println("Ben has sent a message: Hello");
        verify(mockWriter).println("Ben has sent a message: Im well");
        verify(mockWriter).println("Ben has sent a message: and you?");

    }

    @Test
    public void testCreateMessage(){

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


    //Integration Test
    @Test
    public void testClientConnection() throws IOException{

        //setup
        Server server = new Server(1111); //use actual server for integration test

        System.setIn(new ByteArrayInputStream(name.getBytes()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        Client c = new Client(reader, "localhost", 1111);
        c.connect();

        //assert
        assertEquals(server.serverPortNumber, c.serverPortNumber);
        assertEquals(name, c.name);

        //cleanup
        reader.close();
    }



    /**
     * Helper Method for mocking a server.
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