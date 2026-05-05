package mesos.am30.server;

import mesos.am30.gameModel.Player;
import mesos.am30.common.ErrorType;
import mesos.am30.client.IF_GameView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import java.io.IOException;

import java.util.*;
import java.util.concurrent.CountDownLatch;

@ExtendWith(MockitoExtension.class)
class ServerTest {

    @Mock
    private IF_GameView mockView;

    @BeforeEach
    void setUp() {
        // reset the Server
        Server.getLobbies().clear();
        Server.getLobbyViews().clear();
        Server.getPendingViews().clear();
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
        verify(mockView).askNickname();
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
        Server.getLobbies().put("123456", mockController);
        Server.getLobbyViews().put("123456", new ArrayList<>());

        // Act
        Server.getPendingViews().add(mockView);
        Server.getInstance().joinLobby(mockView, "123456");

        // Assert
        Thread.sleep(200);
        verify(mockView).askNickname();
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
        IF_GameView view1 = mock(IF_GameView.class);
        IF_GameView view2 = mock(IF_GameView.class);
        Server.getLobbies().put("123456", mockController);
        Server.getLobbyViews().put("123456", new ArrayList<>(List.of(view1, view2)));
        when(mockController.isFull()).thenReturn(2 == Server.getLobbyViews().get("123456").size());

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
        verify(mockView, never()).confirmLobbyJoined();
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
}