package mesos.am30.client;

import mesos.am30.GameModel.BuildingCard;
import mesos.am30.GameModel.CharacterCard;
import mesos.am30.GameModel.Tile;
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
        IF_Server ServerStub = (IF_Server) UnicastRemoteObject.exportObject(mockServer, 0);

        testRegistry = LocateRegistry.createRegistry(1099);
        testRegistry.rebind("Game", ServerStub);
        rmiView.findServer("localhost", 1099);

        verify(mockServer, times(1)).handleConnection(any(IF_GameView.class));
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
        verify(mockController, times(1)).chooseTile("Lore", mockTile);
        verify(mockController, never()).chooseBuilding(anyString(), any());
        verify(mockServer, never()).setNickname(any(),any());
        verify(mockServer, never()).setPlayersNumber(any(),any(Integer.class));
        verify(mockController, never()).chooseCharacter(anyString(), any());
    }

    @Test
    void toController_RoutesChooseBuildingCorrectly() throws Exception {
        rmiView.setController(mockController);
        rmiView.setNickname("Lore");

        rmiView.toController(Choice.CHOOSE_BUILDING, mockBuilding);

        // verify
        verify(mockController, times(1)).chooseBuilding("Lore", mockBuilding);
        verify(mockServer, never()).setPlayersNumber(any(),any(Integer.class));
        verify(mockServer, never()).setNickname(any(),any());
        verify(mockController, never()).chooseCharacter(anyString(), any());
        verify(mockController, never()).chooseTile(anyString(), any());
    }

    @Test
    void toController_RoutesChooseCharacterCorrectly() throws Exception {
        rmiView.setController(mockController);
        rmiView.setNickname("Lore");

        rmiView.toController(Choice.CHOOSE_CHARACTER, mockCharacter);

        // verify
        verify(mockController, times(1)).chooseCharacter("Lore", mockCharacter);
        verify(mockServer, never()).setPlayersNumber(any(),any(Integer.class));
        verify(mockServer, never()).setNickname(any(),any());
        verify(mockController, never()).chooseTile(anyString(), any());
        verify(mockController, never()).chooseBuilding(anyString(), any());
    }

    @Test
    void toController_RoutesChoosePlayersNumbers() throws Exception {
        rmiView.setController(mockController);
        rmiView.setNickname("Lore");

        rmiView.toController(Choice.PLAYERS_NUMBER, 3);

        // verify
        verify(mockServer, times(1)).setPlayersNumber(rmiView, (int) 3);
        verify(mockServer, never()).setNickname(any(),any());
        verify(mockController, never()).chooseCharacter(anyString(), any());
        verify(mockController, never()).chooseTile(anyString(), any());
        verify(mockController, never()).chooseBuilding(anyString(), any());
    }

    @Test
    void toController_RoutesChooseNickname() throws Exception {
        rmiView.setController(mockController);
        rmiView.setNickname("Lore");

        rmiView.toController(Choice.NICKNAME, "Lore");

        // verify
        verify(mockServer, times(1)).setNickname(rmiView, (String) "Lore");
        verify(mockServer, never()).setPlayersNumber(any(),any(Integer.class));
        verify(mockController, never()).chooseCharacter(anyString(), any());
        verify(mockController, never()).chooseTile(anyString(), any());
        verify(mockController, never()).chooseBuilding(anyString(), any());
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