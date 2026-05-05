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
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mock;
import static org.mockito.Mockito.*;

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
    private ObjectInputStream clientIn;
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
        clientIn = new ObjectInputStream(clientSocket.getInputStream());
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

        // reset the Server
        Server.getLobbies().clear();
        Server.getLobbyViews().clear();
        Server.getPendingViews().clear();
    }

    @Test
    void startListeningThread_CREATE_LOBBY() throws IOException, InterruptedException {
        // Act
        Server.getPendingViews().add(proxy);
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.CREATE_LOBBY, "123456", 3));
        clientOut.flush();

        // Assert
        Thread.sleep(200);
        assertTrue(Server.getLobbies().containsKey("123456"));
    }

    @Test
    void startListeningThread_JOIN_LOBBY() throws IOException, InterruptedException, ClassNotFoundException {
        // set up Mock Lobby
        when(mockController.isFull()).thenReturn(false);
        Server.getLobbies().put("123456", mockController);
        Server.getLobbyViews().put("123456", new ArrayList<>());
        Server.getPendingViews().add(proxy);

        // Act
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.JOIN_LOBBY, "123456", null));
        clientOut.flush();

        // Assert
        Thread.sleep(200);
        Message message = (Message) clientIn.readObject();
        assertEquals(MessageType.NICKNAME, message.getType());

    }

    @Test
    void startListeningThread_GET_AVAILABLE_LOBBIES() throws IOException, ClassNotFoundException {
        // set up Mock Lobby
        when(mockController.isFull()).thenReturn(false);
        when(mockController.getOccupiedSlots()).thenReturn(3);
        Server.getLobbies().put("123456", mockController);

        // Act
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.GET_AVAILABLE_LOBBIES, "", null));
        clientOut.flush();

        // Assert
        Message message = (Message) clientIn.readObject();
        assertEquals(MessageType.SHOW_LOBBIES, message.getType());
        ShowLobbiesMessage showLobbiesMessage = (ShowLobbiesMessage) message;
        Map<String, Integer> lobbies = showLobbiesMessage.getAvailableLobbies();
        assertTrue(lobbies.containsKey("123456"));
        assertEquals(3, lobbies.get("123456"));
    }

    @Test
    void startListeningThread_NICKNAME() throws IOException, InterruptedException {
        // set up Mock Controller
        when(mockController.getClients()).thenReturn(Map.of());
        when(mockController.connect(proxy, "nickname")).thenReturn(false);
        Server.getLobbies().put("123456", mockController);
        Server.getLobbyViews().put("123456", new ArrayList<>(List.of(proxy)));

        // Act
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.NICKNAME, "123456", "nickname"));
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
        Server.getLobbies().put("123456", mockController);
        Server.getLobbyViews().put("123456", new ArrayList<>(List.of(proxy, mockView)));
        List<IF_GameView> clients = Server.getLobbyViews().get("123456");
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
        assertFalse(Server.getLobbies().containsKey("123456"));
        assertFalse(Server.getLobbyViews().containsKey("123456"));
    }

    @Test
    void confirmConnection() throws IOException, ClassNotFoundException {
        // Act
        proxy.confirmConnection();

        // Assert
        Message response = (Message) clientIn.readObject();
        assertEquals(MessageType.CONFIRM_CONNECTION, response.getType());
    }

    @Test
    void confirmLobbyJoined() throws IOException, ClassNotFoundException {
        // Act
        proxy.confirmLobbyJoined();

        Message response = (Message) clientIn.readObject();
        assertEquals(MessageType.CONFIRM_LOBBY_JOINED, response.getType());
    }

    @Test
    void startListeningThread_CorrectDisconnection() throws IOException {
        // set up Server
        Server.getLobbies().put("123456", mockController);
        Server.getLobbyViews().put("123456", new ArrayList<>(List.of(proxy)));

        // Act
        proxy.end();

        // Assert
        assertTrue(Server.getLobbies().containsKey("123456"));
        assertTrue(Server.getLobbyViews().containsKey("123456"));
    }
}