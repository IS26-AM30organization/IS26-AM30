package mesos.am30.server;

import mesos.am30.gameModel.Player;
import mesos.am30.common.enumerations.ErrorType;
import mesos.am30.common.interfaces.IF_GameView;

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
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.*;
import java.util.concurrent.CountDownLatch;

@ExtendWith(MockitoExtension.class)
class ServerTest {
    Server server;

    @Mock
    private IF_GameView mockView;

    @Mock
    private Registry mockRegistry;

    @BeforeEach
    void setUp() throws RemoteException {
        // reset the Server state and inject mock registry
        server = Server.getInstance();
        server.getLobbies().clear();
        server.getLobbyViews().clear();
        server.getPendingViews().clear();
        server.setRegistry(mockRegistry);
    }

    @Test
    void handleConnection() throws Exception {
        // Act
        server.handleConnection(mockView);

        // Assert
        Thread.sleep(200);
        assertTrue(server.getPendingViews().contains(mockView));
        verify(mockView).confirmConnection();
    }

    @Test
    void startHeartbeat() throws IOException, InterruptedException {
        // Act
        server.handleConnection(mockView);

        // Assert
        Thread.sleep(1500);
        verify(mockView, atLeastOnce()).ping();
    }

    @Test
    void startHeartbeat_DisconnectionPending() throws Exception {
        // Act
        doThrow(new IOException()).when(mockView).ping();
        server.handleConnection(mockView);

        // Assert
        Thread.sleep(1500);
        assertFalse(server.getPendingViews().contains(mockView));
    }

    @Test
    void startHeartbeat_DisconnectionLobby() throws Exception {
        // set up the Mock Lobby
        doThrow(new IOException()).when(mockView).ping();
        server.getLobbyViews().put("ID", new ArrayList<>());
        List<IF_GameView> clients = server.getLobbyViews().get("ID");
        for (int i = 0; i < 3; i++) clients.add(mock(IF_GameView.class));

        // Act
        server.handleConnection(mockView);
        clients.add(mockView);
        server.getPendingViews().remove(mockView);

        // Assert
        Thread.sleep(1500);
        assertFalse(server.getLobbyViews().containsKey("ID"));
        for (IF_GameView view : clients) {
            verify(view).notifyError(ErrorType.END_FOR_DISCONNECTION);
            verify(view).end();
        }
    }

    @Test
    void createLobby_CustomCode() throws Exception {
        // Act
        server.getPendingViews().add(mockView);
        server.createLobby(mockView, 3, "123456");

        // Assert
        Thread.sleep(200);
        assertTrue(server.getLobbies().containsKey("123456"));
        verify(mockView).askNickname("123456");
    }

    @Test
    void createLobby_DuplicateCode() throws Exception {
        // set up Mock Lobby
        server.getLobbies().put("123456", mock(Controller.class));
        server.getLobbyViews().put("123456", new ArrayList<>());

        // Act
        server.getPendingViews().add(mockView);
        server.createLobby(mockView, 3, "123456");

        // Assert
        Thread.sleep(200);
        verify(mockView).notifyError(ErrorType.ALREADY_EXISTING_LOBBY);
        assertEquals(1, server.getLobbies().size());
    }

    @Test
    void joinLobby_Valid() throws Exception {
        // set up Mock Lobby
        Controller mockController = mock(Controller.class);
        when(mockController.isFull()).thenReturn(false);
        server.getLobbies().put("123456", mockController);
        server.getLobbyViews().put("123456", new ArrayList<>());

        // Act
        server.getPendingViews().add(mockView);
        server.joinLobby(mockView, "123456");

        // Assert
        Thread.sleep(200);
        verify(mockView).askNickname("123456");
    }

    @Test
    void joinLobby_NotExisting() throws IOException, InterruptedException {
        // Act
        server.joinLobby(mockView, "999999");

        // Assert
        Thread.sleep(200);
        verify(mockView).notifyError(ErrorType.NOT_EXISTING_LOBBY);
    }

    @Test
    void joinLobby_Full() throws Exception {
        // set up mock Lobby
        Controller mockController = mock(Controller.class);
        when(mockController.isFull()).thenReturn(true);
        server.getLobbies().put("123456", mockController);
        server.getLobbyViews().put("123456", new ArrayList<>());

        // Act
        server.joinLobby(mockView, "123456");

        // Assert
        Thread.sleep(200);
        verify(mockView).notifyError(ErrorType.FULL_LOBBY);
    }

