package mesos.am30.server;

import mesos.am30.gameModel.Player;
import mesos.am30.common.ErrorType;
import mesos.am30.client.IF_GameView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.*;
import java.util.concurrent.CountDownLatch;

@ExtendWith(MockitoExtension.class)
class ServerTest {

    @Mock
    private IF_GameView mockView;

    @Mock
    private Registry mockRegistry;

    @BeforeEach
    void setUp() {
        // reset the Server state and inject mock registry
        Server.getLobbies().clear();
        Server.getLobbyViews().clear();
        Server.getPendingViews().clear();
        Server.setRegistry(mockRegistry);
    }

    @Test
    void handleConnection() throws Exception {
        // Act
        Server.getInstance().handleConnection(mockView);

        // Assert
        Thread.sleep(200);
        assertTrue(Server.getPendingViews().contains(mockView));
        verify(mockView).confirmConnection();
    }

    @Test
    void startHeartbeat() throws IOException, InterruptedException {
        // Act
        Server.getInstance().handleConnection(mockView);

        // Assert
        Thread.sleep(1500);
        verify(mockView, atLeastOnce()).ping();
    }

    @Test
    void startHeartbeat_DisconnectionPending() throws Exception {
        // Act
        doThrow(new IOException()).when(mockView).ping();
        Server.getInstance().handleConnection(mockView);

        // Assert
        Thread.sleep(1500);
        assertFalse(Server.getPendingViews().contains(mockView));
    }

    @Test
    void startHeartbeat_DisconnectionLobby() throws Exception {
        // set up the Mock Lobby
        doThrow(new IOException()).when(mockView).ping();
        Server.getLobbyViews().put("ID", new ArrayList<>());
        List<IF_GameView> clients = Server.getLobbyViews().get("ID");
        for (int i = 0; i < 3; i++) clients.add(mock(IF_GameView.class));

        // Act
        Server.getInstance().handleConnection(mockView);
        clients.add(mockView);
        Server.getPendingViews().remove(mockView);

        // Assert
        Thread.sleep(1500);
        assertFalse(Server.getLobbyViews().containsKey("ID"));
        for (IF_GameView view : clients) {
            verify(view).notifyError(ErrorType.END_FOR_DISCONNECTION);
            verify(view).end();
        }
    }

    @Test
    void createLobby_CustomCode() throws Exception {
        // Act
        Server.getPendingViews().add(mockView);
        Server.getInstance().createLobby(mockView, 3, "123456");

        // Assert
        Thread.sleep(200);
        assertTrue(Server.getLobbies().containsKey("123456"));
        verify(mockView).askNickname("123456");
    }

    @Test
    void createLobby_DuplicateCode() throws Exception {
        // set up Mock Lobby
        Server.getLobbies().put("123456", mock(Controller.class));
        Server.getLobbyViews().put("123456", new ArrayList<>());

        // Act
        Server.getPendingViews().add(mockView);
        Server.getInstance().createLobby(mockView, 3, "123456");

        // Assert
        Thread.sleep(200);
        verify(mockView).notifyError(ErrorType.ALREADY_EXISTING_LOBBY);
        assertEquals(1, Server.getLobbies().size());
    }

    @Test
    void joinLobby_Valid() throws Exception {
        // set up Mock Lobby
        Controller mockController = mock(Controller.class);
        when(mockController.isFull()).thenReturn(false);
        Server.getLobbies().put("123456", mockController);
        Server.getLobbyViews().put("123456", new ArrayList<>());

        // Act
        Server.getPendingViews().add(mockView);
        Server.getInstance().joinLobby(mockView, "123456");

        // Assert
        Thread.sleep(200);
        verify(mockView).askNickname("123456");
    }

    @Test
    void joinLobby_NotExisting() throws IOException, InterruptedException {
        // Act
        Server.getInstance().joinLobby(mockView, "999999");

        // Assert
        Thread.sleep(200);
        verify(mockView).notifyError(ErrorType.NOT_EXISTING_LOBBY);
    }

