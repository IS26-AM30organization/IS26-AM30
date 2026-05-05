package mesos.am30.client;

import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.EventCard;
import mesos.am30.gameModel.Player;
import mesos.am30.common.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

@ExtendWith(MockitoExtension.class)
class SocketViewTest {
    private SocketView view;
    private ServerSocket serverSocket;
    private Socket proxySocket;
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
    void findServer_Found() {
        // Act
        new Thread(() -> {
            try {
                view.findServer("localhost", serverSocket.getLocalPort());
            } catch (IOException ignored) { /* test will not throw IOException */ }
        }).start();

        // Assert
        verify(mockUI, never()).printError(ErrorType.WRONG_IP);
        verify(mockUI, never()).printEnd();
    }

    @Test
    void findServer_NotFound() throws IOException {
        // Act
        view.findServer("", 0);

        // Assert
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
    void startListeningThread_NICKNAME() throws IOException, InterruptedException {
        // Act
        view.startListeningThread();
        proxyOut.writeObject(new Message(MessageType.NICKNAME));
        proxyOut.flush();

        // Assert
        Thread.sleep(200);
        verify(mockUI).askNickname();
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
}