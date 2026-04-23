package mesos.am30.server;

import mesos.am30.GameModel.BuildingCard;
import mesos.am30.GameModel.CharacterCard;
import mesos.am30.GameModel.Tile;
import mesos.am30.common.*;

import mesos.am30.client.IF_GameView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


@ExtendWith(MockitoExtension.class)
class SocketProxyTest {
    private SocketProxy proxy;
    private ServerSocket serverSocket;
    private Socket proxySocket;
    private Socket clientSocket;
    private ObjectOutputStream clientOut;

    @Mock
    private Controller mockController;

    @Mock
    private Tile mockTile;

    @Mock
    private CharacterCard mockCharacterCard;

    @Mock
    private BuildingCard mockBuildingCard;

    @Mock
    private IF_GameView mockView;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        // set up SocketProxy

        CountDownLatch latch = new CountDownLatch(1);
        serverSocket = new ServerSocket(0);
        new Thread(() -> {
            try {
                proxySocket = serverSocket.accept();
                ObjectOutputStream outputStream = new ObjectOutputStream(proxySocket.getOutputStream());
                outputStream.flush();
                ObjectInputStream inputStream = new ObjectInputStream(proxySocket.getInputStream());
                proxy = new SocketProxy(proxySocket, outputStream, inputStream);
                latch.countDown();
            } catch (IOException e) {
                try { proxySocket.close(); } catch (IOException ignored) {}
            }
        }).start();

        // set up Client connection
        clientSocket = new Socket("localhost", serverSocket.getLocalPort());
        clientOut = new ObjectOutputStream(clientSocket.getOutputStream());
        clientOut.flush();
        latch.await();
    }

    @AfterEach
    void tearDown() throws IOException {
        // close previous connections
        serverSocket.close();
        proxySocket.close();
        clientSocket.close();
    }

    @Test
    void startListeningThread_PLAYERS_NUMBER() throws IOException, InterruptedException {
        // Act
        Server.setLobby(null);
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.PLAYERS_NUMBER, "", 3));
        clientOut.flush();

        // Assert
        Thread.sleep(200);
        assertNotNull(Server.getLobby());
        Controller controller = Server.getLobby();
        assertEquals(3, controller.getPlayersNumber());
    }

    @Test
    void startListeningThread_NICKNAME() throws IOException, InterruptedException {
        // set up Mock Controller
        when(mockController.isFull()).thenReturn(false);
        when(mockController.getClients()).thenReturn(Map.of());
        when(mockController.connect(proxy, "nickname")).thenReturn(false);

        // Act
        Server.setLobby(mockController);
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.NICKNAME, "", "nickname"));
        clientOut.flush();

        // Assert
        Thread.sleep(200);
        verify(mockController).connect(proxy, "nickname");
    }

    @Test
    void startListeningThread_CHOOSE_TILE() throws IOException, InterruptedException {
        // Act
        proxy.setController(mockController);
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.CHOOSE_TILE, "nickname", mockTile));
        clientOut.flush();

        // Assert
        Thread.sleep(200);
        verify(mockController).chooseTile(eq("nickname"), any(Tile.class));
    }

    @Test
    void startListeningThread_CHOOSE_CHARACTER() throws IOException, InterruptedException {
        // Act
        proxy.setController(mockController);
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.CHOOSE_CHARACTER, "nickname", mockCharacterCard));
        clientOut.flush();

        // Assert
        Thread.sleep(200);
        verify(mockController).chooseCharacter(eq("nickname"), any(CharacterCard.class));
    }

    @Test
    void startListeningThread_CHOOSE_BUILDING() throws IOException, InterruptedException {
        // Act
        proxy.setController(mockController);
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.CHOOSE_BUILDING, "nickname", mockBuildingCard));
        clientOut.flush();

        // Assert
        Thread.sleep(200);
        verify(mockController).chooseBuilding(eq("nickname"), any(BuildingCard.class));
    }

    @Test
    void startListeningThread_WrongDisconnection() throws IOException, InterruptedException {
        // set up Server
        Server.setLobby(new Controller(1));
        List<IF_GameView> clients = Server.getConnectedViews();
        clients.add(proxy);
        for (int i = 0; i < 3; i++) clients.add(mockView);

        // set up Mock IOException
        doThrow(new IOException()).when(mockController).chooseTile(any(String.class), any(Tile.class));
        proxy.setController(mockController);

        // Act
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.CHOOSE_TILE, "nickname", mockTile));
        clientOut.flush();

        // Assert
        Thread.sleep(200);
        verify(mockController).chooseTile(eq("nickname"), any(Tile.class));
        verify(mockView, times(clients.size() - 1)).notifyError(ErrorType.END_FOR_DISCONNECTION);
        verify(mockView, times(clients.size() - 1)).end();
        assertTrue(Server.getConnectedViews().isEmpty());
        assertNull(Server.getLobby());

    }

    @Test
    void startListeningThread_CorrectDisconnection() throws IOException {
        // set up Server
        Server.setLobby(new Controller(1));
        List<IF_GameView> clients = Server.getConnectedViews();
        clients.add(proxy);

        // Act
        proxy.end();

        // Assert
        assertNotNull(Server.getConnectedViews());
        assertTrue(Server.getConnectedViews().contains(proxy));
        assertNotNull(Server.getLobby());
    }
}