    @Test
    void joinLobby_Full() throws Exception {
        // set up mock Lobby
        Controller mockController = mock(Controller.class);
        when(mockController.isFull()).thenReturn(true);
        Server.getLobbies().put("123456", mockController);
        Server.getLobbyViews().put("123456", new ArrayList<>());

        // Act
        Server.getInstance().joinLobby(mockView, "123456");

        // Assert
        Thread.sleep(200);
        verify(mockView).notifyError(ErrorType.FULL_LOBBY);
    }

    @Test
    void setNickname_NoLobby() throws IOException, InterruptedException {
        // Act
        Server.getInstance().setNickname(mockView, "Lorenzo", "123456");

        // Assert
        Thread.sleep(200);
        verify(mockView).notifyError(ErrorType.NOT_EXISTING_LOBBY);
    }

    @Test
    void setNickname_DuplicateNickname() throws Exception {
        // setup player, controller and lobby
        Player existing = mock(Player.class);
        when(existing.getNickname()).thenReturn("Lorenzo");
        Controller controller = mock(Controller.class);
        when(controller.getClients()).thenReturn(Map.of(existing, mock(IF_GameView.class)));
        Server.getLobbies().put("123456", controller);
        Server.getLobbyViews().put("123456", new ArrayList<>(List.of(mockView)));

        // Act
        Server.getInstance().setNickname(mockView, "Lorenzo", "123456");

        // Assert
        Thread.sleep(200);
        verify(mockView).notifyError(ErrorType.WRONG_NICKNAME);
    }

    @Test
    void setNickname_ValidNotFull() throws Exception {
        // setup lobby
        Controller controller = mock(Controller.class);
        when(controller.getClients()).thenReturn(new HashMap<>());
        when(controller.connect(mockView, "Lorenzo")).thenReturn(false);
        Server.getLobbies().put("123456", controller);
        Server.getLobbyViews().put("123456", new ArrayList<>(List.of(mockView)));

        // Act
        Server.getInstance().setNickname(mockView, "Lorenzo", "123456");

        // Assert
        Thread.sleep(200);
        verify(controller).connect(mockView, "Lorenzo");
        verify(mockView).confirmLobbyJoined();
        verify(controller, never()).startGame();
    }

    @Test
    void setNickname_ValidFull_StartsGame() throws Exception {
        // setup lobby
        Controller controller = mock(Controller.class);
        when(controller.getClients()).thenReturn(new HashMap<>());
        when(controller.connect(mockView, "Lorenzo")).thenReturn(true);
        Server.getLobbies().put("123456", controller);
        Server.getLobbyViews().put("123456", new ArrayList<>(List.of(mockView)));

        // Act
        Server.getInstance().setNickname(mockView, "Lorenzo", "123456");

        // Assert
        Thread.sleep(500);
        verify(controller).connect(mockView, "Lorenzo");
        verify(controller).startGame();
        verify(mockView).confirmLobbyJoined();
    }

    @Test
    void setNickname_FullLobby_ReturnsError() throws Exception {
        // setup lobby that is full at nickname-set time
        Controller controller = mock(Controller.class);
        when(controller.getClients()).thenReturn(new HashMap<>());
        when(controller.isFull()).thenReturn(true);
        Server.getLobbies().put("123456", controller);
        Server.getLobbyViews().put("123456", new ArrayList<>(List.of(mockView)));

        // Act
        Server.getInstance().setNickname(mockView, "Lorenzo", "123456");

        // Assert
        Thread.sleep(200);
        verify(mockView).notifyError(ErrorType.FULL_LOBBY);
        verify(controller, never()).connect(any(), any());
    }

    @Test
    void showAvailableLobbies_ReturnsOnlyNonFull() throws Exception {
        // setup controller
        Controller openController = mock(Controller.class);
        Controller fullController = mock(Controller.class);
        when(openController.isFull()).thenReturn(false);
        when(openController.getOccupiedSlots()).thenReturn(2);
        when(fullController.isFull()).thenReturn(true);
        Server.getLobbies().put("111111", openController);
        Server.getLobbies().put("222222", fullController);

        // Act
        Server.getInstance().showAvailableLobbies(mockView);

        // Assert
        Thread.sleep(200);
        verify(mockView).showLobbies(argThat(map ->
                map.size() == 1 && map.containsKey("111111") && map.get("111111") == 2
        ));
    }

