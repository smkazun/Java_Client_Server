package hw1;

import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import static org.mockito.Mockito.*;


public class ServerTest {

    static Server mockServer;
    static BufferedReader mockReader;
    static PrintWriter mockWriter;
    static int num;
    static Connection connection;


    @BeforeClass
    public static void setUp() {
        mockServer = mock(Server.class);
        mockReader = mock(BufferedReader.class);
        mockWriter = mock(PrintWriter.class);
        num = 0;
        connection = Connection.SERVER;

    }

    @AfterClass
    public static void tearDown() throws Exception {
        mockServer.close();
        mockReader.close();
        mockWriter.close();
    }


    @Test
    public void testBroadcast() {
        
        Messenger messenger = new Messenger(mockWriter, mockReader, connection);

        ClientHandler clientHandler1 = new ClientHandler(++num, mockServer, messenger);
        ClientHandler clientHandler2 = new ClientHandler(++num, mockServer, messenger);
        ClientHandler clientHandler3 = new ClientHandler(++num, mockServer, messenger);
        ClientHandler clientHandler4 = new ClientHandler(++num, mockServer, messenger);


        ConcurrentHashMap<Integer, ClientHandler> mockMap = new ConcurrentHashMap<>();
        mockMap.put(1, clientHandler1);
        mockMap.put(2, clientHandler2);
        mockMap.put(3, clientHandler3);
        mockMap.put(4, clientHandler4);

        //act
        messenger.broadcast("Some message", mockMap, clientHandler1.getClientNumber());

        //assert
        verify(mockWriter, times(3)).println("Some message");

    }

    @Test
    public void testSendMessageToClient() {

        Messenger messenger = new Messenger(mockWriter, mockReader, connection);

        //act
        messenger.sendMessage("Some message");

        //assert
        verify(mockWriter).println("Some message");
    }

    @Test
    public void testReadMessageFromClient() throws IOException {

        when(mockReader.readLine()).thenReturn("Hello", "Im well", "and you?");

        //Act
        Messenger messenger = new Messenger(mockWriter, mockReader, connection);

        String testMessage = messenger.readMessage();
        String testMessage2 = messenger.readMessage();
        String testMessage3 = messenger.readMessage();

        // Assert
        assertEquals("Hello", testMessage);
        assertEquals("Im well", testMessage2);
        assertEquals("and you?", testMessage3);
    }

    //Integration Test
    @Test
    public void testServerConnection() {

        new Thread(() -> {
            mockServer = new Server(1111); //simply substituting mockServer with the actual server class
            mockServer.listen();

        }).start();

        Client client1 = new Client(mockReader,"localhost", 1111);
        Client client2 = new Client(mockReader, "localhost", 1111);
        Client client3 = new Client(mockReader, "localhost", 1111);

        client1.connect();
        client2.connect();
        client3.connect();

        //wait for clients to connect
        try{
            Thread.sleep(100);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        assertNotNull(mockServer.getPortNumber());
        assertEquals(3, mockServer.getClients().size());
    }

}