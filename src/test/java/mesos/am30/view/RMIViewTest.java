package mesos.am30.view;

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
        IF_Server serverStub = (IF_Server) UnicastRemoteObject.exportObject(mockServer, 0);

        // registry for test
        testRegistry = LocateRegistry.createRegistry(1099);
        testRegistry.rebind("Game", serverStub);
    }

    // free the port after each test, in order to avoid "Port already in use" error
    @AfterEach
    void tearDown() throws Exception {
        UnicastRemoteObject.unexportObject(rmiView, true);
        UnicastRemoteObject.unexportObject(testRegistry, true);
    }

    @Test
    void findServer_Success() throws Exception {
        rmiView.findServer("localhost", 1099);

        verify(mockServer, times(1)).handleConnection(any(IF_GameView.class));
    }

    @Test
    void findServer_Fail() throws Exception {
        testRegistry.unbind("Game");

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
        verify(mockController, never()).chooseCharacter(anyString(), any());
    }

    @Test
    void toController_RoutesChooseBuildingCorrectly() throws Exception {
        rmiView.setController(mockController);
        rmiView.setNickname("Lore");

        rmiView.toController(Choice.CHOOSE_BUILDING, mockBuilding);

        // verify
        verify(mockController, times(1)).chooseBuilding("Lore", mockBuilding);
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
        verify(mockController, never()).chooseTile(anyString(), any());
        verify(mockController, never()).chooseBuilding(anyString(), any());
    }
}