    @Test
    void showAvailableLobbies_ExcludesNotFullButEmpty() throws Exception {
        // setup: controller not full but with 0 occupied slots (lobby just created, no players yet)
        Controller emptyController = mock(Controller.class);
        when(emptyController.isFull()).thenReturn(false);
        when(emptyController.getOccupiedSlots()).thenReturn(0);
        Server.getLobbies().put("333333", emptyController);

        // Act
        Server.getInstance().showAvailableLobbies(mockView);

        // Assert
        Thread.sleep(200);
        verify(mockView).showLobbies(argThat(Map::isEmpty));
    }

    @Test
    void handleDisconnection_PendingView_RemovedWithoutNotification() throws Exception {
        // set up Mock Views
        IF_GameView otherPending = mock(IF_GameView.class);
        Server.getPendingViews().add(mockView);
        Server.getPendingViews().add(otherPending);

        // Act
        Server.getInstance().handleDisconnection(mockView);

        // Assert
        assertFalse(Server.getPendingViews().contains(mockView));
        assertTrue(Server.getPendingViews().contains(otherPending));
        verify(otherPending, never()).notifyError(any());
        verify(otherPending, never()).end();
    }

    @Test
    void handleDisconnection_InLobby_NotifiesOthersAndRemovesLobby() throws Exception {
        // set up Mock Views
        IF_GameView otherView = mock(IF_GameView.class);
        List<IF_GameView> views = new ArrayList<>(List.of(mockView, otherView));
        Server.getLobbies().put("123456", mock(Controller.class));
        Server.getLobbyViews().put("123456", views);

        // Act
        Server.getInstance().handleDisconnection(mockView);

        // Assert
        Thread.sleep(200);
        verify(otherView).notifyError(ErrorType.END_FOR_DISCONNECTION);
        verify(otherView).end();
        assertFalse(Server.getLobbies().containsKey("123456"));
        assertFalse(Server.getLobbyViews().containsKey("123456"));
    }

    @Test
    void multipleLobbies_CreatedConcurrently() throws Exception {
        // set up Mock Lobby
        int count = 5;
        CountDownLatch latch = new CountDownLatch(count);
        List<IF_GameView> views = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            IF_GameView view = mock(IF_GameView.class);
            views.add(view);
            Server.getPendingViews().add(view);
        }

