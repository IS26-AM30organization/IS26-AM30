package mesos.am30.client;

import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.EventCard;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.Tile;
import mesos.am30.common.*;

import mesos.am30.server.IF_GameController;
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
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

@ExtendWith(MockitoExtension.class)
class SocketViewTest {
    private SocketView view;
    private ServerSocket serverSocket;
    private Socket proxySocket;
    private ObjectInputStream proxyIn;
    private ObjectOutputStream proxyOut;
    private Socket clientSocket;

    @Mock
    private IF_GameUI mockUI;

    public SocketViewTest() {
    }

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        // set up SocketProxy
        CountDownLatch latch = new CountDownLatch(1);
        serverSocket = new ServerSocket(0);
        new Thread(() -> {
            try {
                proxySocket = serverSocket.accept();
                proxyOut = new ObjectOutputStream(proxySocket.getOutputStream());
                proxyOut.flush();
                proxyIn = new ObjectInputStream(proxySocket.getInputStream());
                latch.countDown();
            } catch (IOException e) {
                try { proxySocket.close(); } catch (IOException ignored) {}
            }
        }).start();

        // set up the View
        view = new SocketView(mockUI);

        // set up Client connection
        clientSocket = new Socket("localhost", serverSocket.getLocalPort());
        view.setSocket(clientSocket);
        view.setOutputStream(new ObjectOutputStream(clientSocket.getOutputStream()));
        view.setInputStream(new ObjectInputStream(clientSocket.getInputStream()));
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
    void findServer_Found() throws IOException, InterruptedException {
        // Act
        new Thread(() -> {
            try {
                view.findServer("localhost", serverSocket.getLocalPort());
            } catch (IOException ignored) { /* test will not throw IOException */ }
        }).start();

        // establish the connection Server-side
        Socket socket = serverSocket.accept();
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();

        // Assert
        Thread.sleep(200);
        assertTrue(view.isConnectionOpen());
        verify(mockUI, never()).printError(ErrorType.WRONG_IP);
        verify(mockUI, never()).printEnd();
    }

    @Test
    void findServer_NotFound() throws IOException {
        // Act
        view.findServer("", 0);

        // Assert
        assertFalse(view.isConnectionOpen());
        verify(mockUI).printError(ErrorType.WRONG_IP);
        verify(mockUI).printEnd();
    }

    @Test
    void startListeningThread_WrongDisconnection() throws IOException, InterruptedException {
        // Act
        view.startListeningThread();
        proxySocket.close();

        // Assert
        Thread.sleep(200);
        verify(mockUI, times(1)).printError(ErrorType.CONNECTION_CRASHED);
        verify(mockUI, times(1)).printEnd();
    }

    @Test
    void startListeningThread_CorrectDisconnection() throws IOException, InterruptedException {
        // Act
        view.startListeningThread();
        proxyOut.writeObject(new Message(MessageType.END));
        proxyOut.flush();

        // Assert
        Thread.sleep(200);
        verify(mockUI, times(1)).printEnd();
        verify(mockUI, never()).printError(ErrorType.CONNECTION_CRASHED);
    }

    @Test
    void stratListeningThread_CONFIRM_CONNECTION() throws IOException, InterruptedException {
        // Act
        view.startListeningThread();
        proxyOut.writeObject(new Message(MessageType.CONFIRM_CONNECTION));
        proxyOut.flush();

        // Assert
        Thread.sleep(200);
        verify(mockUI).confirmConnection();
    }

    @Test
    void stratListeningThread_SHOW_LOBBIES() throws IOException, InterruptedException {
        // set up Mock Lobbies
        Map<String, Integer> lobbies = new HashMap<>(4);
        for (int i = 0; i < 4; i++) {
            lobbies.put("lobby" + i, i);
        }

        // Act
        view.startListeningThread();
        proxyOut.writeObject(new ShowLobbiesMessage(MessageType.SHOW_LOBBIES, lobbies));
        proxyOut.flush();

        // Assert
        Thread.sleep(200);
        verify(mockUI).showLobbies(lobbies);
    }

    @Test
    void startListeningThread_NICKNAME() throws IOException, InterruptedException {
        // Act
        view.startListeningThread();
        proxyOut.writeObject(new AskNicknameMessage(MessageType.NICKNAME, "123456"));
        proxyOut.flush();

        // Assert
        Thread.sleep(200);
        verify(mockUI).askNickname();
    }

    @Test
    void startListeningThread_CONFRIM_LOBBY_JOINED() throws IOException, InterruptedException {
        // Act
        view.startListeningThread();
        proxyOut.writeObject(new Message(MessageType.CONFIRM_LOBBY_JOINED));
        proxyOut.flush();

        // Assert
        Thread.sleep(200);
        verify(mockUI).confirmLobbyJoined();
    }

