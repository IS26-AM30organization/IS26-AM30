package mesos.am30.server;

import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.Tile;
import mesos.am30.common.*;

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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
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
    private Server mockServer;

    @Mock
    private Controller mockController;

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
                proxy.setServer(mockServer);
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
    }

    @Test
    void startListeningThread_CorrectDisconnection() throws IOException, InterruptedException, ClassNotFoundException {
        // Act
        proxy.end();

        // Assert Client-side
        Thread.sleep(2000);
        Message message = (Message) clientIn.readObject();
        assertEquals(MessageType.END, message.getType());

        // throw IOException (no real View which closes the connection)
        clientSocket.close();

        // Assert Proxy
        verifyNoInteractions(mockServer);
        verifyNoInteractions(mockController);
        assertFalse(proxy.isConnectionOpen());
        assertTrue(proxySocket.isClosed());
    }

    @Test
    void startListeningThread_WrongDisconnection() throws IOException, InterruptedException {
        // set up Mock IOException
        doThrow(new IOException()).when(mockController).chooseTile(any(String.class), any(Tile.class));
        proxy.setController(mockController);

        // Act
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.CHOOSE_TILE, "nickname", mock(Tile.class)));
        clientOut.flush();

        // Assert
        Thread.sleep(2000);
        verify(mockController).chooseTile(eq("nickname"), any(Tile.class));
        verify(mockServer, times(1)).handleDisconnection(proxy);
    }

    @Test
    void startListeningThread_WrongMessage() throws IOException, InterruptedException {
        // Act
        proxy.setController(mockController);
        clientOut.writeObject(new Message(MessageType.NOTIFY));

        // Assert
        Thread.sleep(2000);
        verifyNoInteractions(mockServer);
        verifyNoInteractions(mockController);
    }

    @Test
    void startListeningThread_WrongChoice_Server() throws IOException, InterruptedException {
        // Act
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.CHOOSE_TILE, "nickname", mock(Tile.class)));

        // Assert
        Thread.sleep(2000);
        verifyNoInteractions(mockServer);
        verifyNoInteractions(mockController);
    }

    @Test
    void startListeningThread_WrongChoice_Controller() throws IOException, InterruptedException {
        // Act
        proxy.setController(mockController);
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.NICKNAME, "123456", "nickname"));

        // Assert
        Thread.sleep(2000);
        verifyNoInteractions(mockServer);
        verifyNoInteractions(mockController);
    }

    @Test
    void startListeningThread_CREATE_LOBBY() throws IOException, InterruptedException {
        // Act
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.CREATE_LOBBY, "123456", 3));
        clientOut.flush();

        // Assert
        Thread.sleep(2000);
        verify(mockServer, times(1)).createLobby(proxy, 3, "123456");
        verifyNoInteractions(mockController);
    }

    @Test
    void startListeningThread_GET_AVAILABLE_LOBBIES() throws IOException, InterruptedException {
        // Act
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.GET_AVAILABLE_LOBBIES, "", null));
        clientOut.flush();

        // Assert
        Thread.sleep(2000);
        verify(mockServer, times(1)).showAvailableLobbies(proxy);
        verifyNoInteractions(mockController);
    }

    @Test
    void startListeningThread_JOIN_LOBBY() throws IOException, InterruptedException {
        // Act
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.JOIN_LOBBY, "123456", null));
        clientOut.flush();

        // Assert
        Thread.sleep(2000);
        verify(mockServer, times(1)).joinLobby(proxy, "123456");
        verifyNoInteractions(mockController);
    }

    @Test
    void startListeningThread_NICKNAME() throws IOException, InterruptedException {
        // Act
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.NICKNAME, "123456", "nickname"));
        clientOut.flush();

        // Assert
        Thread.sleep(2000);
        verify(mockServer, times(1)).setNickname(proxy, "nickname", "123456");
        verifyNoInteractions(mockController);
    }

    @Test
    void startListeningThread_CHOOSE_TILE() throws IOException, InterruptedException {
        // Act
        proxy.setController(mockController);
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.CHOOSE_TILE, "nickname", mock(Tile.class)));
        clientOut.flush();

        // Assert
        Thread.sleep(2000);
        verifyNoInteractions(mockServer);
        verify(mockController).chooseTile(eq("nickname"), any(Tile.class));
    }

    @Test
    void startListeningThread_CHOOSE_CHARACTER() throws IOException, InterruptedException {
        // Act
        proxy.setController(mockController);
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.CHOOSE_CHARACTER, "nickname", mock(CharacterCard.class)));
        clientOut.flush();

        // Assert
        Thread.sleep(2000);
        verifyNoInteractions(mockServer);
        verify(mockController).chooseCharacter(eq("nickname"), any(CharacterCard.class));
    }

    @Test
    void startListeningThread_CHOOSE_BUILDING() throws IOException, InterruptedException {
        // Act
        proxy.setController(mockController);
        clientOut.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, Choice.CHOOSE_BUILDING, "nickname", mock(BuildingCard.class)));
        clientOut.flush();

        // Assert
        Thread.sleep(2000);
        verifyNoInteractions(mockServer);
        verify(mockController).chooseBuilding(eq("nickname"), any(BuildingCard.class));
    }

    @Test
    void confirmConnection() throws IOException, InterruptedException, ClassNotFoundException {
        // Act
        proxy.confirmConnection();

        // Assert
        Thread.sleep(2000);
        Message message = (Message) clientIn.readObject();
        assertEquals(MessageType.CONFIRM_CONNECTION, message.getType());
    }

    @Test
    void showLobbies() throws IOException, InterruptedException, ClassNotFoundException {
        // set up Lobbies
        Map<String, Integer> lobbies = new HashMap<>();
        lobbies.put("123456", 5);
        lobbies.put("123457", 2);

        // Act
        proxy.showLobbies(lobbies);

        // Assert
        Thread.sleep(2000);
        Message message = (Message) clientIn.readObject();
        assertEquals(MessageType.SHOW_LOBBIES, message.getType());
        ShowLobbiesMessage showLobbiesMessage = (ShowLobbiesMessage) message;
        assertEquals(lobbies, showLobbiesMessage.getAvailableLobbies());
    }

    @Test
    void askNickname() throws IOException, InterruptedException, ClassNotFoundException {
        // Act
        proxy.askNickname("123456");

        // Assert
        Thread.sleep(2000);
        Message message = (Message) clientIn.readObject();
        assertEquals(MessageType.NICKNAME, message.getType());
        AskNicknameMessage askNicknameMessage = (AskNicknameMessage) message;
        assertEquals("123456", askNicknameMessage.getLobbyCode());
    }

    @Test
    void confirmLobbyJoined() throws IOException, InterruptedException, ClassNotFoundException {
        // Act
        proxy.confirmLobbyJoined();

        // Assert
        Thread.sleep(2000);
        Message message = (Message) clientIn.readObject();
        assertEquals(MessageType.CONFIRM_LOBBY_JOINED, message.getType());
    }

    @Test
    void notifyTurn() throws IOException, InterruptedException, ClassNotFoundException {
        // Act
        proxy.notifyTurn("nickname", Move.PICK_TILE);

        // Assert
        Thread.sleep(2000);
        Message message = (Message) clientIn.readObject();
        assertEquals(MessageType.NOTIFY, message.getType());
        ClienTurnMessage clienTurnMessage = (ClienTurnMessage) message;
        assertEquals("nickname", clienTurnMessage.getNickname());
        assertEquals(Move.PICK_TILE, clienTurnMessage.getMove());
    }

    @Test
    void notifyError() throws IOException, InterruptedException, ClassNotFoundException {
        // Act
        proxy.notifyError(ErrorType.WRONG_TILE);

        // Assert
        Thread.sleep(2000);
        Message message = (Message) clientIn.readObject();
        assertEquals(MessageType.ERROR, message.getType());
        ErrorMessage errorMessage = (ErrorMessage) message;
        assertEquals(ErrorType.WRONG_TILE, errorMessage.getError());
    }

    @Test
    void update() throws IOException, InterruptedException, ClassNotFoundException {
        // Act
        proxy.update(ViewParameter.PLAYERS, List.of(mock(Player.class)));

        // Assert
        Thread.sleep(2000);
        Message message = (Message) clientIn.readObject();
        assertEquals(MessageType.UPDATE, message.getType());
        ModelUpdateMessage modelUpdateMessage = (ModelUpdateMessage) message;
        assertEquals(ViewParameter.PLAYERS, modelUpdateMessage.getToUpdate());
        List<Player> players = new ArrayList<>();
        for (Object parameter : modelUpdateMessage.getParameters()) {
            players.add((Player) parameter);
        }
        assertEquals(players, modelUpdateMessage.getParameters());
    }

    @Test
    void ping() throws IOException, InterruptedException, ClassNotFoundException {
        // Act
        proxy.ping();

        // Assert
        Thread.sleep(2000);
        Message message = (Message) clientIn.readObject();
        assertEquals(MessageType.PING, message.getType());
    }
}