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
    static Connection connection;

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
        connection = Connection.CLIENT;
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
        assertNotNull(c.getClientOutputStream());
        assertNotNull(c.getClientInputStream());
        assertNotNull(c.getReadHandler());
        assertNotNull(c.getSendHandler());

    }


    @Test
    public void testReadMessage() throws IOException{

        when(mockReader.readLine()).thenReturn("Hello", "Im well", "and you?");

        Messenger messenger = new Messenger(mockWriter, mockReader, connection);

        //Act
        String testMessage = messenger.readMessage();
        String testMessage2 = messenger.readMessage();
        String testMessage3 = messenger.readMessage();

        // Assert
        assertEquals("Hello", testMessage);
        assertEquals("Im well", testMessage2);
        assertEquals("and you?", testMessage3);

    }

    @Test
    public void testSendMessage() throws IOException {

        when(mockReader.readLine()).thenReturn("Hello", "Im well", "and you?");
        Messenger messenger = new Messenger(mockWriter, mockReader, connection);

        //Act
        String message1 = messenger.createMessage(name);
        String message2 = messenger.createMessage(name);
        String message3 = messenger.createMessage(name);

        messenger.sendMessage(message1);
        messenger.sendMessage(message2);
        messenger.sendMessage(message3);

        //assert
        verify(mockWriter).println("Ben has sent a message: Hello");
        verify(mockWriter).println("Ben has sent a message: Im well");
        verify(mockWriter).println("Ben has sent a message: and you?");

    }

    @Test
    public void testCreateMessage() throws IOException{

        when(mockReader.readLine()).thenReturn("Hello", "Im well", "and you?");

        //Act
        Messenger messenger = new Messenger(mockWriter, mockReader, connection);

        String testMessage = messenger.createMessage(name);
        String testMessage2 = messenger.createMessage(name);
        String testMessage3 = messenger.createMessage(name);

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
        assertEquals(server.getPortNumber(), c.getPortNumber());
        assertEquals(name, c.getUserName());

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