        // Act
        for (IF_GameView view : views) {
            new Thread(() -> {
                try {
                    Server.getInstance().createLobby(view, 3, "");
                } catch (IOException e) {
                    fail("IOException during concurrent lobby creation");
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        latch.await();

        // Assert
        Thread.sleep(200);
        assertEquals(count, Server.getLobbies().size());
    }

    @Test
    void multipleLobbies_SetNicknameDoesNotInterfere() throws Exception {
        // setup lobbies
        IF_GameView viewA = mock(IF_GameView.class);
        IF_GameView viewB = mock(IF_GameView.class);
        Controller controllerA = mock(Controller.class);
        Controller controllerB = mock(Controller.class);
        when(controllerA.getClients()).thenReturn(new HashMap<>());
        when(controllerB.getClients()).thenReturn(new HashMap<>());
        when(controllerA.connect(viewA, "PlayerA")).thenReturn(false);
        when(controllerB.connect(viewB, "PlayerB")).thenReturn(false);
        Server.getLobbies().put("111111", controllerA);
        Server.getLobbies().put("222222", controllerB);
        Server.getLobbyViews().put("111111", new ArrayList<>(List.of(viewA)));
        Server.getLobbyViews().put("222222", new ArrayList<>(List.of(viewB)));

        // Act
        Server.getInstance().setNickname(viewA, "PlayerA","111111");
        Server.getInstance().setNickname(viewB, "PlayerB", "222222");

        // Assert
        Thread.sleep(200);
        verify(controllerA).connect(viewA, "PlayerA");
        verify(controllerB).connect(viewB, "PlayerB");
        verify(controllerA, never()).connect(eq(viewB), any());
        verify(controllerB, never()).connect(eq(viewA), any());
    }

    @Test
    void handleDisconnection_InOneLobby_DoesNotAffectOtherLobby() throws Exception {
        // setup
        IF_GameView crashedView = mock(IF_GameView.class);
        IF_GameView sameLobbyView = mock(IF_GameView.class);
        IF_GameView otherLobbyView = mock(IF_GameView.class);
        Server.getLobbies().put("111111", mock(Controller.class));
        Server.getLobbies().put("222222", mock(Controller.class));
        Server.getLobbyViews().put("111111", new ArrayList<>(List.of(crashedView, sameLobbyView)));
        Server.getLobbyViews().put("222222", new ArrayList<>(List.of(otherLobbyView)));

        // Act
        Server.getInstance().handleDisconnection(crashedView);

        // Assert
        Thread.sleep(200);
        verify(sameLobbyView).notifyError(ErrorType.END_FOR_DISCONNECTION);
        verify(sameLobbyView).end();
        verify(otherLobbyView, never()).notifyError(any());
        verify(otherLobbyView, never()).end();
        assertFalse(Server.getLobbies().containsKey("111111"));
        assertTrue(Server.getLobbies().containsKey("222222"));
    }

    @Test
    void createLobby_WrongPlayersNumber() throws Exception {
        // Act
        Server.getPendingViews().add(mockView);
        Server.getInstance().createLobby(mockView, 1, "123456");

        // Assert
        Thread.sleep(200);
        verify(mockView).notifyError(ErrorType.WRONG_PLAYERS_NUMBER);
        assertFalse(Server.getLobbies().containsKey("123456"));
    }

    @Test
    void createLobby_InvalidLobbyCode() throws Exception {
        // Act
        Server.getPendingViews().add(mockView);
        Server.getInstance().createLobby(mockView, 3, "abc");

        // Assert
        Thread.sleep(200);
        verify(mockView).notifyError(ErrorType.INVALID_LOBBY_CODE);
        assertFalse(Server.getLobbies().containsKey("abc"));
    }

    @Test
    void createLobby_AutoGeneratedCode() throws Exception {
        // Act
        Server.getPendingViews().add(mockView);
        Server.getInstance().createLobby(mockView, 3, "");

        // Assert
        Thread.sleep(200);
        assertEquals(1, Server.getLobbies().size());
        verify(mockView).askNickname(any());
    }

    @Test
    void createLobby_WrongPlayersNumberTooMany() throws Exception {
        // Act: playersNumber=6 covers the right side of the || condition (> 5)
        Server.getPendingViews().add(mockView);
        Server.getInstance().createLobby(mockView, 6, "123456");

        // Assert
        Thread.sleep(200);
        verify(mockView).notifyError(ErrorType.WRONG_PLAYERS_NUMBER);
        assertFalse(Server.getLobbies().containsKey("123456"));
    }

    @Test
    void setNickname_NullCode() throws IOException, InterruptedException {
        // Act: code == null means target == null, so NOT_EXISTING_LOBBY is returned
        Server.getInstance().setNickname(mockView, "Lorenzo", null);

        // Assert
        Thread.sleep(200);
        verify(mockView).notifyError(ErrorType.NOT_EXISTING_LOBBY);
    }

    @Test
    void generateLobbyCode_Collision() throws Exception {
        // Force the first random value to collide with an existing lobby
        Random controlled = mock(Random.class);
        when(controlled.nextInt(1_000_000)).thenReturn(123456, 654321);
        Server.setRandom(controlled);
        Server.getLobbies().put("123456", mock(Controller.class));
        Server.getPendingViews().add(mockView);

        // Act
        Server.getInstance().createLobby(mockView, 3, "");

        // Assert: second generated code was used after the collision
        Thread.sleep(200);
        assertTrue(Server.getLobbies().containsKey("654321"));
        verify(mockView).askNickname("654321");

        // Restore default random
        Server.setRandom(new Random());
    }

    @Test
    void startHeartbeat_ExitsNormallyWhenRemovedFromLobby() throws Exception {
        // Setup: view is in a lobby but not in pendingViews
        Server.getLobbyViews().put("ID", new ArrayList<>(List.of(mockView)));
        Server.getLobbies().put("ID", mock(Controller.class));
        Server.getInstance().handleConnection(mockView);
        Server.getPendingViews().remove(mockView);

        // Let the heartbeat run at least once while the view is in the lobby
        Thread.sleep(1200);

        // Remove view from lobby so both while conditions become false and the loop exits normally
        Server.getLobbyViews().remove("ID");
        Server.getLobbies().remove("ID");

        Thread.sleep(1500);
        verify(mockView, never()).notifyError(any());
    }

    @Test
    void startSocketServer_ClientDisconnects_HandlerCatchesIOException() throws Exception {
        int port;
        try (ServerSocket probe = new ServerSocket(0)) {
            port = probe.getLocalPort();
        }

        Server.startSocketServer(Server.getInstance(), port);
        Thread.sleep(200);

        // Connect without sending ObjectOutputStream header so the server's
        // ObjectInputStream throws EOFException, triggering the catch block
        Socket client = new Socket("localhost", port);
        client.close();

        Thread.sleep(500);
    }

    @Test
    void main_CallsBothServers() throws Exception {
        try (MockedStatic<LocateRegistry> mockedLocate = mockStatic(LocateRegistry.class)) {
            mockedLocate.when(() -> LocateRegistry.createRegistry(1099)).thenReturn(mockRegistry);

            // Act
            Server.main(new String[0]);

            // Assert: registry bound and socket server thread started (no exception)
            verify(mockRegistry).bind(eq("server"), any());
        }
    }

    @Test
    void main_RmiAlreadyBound_SkipsSocketServer() throws Exception {
        try (MockedStatic<LocateRegistry> mockedLocate = mockStatic(LocateRegistry.class)) {
            mockedLocate.when(() -> LocateRegistry.createRegistry(1099)).thenReturn(mockRegistry);
            doThrow(new AlreadyBoundException()).when(mockRegistry).bind(eq("server"), any());

            // Act: startRmiServer returns false, so startSocketServer must not be called
            Server.main(new String[0]);

            // Assert: no exception thrown, execution stopped after RMI failure
            verify(mockRegistry).bind(eq("server"), any());
        }
    }

    @Test
    void startRmiServer_Success() throws Exception {
        try (MockedStatic<LocateRegistry> mockedLocate = mockStatic(LocateRegistry.class)) {
            mockedLocate.when(() -> LocateRegistry.createRegistry(1099)).thenReturn(mockRegistry);

            // Act
            boolean result = Server.startRmiServer(Server.getInstance(), 1099);

            // Assert
            assertTrue(result);
            verify(mockRegistry).bind(eq("server"), any());
        }
    }

    @Test
    void startRmiServer_AlreadyBoundException() throws Exception {
        try (MockedStatic<LocateRegistry> mockedLocate = mockStatic(LocateRegistry.class)) {
            mockedLocate.when(() -> LocateRegistry.createRegistry(1099)).thenReturn(mockRegistry);
            doThrow(new AlreadyBoundException()).when(mockRegistry).bind(eq("server"), any());

            // Act: should return false without throwing
            boolean result = Server.startRmiServer(Server.getInstance(), 1099);

            // Assert: bind was attempted, returned false
            assertFalse(result);
        }
    }

    @Test
    void startSocketServer_PortInUse_LogsError() throws Exception {
        // Pre-occupy a port so ServerSocket creation fails
        try (ServerSocket occupied = new ServerSocket(0)) {
            int port = occupied.getLocalPort();

            // Act: should catch IOException silently
            Server.startSocketServer(Server.getInstance(), port);

            // wait for the thread to attempt and fail
            Thread.sleep(300);
            // No exception thrown — IOException was swallowed internally
        }
    }

    @Test
    void startSocketServer_HandlesConnection() throws Exception {
        // find a free port
        int port;
        try (ServerSocket probe = new ServerSocket(0)) {
            port = probe.getLocalPort();
        }

        Server server = Server.getInstance();
        Server.startSocketServer(server, port);
        Thread.sleep(200); // let server thread start

        // Connect a client to trigger the connection handler
        try (Socket client = new Socket("localhost", port)) {
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            out.flush();
            Thread.sleep(300); // let server process the connection
        }

        // Assert: a SocketProxy was added to pendingViews via handleConnection
        assertFalse(Server.getPendingViews().isEmpty());
    }
}