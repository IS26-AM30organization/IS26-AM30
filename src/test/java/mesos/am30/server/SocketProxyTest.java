package mesos.am30.server;

import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.Tile;
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
import static mesos.am30.server.Server.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
    private ObjectInputStream clientIn;


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
        getLobbies().clear();
        getLobbyViews().clear();
        getPendingViews().clear();

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
        clientIn = new ObjectInputStream(clientSocket.getInputStream());
    }

    @AfterEach
    void tearDown() throws IOException {
        // close previous connections
        serverSocket.close();
        proxySocket.close();
        clientSocket.close();
        getLobbies().clear();
        getLobbyViews().clear();
        getPendingViews().clear();
    }

    @Test
    void startListeningThread_CREATE_LOBBY() throws IOException, InterruptedException {
        // Act
        getPendingViews().add(proxy);
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.CREATE_LOBBY, "123456", 3));
        clientOut.flush();

        // Assert
        Thread.sleep(200);
        assertTrue(getLobbies().containsKey("123456"));
    }

    @Test
    void startListeningThread_JOIN_LOBBY() throws IOException, InterruptedException {
        // Act
        when(mockController.getPlayersNumber()).thenReturn(2);
        getLobbies().put("123456", mockController);
        getLobbyViews().put("123456", new ArrayList<>());
        getPendingViews().add(proxy);
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.JOIN_LOBBY, "", "123456"));
        clientOut.flush();

        // Assert
        Thread.sleep(200);
        assertTrue(getLobbyViews().get("123456").contains(proxy));
        assertFalse(getPendingViews().contains(proxy));
    }

    @Test
    void startListeningThread_GET_AVAILABLE_LOBBIES() throws IOException, ClassNotFoundException {
        // Act
        when(mockController.isFull()).thenReturn(false);
        when(mockController.getOccupiedSlots()).thenReturn(3);
        getLobbies().put("123456", mockController);
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.GET_AVAILABLE_LOBBIES, "", null));
        clientOut.flush();

        // Assert
        Message response = (Message) clientIn.readObject();
        assertEquals(MessageType.SHOW_LOBBIES, response.getType());
        ClientChoiceMessage m = (ClientChoiceMessage) response;
        Map<String, Integer> lobbies = (Map<String, Integer>) m.getParameter();
        assertTrue(lobbies.containsKey("123456"));
        assertEquals(3, lobbies.get("123456"));
    }

    @Test
    void startListeningThread_NICKNAME() throws IOException, InterruptedException {
        // set up Mock Controller
        when(mockController.getClients()).thenReturn(Map.of());
        when(mockController.connect(proxy, "nickname")).thenReturn(false);
        getLobbies().put("123456", mockController);
        getLobbyViews().put("123456", new ArrayList<>(List.of(proxy)));

        // Act
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
        getLobbies().put("123456", mockController);
        getLobbyViews().put("123456", new ArrayList<>(List.of(proxy, mockView)));

        // set up Mock IOException
        doThrow(new IOException()).when(mockController).chooseTile(any(String.class), any(Tile.class));
        proxy.setController(mockController);

        // Act
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.CHOOSE_TILE, "nickname", mockTile));
        clientOut.flush();

        // Assert
        Thread.sleep(200);
        verify(mockController).chooseTile(eq("nickname"), any(Tile.class));
        verify(mockView).notifyError(ErrorType.END_FOR_DISCONNECTION);
        verify(mockView).end();
        assertFalse(getLobbies().containsKey("123456"));
        assertFalse(getLobbyViews().containsKey("123456"));

    }

    @Test
    void confirmConnection() throws IOException, ClassNotFoundException {
        proxy.confirmConnection();

        Message response = (Message) clientIn.readObject();
        assertEquals(MessageType.CONFIRM_CONNECTION, response.getType());
    }

    @Test
    void confirmLobbyJoined() throws IOException, ClassNotFoundException {
        proxy.confirmLobbyJoined("123456");

        Message response = (Message) clientIn.readObject();
        assertEquals(MessageType.CONFIRM_LOBBY_JOINED, response.getType());
        assertInstanceOf(ClientChoiceMessage.class, response);
        ClientChoiceMessage m = (ClientChoiceMessage) response;
        assertEquals("123456", m.getParameter());
    }

    @Test
    void startListeningThread_CorrectDisconnection() throws IOException {
        // set up Server
        getLobbies().put("123456", mockController);
        getLobbyViews().put("123456", new ArrayList<>(List.of(proxy)));

        // Act
        proxy.end();

        // Assert
        assertTrue(getLobbies().containsKey("123456"));
        assertTrue(getLobbyViews().containsKey("123456"));
    }
}