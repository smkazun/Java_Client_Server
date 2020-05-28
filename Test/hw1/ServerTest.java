package hw1;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.Socket;

import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class ServerTest {

    static Thread serverThread;
    private static Server s;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        s.close();
    }

    @Test
    public void testBroadcastToClients() {

    }

    @Test
    public void testsendMessageToClient() {

    }

}