    @Test
    void startListeningThread_NOTIFY() throws IOException, InterruptedException {
        // set up Mock Players
        List<Player> players = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            players.add(mock(Player.class));
            when(players.get(i).getNickname()).thenReturn("user" + i);
        }
        view.getModel().setPlayers(players);
        view.setNickname(players.getLast().getNickname());

        // Act
        view.startListeningThread();
        proxyOut.writeObject(new ClienTurnMessage(MessageType.NOTIFY, view.getNickname(), Move.PICK_TILE));
        proxyOut.flush();

        // Assert
        Thread.sleep(200);
        assertEquals(Move.PICK_TILE, view.getModel().getCurrentMove());
        assertEquals(view.getNickname(), view.getModel().getCurrentUser().getNickname());
        verify(mockUI).printMove(view.getNickname(), Move.PICK_TILE);
    }

    @Test
    void startListeningThread_ERROR() throws IOException, InterruptedException {
        // Act
        view.startListeningThread();
        proxyOut.writeObject(new ErrorMessage(MessageType.ERROR, ErrorType.NOT_YOUR_TURN));
        proxyOut.flush();

        // Assert
        Thread.sleep(200);
        verify(mockUI).printError(ErrorType.NOT_YOUR_TURN);
    }

    @Test
    void startListeningThread_UPDATE() throws IOException, InterruptedException {
        // set up Mock Players
        List<Object> upperRow = new ArrayList<>(6);
        for (int i = 0; i < 4; i++) {
            upperRow.add(mock(CharacterCard.class));
        }
        for (int i = 0; i < 2; i++) {
            upperRow.add(mock(EventCard.class));
        }

        // Act
        view.startListeningThread();
        proxyOut.writeObject(new ModelUpdateMessage(MessageType.UPDATE, ViewParameter.UPPER_ROW, upperRow));
        proxyOut.flush();

        // Assert (Mock are not serializable, assert on the Classes)
        Thread.sleep(200);
        ViewModel viewModel = view.getModel();
        assertEquals(upperRow.size(), viewModel.getUpperRow().size());
        assertTrue(viewModel.getUpperRow().stream()
                .limit(4)
                .allMatch(c -> c instanceof CharacterCard));
        assertTrue(viewModel.getUpperRow().stream()
                .filter(c -> viewModel.getUpperRow().indexOf(c) >= 4)
                .allMatch(c -> c instanceof EventCard));
        verify(mockUI).refresh(viewModel);
    }

    @Test
    void startListeningThread_END() throws IOException, InterruptedException {
        // Act
        view.startListeningThread();
        proxyOut.writeObject(new Message(MessageType.END));
        proxyOut.flush();

        // Assert
        Thread.sleep(200);
        verify(mockUI).printEnd();
    }

    @Test
    void startListeningThread_PING() throws IOException, InterruptedException {
        // Act
        view.startListeningThread();
        proxyOut.writeObject(new Message(MessageType.PING));
        proxyOut.flush();

        // Assert
        Thread.sleep(200);
        verifyNoInteractions(mockUI);
    }

    @Test
    void toServer() throws IOException, InterruptedException, ClassNotFoundException {
        // Act
        view.toServer(Choice.NICKNAME, "123456", "nickname");

        // Assert
        Thread.sleep(200);
        Message message = (Message) proxyIn.readObject();
        assertEquals(MessageType.CHOOSE, message.getType());
        ClientChoiceMessage clientChoiceMessage = (ClientChoiceMessage) message;
        assertEquals(Choice.NICKNAME, clientChoiceMessage.getChoice());
        assertEquals("123456", clientChoiceMessage.getIdentifier());
        assertEquals("nickname", clientChoiceMessage.getParameter());
    }

    @Test
    void toController() throws IOException, InterruptedException, ClassNotFoundException {
        // Act
        view.setNickname("nickname");
        view.toController(Choice.CHOOSE_TILE, mock(Tile.class));

        // Assert
        Thread.sleep(200);
        Message message = (Message) proxyIn.readObject();
        assertEquals(MessageType.CHOOSE, message.getType());
        ClientChoiceMessage clientChoiceMessage = (ClientChoiceMessage) message;
        assertEquals(Choice.CHOOSE_TILE, clientChoiceMessage.getChoice());
        assertEquals("nickname", clientChoiceMessage.getIdentifier());
    }

    @Test
    void setController() {
        view.setController(mock(IF_GameController.class));
        verifyNoInteractions(mockUI);
    }
}