    @Test
    void setNickname_NoLobby() throws IOException, InterruptedException {
        // Act
        server.setNickname(mockView, "Lorenzo", "123456");

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
        server.getLobbies().put("123456", controller);
        server.getLobbyViews().put("123456", new ArrayList<>(List.of(mockView)));

        // Act
        server.setNickname(mockView, "Lorenzo", "123456");

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
        server.getLobbies().put("123456", controller);
        server.getLobbyViews().put("123456", new ArrayList<>(List.of(mockView)));

        // Act
        server.setNickname(mockView, "Lorenzo", "123456");

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
        server.getLobbies().put("123456", controller);
        server.getLobbyViews().put("123456", new ArrayList<>(List.of(mockView)));

        // Act
        server.setNickname(mockView, "Lorenzo", "123456");

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
        server.getLobbies().put("123456", controller);
        server.getLobbyViews().put("123456", new ArrayList<>(List.of(mockView)));

        // Act
        server.setNickname(mockView, "Lorenzo", "123456");

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
        server.getLobbies().put("111111", openController);
        server.getLobbies().put("222222", fullController);

        // Act
        server.showAvailableLobbies(mockView);

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
        server.getLobbies().put("333333", emptyController);

        // Act
        server.showAvailableLobbies(mockView);

        // Assert
        Thread.sleep(200);
        verify(mockView).showLobbies(argThat(Map::isEmpty));
    }

    @Test
    void ping() throws IOException {
        server.ping();
        assertTrue(server.getLobbies().isEmpty());
        assertTrue(server.getLobbyViews().isEmpty());
        assertTrue(server.getPendingViews().isEmpty());
        verifyNoInteractions(mockView);
        verifyNoInteractions(mockRegistry);
    }

    @Test
    void disconnectPlayerGracefully_NoLobby() {
        server.disconnectPlayerGracefully(mockView);
        assertTrue(server.getLobbyViews().isEmpty());
    }

    @Test
    void disconnectPlayerGracefully_ExistingLobby() throws IOException {
        // set up Mock Lobby
        server.createLobby(mockView, 3,"123456");
        server.setNickname(mockView, "view1", "123456");
        server.setNickname(mockView, "view2", "123456");

        // Act - disconnect first View
        server.disconnectPlayerGracefully(mockView);
        assertFalse(server.getLobbyViews().isEmpty());
        assertFalse(server.getLobbies().isEmpty());
        assertTrue(server.getLobbyViews().get("123456").contains(mockView));

        // Act - disconnect second View
        server.disconnectPlayerGracefully(mockView);
        assertTrue(server.getLobbyViews().isEmpty());
        assertTrue(server.getLobbies().isEmpty());
    }

    @Test
    void handleDisconnection_PendingView_RemovedWithoutNotification() throws Exception {
        // set up Mock Views
        IF_GameView otherPending = mock(IF_GameView.class);
        server.getPendingViews().add(mockView);
        server.getPendingViews().add(otherPending);

        // Act
        server.handleDisconnection(mockView);

        // Assert
        assertFalse(server.getPendingViews().contains(mockView));
        assertTrue(server.getPendingViews().contains(otherPending));
        verify(otherPending, never()).notifyError(any());
        verify(otherPending, never()).end();
    }

    @Test
    void handleDisconnection_InLobby_NotifiesOthersAndRemovesLobby() throws Exception {
        // set up Mock Views
        IF_GameView otherView = mock(IF_GameView.class);
        List<IF_GameView> views = new ArrayList<>(List.of(mockView, otherView));
        server.getLobbies().put("123456", mock(Controller.class));
        server.getLobbyViews().put("123456", views);

        // Act
        server.handleDisconnection(mockView);

        // Assert
        Thread.sleep(200);
        verify(otherView).notifyError(ErrorType.END_FOR_DISCONNECTION);
        verify(otherView).end();
        assertFalse(server.getLobbies().containsKey("123456"));
        assertFalse(server.getLobbyViews().containsKey("123456"));
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
            server.getPendingViews().add(view);
        }

