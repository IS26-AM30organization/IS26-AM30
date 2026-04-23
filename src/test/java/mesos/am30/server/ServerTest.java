package mesos.am30.server;

import mesos.am30.GameModel.Player;
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

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ServerTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private IF_GameView mockView;

    @BeforeEach
    void setUp() {
        // reset the lobby
        Server.setLobby(null);
    }

    @Test
    void handleConnection_First_Player() throws IOException {
        // Act
        Server.getInstance().handleConnection(mockView);

        // Assert
        assertTrue(Server.getConnectedViews().contains(mockView));
        verify(mockView).askPlayersNumber();
    }

    @Test
    void handleConnection_Not_First_Player() throws IOException {
        // Act
        Server.setLobby(new Controller(4));
        Server.getInstance().handleConnection(mockView);

        // Assert
        assertTrue(Server.getConnectedViews().contains(mockView));
        verify(mockView).askNickname();
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
    void handleDisconnection() throws IOException, InterruptedException {
        // set up disconnected View
        doThrow(new IOException()).when(mockView).ping();
        List<IF_GameView> clients = Server.getConnectedViews();
        for (int i = 0; i < 3; i++) clients.add(mock(IF_GameView.class));

        // Act
        Server.getInstance().handleConnection(mockView);

        // Assert
        Thread.sleep(1500);
        assertThrows(IOException.class, () -> mockView.ping());
        assertFalse(clients.contains(mockView));
        for (IF_GameView view : clients) {
            verify(view).notifyError(ErrorType.END_FOR_DISCONNECTION);
            verify(view).end();
        }
        assertTrue(Server.getConnectedViews().isEmpty());
        assertNull(Server.getLobby());
    }

    @Test
    void setPlayersNumber_New_Lobby() throws IOException {
        // Act
        Server.getInstance().setPlayersNumber(mockView, 4);

        // Assert
        assertNotNull(Server.getLobby());
        Controller controller = Server.getLobby();
        assertEquals(4, controller.getPlayersNumber());
        assertEquals(0, controller.getClients().size());
        verify(mockView).askNickname();
    }

    @Test
    void setPlayersNumber_Already_Existing_Lobby() throws IOException {
        // Act
        Server.setLobby(new Controller(2));
        Server.getInstance().setPlayersNumber(mockView, 4);

        // Assert
        assertNotNull(Server.getLobby());
        Controller controller = Server.getLobby();
        assertEquals(2, controller.getPlayersNumber());
        assertEquals(0, controller.getClients().size());
        verify(mockView).notifyError(ErrorType.ALREADY_EXISTING_LOBBY);
    }

    @Test
    void setPlayersNumber_Low_PlayersNumber() throws IOException {
        // Act
        Server.getInstance().setPlayersNumber(mockView, 1);

        // Assert
        assertNull(Server.getLobby());
        verify(mockView).notifyError(ErrorType.WRONG_PLAYERS_NUMBER);
    }

    @Test
    void setPlayersNumber_High_PlayersNumber() throws IOException {
        // Act
        Server.getInstance().setPlayersNumber(mockView, 6);

        // Assert
        assertNull(Server.getLobby());
        verify(mockView).notifyError(ErrorType.WRONG_PLAYERS_NUMBER);
    }

    @Test
    void setNickname_No_Lobby() throws IOException {
        // Act
        Server.getInstance().setNickname(mockView, "nickname");

        // Assert
        assertNull(Server.getLobby());
        verify(mockView).notifyError(ErrorType.NOT_EXISTING_LOBBY);
    }

    @Test
    void setNickname_Already_Full_Lobby() throws IOException {
        // set up lobby
        Server.setLobby(new Controller(0));

        // Act
        Server.getInstance().setNickname(mockView, "nickname");

        // Assert
        Controller controller = Server.getLobby();
        assertEquals(0, controller.getPlayersNumber());
        assertEquals(0, controller.getClients().size());
        verify(mockView).notifyError(ErrorType.FULL_LOBBY);
    }

    @Test
    void setNickname_New_Nickname() throws IOException {
        // Act
        Server.setLobby(new Controller(2));
        Server.getInstance().setNickname(mockView, "nickname");

        // Assert
        assertNotNull(Server.getLobby());
        Controller controller = Server.getLobby();
        assertEquals(2, controller.getPlayersNumber());
        assertEquals(1, controller.getClients().size());
        assertTrue(controller.getClients().containsValue(mockView));
        verify(mockView).setController(controller);
    }

    @Test
    void setNickname_Already_Existing_Nickname() throws IOException {
        // set up lobby
        when(mockPlayer.getNickname()).thenReturn("nickname1");
        Server.setLobby(new Controller(3));
        Controller controller = Server.getLobby();
        controller.getClients().put(mockPlayer, mockView);

        // Act
        Server.getInstance().setNickname(mockView, "nickname2");
        Server.getInstance().setNickname(mockView, "nickname1");

        // Assert
        assertEquals(2, controller.getClients().size());
        List<String> existingNicknames = controller.getClients().keySet().stream()
                .map(Player::getNickname)
                .toList();
        assertEquals(2, existingNicknames.size());
        assertTrue(existingNicknames.contains("nickname1"));
        assertTrue(existingNicknames.contains("nickname2"));
    }
}