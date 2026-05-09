package mesos.am30.client;

import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.Tile;
import mesos.am30.common.Choice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import mesos.am30.common.ErrorType;
import mesos.am30.server.IF_Server;
import mesos.am30.server.IF_GameController;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@ExtendWith(MockitoExtension.class)
class RMIViewTest {

    private RMIView rmiView;
    @Mock
    private IF_GameUI mockUI;
    private Registry testRegistry;
    @Mock
    private IF_Server mockServer;
    @Mock
    private IF_GameController mockController;
    @Mock
    private Tile mockTile;
    @Mock
    private BuildingCard mockBuilding;
    @Mock
    private CharacterCard mockCharacter;

    @BeforeEach
    void setUp() throws Exception {
        rmiView = new RMIView(mockUI);
        rmiView.setRemoteServer(mockServer);
    }

    // free the port after each test, in order to avoid "Port already in use" error
    @AfterEach
    void tearDown() {
        try { UnicastRemoteObject.unexportObject(rmiView, true); } catch (NoSuchObjectException ignored) { /* not exported */ }
        try { UnicastRemoteObject.unexportObject(testRegistry, true); } catch (NoSuchObjectException ignored) { /* not exported */ }
    }

    @Test
    void findServer_Success() throws Exception {
        // Act
        IF_Server serverStub = (IF_Server) UnicastRemoteObject.exportObject(mockServer, 0);
        testRegistry = LocateRegistry.createRegistry(1099);
        testRegistry.rebind("server", serverStub);
        rmiView.findServer("localhost", 1099);

        // Assert
        verify(mockUI, never()).printError(ErrorType.WRONG_IP);
        verify(mockUI, never()).printEnd();
    }

    @Test
    void findServer_Fail() throws Exception {
        rmiView.findServer("localhost", 1099);

        verify(mockUI, times(1)).printError(ErrorType.WRONG_IP);
    }

    @Test
    void toController_RoutesChooseTileCorrectly() throws Exception {
        rmiView.setController(mockController);

        rmiView.setNickname("Lore");

        rmiView.toController(Choice.CHOOSE_TILE, mockTile);

        // verify
        verify(mockController, timeout(1000).times(1)).chooseTile("Lore", mockTile);
        verify(mockController, never()).chooseBuilding(anyString(), any());
        verify(mockServer, never()).setNickname(any(), any(), any());
        verify(mockServer, never()).createLobby(any(), anyInt(), any());
        verify(mockController, never()).chooseCharacter(anyString(), any());
    }

    @Test
    void toController_RoutesChooseBuildingCorrectly() throws Exception {
        rmiView.setController(mockController);
        rmiView.setNickname("Lore");

        rmiView.toController(Choice.CHOOSE_BUILDING, mockBuilding);

        // verify
        verify(mockController, timeout(1000).times(1)).chooseBuilding("Lore", mockBuilding);
        verify(mockServer, never()).createLobby(any(), anyInt(), any());
        verify(mockServer, never()).setNickname(any(),any(), any());
        verify(mockController, never()).chooseCharacter(anyString(), any());
        verify(mockController, never()).chooseTile(anyString(), any());
    }

    @Test
    void toController_RoutesChooseCharacterCorrectly() throws Exception {
        rmiView.setController(mockController);
        rmiView.setNickname("Lore");

        rmiView.toController(Choice.CHOOSE_CHARACTER, mockCharacter);

        // verify
        verify(mockController, timeout(1000).times(1)).chooseCharacter("Lore", mockCharacter);
        verify(mockServer, never()).createLobby(any(), anyInt(), any());
        verify(mockServer, never()).setNickname(any(),any(), any());
        verify(mockController, never()).chooseTile(anyString(), any());
        verify(mockController, never()).chooseBuilding(anyString(), any());
    }

    @Test
    void toServer_RoutesCreateLobby() throws Exception {
        rmiView.setController(mockController);
        rmiView.setNickname("Lore");

        rmiView.lobbyCode = "123456";
        rmiView.toServer(Choice.CREATE_LOBBY, "123456", 3);

        // verify
        verify(mockServer, timeout(1000).times(1)).createLobby(rmiView, 3, "123456");
        verify(mockServer, never()).setNickname(any(), any(), any());
        verify(mockController, never()).chooseCharacter(anyString(), any());
        verify(mockController, never()).chooseTile(anyString(), any());
        verify(mockController, never()).chooseBuilding(anyString(), any());
    }

    @Test
    void toServer_RoutesChooseNickname() throws Exception {
        rmiView.setController(mockController);
        rmiView.setNickname("Lore");

        rmiView.toServer(Choice.NICKNAME, "123456", "Lore");

        // verify
        verify(mockServer, timeout(1000).times(1)).setNickname(rmiView, "Lore", "123456");
        verify(mockServer, never()).createLobby(any(), anyInt(), any());
        verify(mockController, never()).chooseCharacter(anyString(), any());
        verify(mockController, never()).chooseTile(anyString(), any());
        verify(mockController, never()).chooseBuilding(anyString(), any());
    }

    @Test
    void toServer_RoutesGetAvailableLobbies() throws Exception {
        rmiView.toServer(Choice.GET_AVAILABLE_LOBBIES, null, null);

        // verify
        verify(mockServer, timeout(1000).times(1)).showAvailableLobbies(rmiView);
        verify(mockServer, never()).createLobby(any(), anyInt(), any());
        verify(mockServer, never()).setNickname(any(), any(), any());
        verify(mockServer, never()).joinLobby(any(), any());
    }

    @Test
    void toServer_RoutesJoinLobby() throws Exception {
        rmiView.toServer(Choice.JOIN_LOBBY, "123456", null);

        // verify
        verify(mockServer, timeout(1000).times(1)).joinLobby(rmiView, "123456");
        verify(mockServer, never()).createLobby(any(), anyInt(), any());
        verify(mockServer, never()).setNickname(any(), any(), any());
        verify(mockServer, never()).showAvailableLobbies(any());
    }

    @Test
    void startHeartbeatCorrect() throws IOException, InterruptedException {
        // Act
        rmiView.startHeartbeat(mockServer);

        // Assert
        Thread.sleep(1500);
        verify(mockServer, atLeastOnce()).ping();
        verify(mockUI, never()).printError(ErrorType.CONNECTION_CRASHED);
        verify(mockUI, never()).printEnd();
    }

    @Test
    void startHeartbeatFail() throws IOException, InterruptedException {
        // Act
        rmiView.startHeartbeat(mockServer);
        doThrow(new IOException()).when(mockServer).ping();

        // Assert
        Thread.sleep(1500);
        verify(mockServer, atLeastOnce()).ping();
        verify(mockUI).printError(ErrorType.CONNECTION_CRASHED);
        verify(mockUI).printEnd();
    }
}