        // Act
        for (IF_GameView view : views) {
            new Thread(() -> {
                try {
                    server.createLobby(view, 3, "");
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
        assertEquals(count, server.getLobbies().size());
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
        server.getLobbies().put("111111", controllerA);
        server.getLobbies().put("222222", controllerB);
        server.getLobbyViews().put("111111", new ArrayList<>(List.of(viewA)));
        server.getLobbyViews().put("222222", new ArrayList<>(List.of(viewB)));

        // Act
        server.setNickname(viewA, "PlayerA","111111");
        server.setNickname(viewB, "PlayerB", "222222");

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
        server.getLobbies().put("111111", mock(Controller.class));
        server.getLobbies().put("222222", mock(Controller.class));
        server.getLobbyViews().put("111111", new ArrayList<>(List.of(crashedView, sameLobbyView)));
        server.getLobbyViews().put("222222", new ArrayList<>(List.of(otherLobbyView)));

        // Act
        server.handleDisconnection(crashedView);

        // Assert
        Thread.sleep(200);
        verify(sameLobbyView).notifyError(ErrorType.END_FOR_DISCONNECTION);
        verify(sameLobbyView).end();
        verify(otherLobbyView, never()).notifyError(any());
        verify(otherLobbyView, never()).end();
        assertFalse(server.getLobbies().containsKey("111111"));
        assertTrue(server.getLobbies().containsKey("222222"));
    }

    @Test
    void createLobby_WrongPlayersNumber() throws Exception {
        // Act
        server.getPendingViews().add(mockView);
        server.createLobby(mockView, 1, "123456");

        // Assert
        Thread.sleep(200);
        verify(mockView).notifyError(ErrorType.WRONG_PLAYERS_NUMBER);
        assertFalse(server.getLobbies().containsKey("123456"));
    }

    @Test
    void createLobby_InvalidLobbyCode() throws Exception {
        // Act
        server.getPendingViews().add(mockView);
        server.createLobby(mockView, 3, "abc");

        // Assert
        Thread.sleep(200);
        verify(mockView).notifyError(ErrorType.INVALID_LOBBY_CODE);
        assertFalse(server.getLobbies().containsKey("abc"));
    }

    @Test
    void createLobby_AutoGeneratedCode() throws Exception {
        // Act
        server.getPendingViews().add(mockView);
        server.createLobby(mockView, 3, "");

        // Assert
        Thread.sleep(200);
        assertEquals(1, server.getLobbies().size());
        verify(mockView).askNickname(any());
    }

    @Test
    void createLobby_WrongPlayersNumberTooMany() throws Exception {
        // Act: playersNumber=6 covers the right side of the || condition (> 5)
        server.getPendingViews().add(mockView);
        server.createLobby(mockView, 6, "123456");

        // Assert
        Thread.sleep(200);
        verify(mockView).notifyError(ErrorType.WRONG_PLAYERS_NUMBER);
        assertFalse(server.getLobbies().containsKey("123456"));
    }

    @Test
    void setNickname_NullCode() throws IOException, InterruptedException {
        // Act: code == null means target == null, so NOT_EXISTING_LOBBY is returned
        server.setNickname(mockView, "Lorenzo", null);

        // Assert
        Thread.sleep(200);
        verify(mockView).notifyError(ErrorType.NOT_EXISTING_LOBBY);
    }

    @Test
    void generateLobbyCode_Collision() throws Exception {
        // Force the first random value to collide with an existing lobby
        Random controlled = mock(Random.class);
        when(controlled.nextInt(1_000_000)).thenReturn(123456, 654321);
        server.setRandom(controlled);
        server.getLobbies().put("123456", mock(Controller.class));
        server.getPendingViews().add(mockView);

        // Act
        server.createLobby(mockView, 3, "");

        // Assert: second generated code was used after the collision
        Thread.sleep(200);
        assertTrue(server.getLobbies().containsKey("654321"));
        verify(mockView).askNickname("654321");

        // Restore default random
        server.setRandom(new Random());
    }

    @Test
    void startHeartbeat_ExitsNormallyWhenRemovedFromLobby() throws Exception {
        // Setup: view is in a lobby but not in pendingViews
        server.getLobbyViews().put("ID", new ArrayList<>(List.of(mockView)));
        server.getLobbies().put("ID", mock(Controller.class));
        server.handleConnection(mockView);
        server.getPendingViews().remove(mockView);

        // Let the heartbeat run at least once while the view is in the lobby
        Thread.sleep(1200);

        // Remove view from lobby so both while conditions become false and the loop exits normally
        server.getLobbyViews().remove("ID");
        server.getLobbies().remove("ID");

        Thread.sleep(1500);
        verify(mockView, never()).notifyError(any());
    }

    @Test
    void startSocketServer_ClientDisconnects_HandlerCatchesIOException() throws Exception {
        int port;
        try (ServerSocket probe = new ServerSocket(0)) {
            port = probe.getLocalPort();
        }

        Server.startSocketServer(server, port);
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
            String[] args = new String[1];
            args[0] = "localhost";
            Server.main(args);

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
            mockedLocate.when(() -> LocateRegistry.createRegistry(1098)).thenReturn(mockRegistry);

            // Act
            boolean result = Server.startRmiServer(server, 1098, "localhost");

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
            boolean result = Server.startRmiServer(server, 1099, "localhost");

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

        Server.startSocketServer(server, port);
        Thread.sleep(200); // let server thread start

        // Connect a client to trigger the connection handler
        try (Socket client = new Socket("localhost", port)) {
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            out.flush();
            Thread.sleep(300); // let server process the connection
        }

        // Assert: a SocketProxy was added to pendingViews via handleConnection
        assertFalse(server.getPendingViews().isEmpty());
    }
}