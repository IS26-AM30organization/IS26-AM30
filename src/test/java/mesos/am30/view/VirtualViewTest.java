package mesos.am30.view;

import mesos.am30.GameModel.*;
import mesos.am30.common.Choice;
import mesos.am30.common.ErrorType;
import mesos.am30.common.Move;
import mesos.am30.common.ViewParameter;

import mesos.am30.server.IF_GameController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class VirtualViewTest {
    private VirtualView virtualView;

    @Mock
    private IF_GameUI mockUI;

    @Mock
    private Player mockPlayer;

    @Mock
    private Tile mockTile;

    @Mock
    private CharacterCard mockCharacterCard;

    @Mock
    private BuildingCard mockBuildingCard;

    @Mock
    private List<Object> mockList;

    @BeforeEach
    void setUp() {
        // set up anonymous VirtualView
        virtualView = new VirtualView(mockUI) {
            @Override
            public void findServer(String path, int port) {}

            @Override
            protected void toController(Choice choice, Object parameter) {}

            @Override
            public void setController(IF_GameController controller) {}

            @Override
            public void startListening() {}
        };
    }

    @Test
    void checkTile_Wrong_Turn() throws IOException {
        // set up VirtualView
        when(mockPlayer.getNickname()).thenReturn("nickname1");
        virtualView.setNickname("nickname2");
        ViewModel viewModel = virtualView.getModel();
        viewModel.setPlayers(List.of(mockPlayer));
        viewModel.setCurrentUser("nickname1");

        // Act
        virtualView.checkTile(mockTile);

        // Assert
        assertNotEquals(virtualView.getNickname(), viewModel.getCurrentUser().getNickname());
        verify(mockUI).printError(ErrorType.NOT_YOUR_TURN);
    }

    @Test
    void checkTile_Wrong_Move() throws IOException {
        // set up VirtualView
        when(mockPlayer.getNickname()).thenReturn("nickname");
        virtualView.setNickname("nickname");
        ViewModel viewModel = virtualView.getModel();
        viewModel.setPlayers(List.of(mockPlayer));
        viewModel.setCurrentUser("nickname");
        viewModel.setCurrentMove(Move.PICK_FROM_UP);

        // Act
        virtualView.checkTile(mockTile);

        // Assert
        assertEquals(virtualView.getNickname(), viewModel.getCurrentUser().getNickname());
        assertNotEquals(Move.PICK_TILE, viewModel.getCurrentMove());
        verify(mockUI).printError(ErrorType.NOT_YOUR_TURN);
    }

    @Test
    void checkTile_Wrong_Tile() throws IOException {
        // set up VirtualView
        when(mockPlayer.getNickname()).thenReturn("nickname");
        virtualView.setNickname("nickname");
        ViewModel viewModel = virtualView.getModel();
        viewModel.setTiles(List.of());
        viewModel.setPlayers(List.of(mockPlayer));
        viewModel.setCurrentUser("nickname");
        viewModel.setCurrentMove(Move.PICK_TILE);

        // Act
        virtualView.checkTile(mockTile);

        // Assert
        assertEquals(virtualView.nickname, viewModel.getCurrentUser().getNickname());
        assertEquals(Move.PICK_TILE, viewModel.getCurrentMove());
        assertFalse(viewModel.getTiles().contains(mockTile));
        verify(mockUI).printError(ErrorType.WRONG_TILE);
    }

    @Test
    void checkTile_Taken_Tile() throws IOException {
        // set up VirtualView
        when(mockTile.getCurrentPlayer()).thenReturn(Optional.of(mockPlayer));
        when(mockPlayer.getNickname()).thenReturn("nickname");
        virtualView.setNickname("nickname");
        ViewModel viewModel = virtualView.getModel();
        viewModel.setTiles(List.of(mockTile));
        viewModel.setPlayers(List.of(mockPlayer));
        viewModel.setCurrentUser("nickname");
        viewModel.setCurrentMove(Move.PICK_TILE);

        // Act
        virtualView.checkTile(mockTile);

        // Assert
        assertEquals(virtualView.nickname, viewModel.getCurrentUser().getNickname());
        assertEquals(Move.PICK_TILE, viewModel.getCurrentMove());
        assertTrue(viewModel.getTiles().contains(mockTile));
        assertFalse(mockTile.getCurrentPlayer().isEmpty());
        verify(mockUI).printError(ErrorType.WRONG_TILE);
    }

    @Test
    void checkTile_Correct() throws IOException {
        // set up VirtualView
        when(mockTile.getCurrentPlayer()).thenReturn(Optional.empty());
        when(mockPlayer.getNickname()).thenReturn("nickname");
        virtualView.setNickname("nickname");
        ViewModel viewModel = virtualView.getModel();
        viewModel.setTiles(List.of(mockTile));
        viewModel.setPlayers(List.of(mockPlayer));
        viewModel.setCurrentUser("nickname");
        viewModel.setCurrentMove(Move.PICK_TILE);

        // Act
        virtualView.checkTile(mockTile);

        // Assert
        assertNull(viewModel.getCurrentUser());
        assertNull(viewModel.getCurrentMove());
        assertTrue(viewModel.getTiles().contains(mockTile));
        assertTrue(mockTile.getCurrentPlayer().isEmpty());
    }

    @Test
    void checkCharacterCard_Wrong_Turn() throws IOException {
        // set up VirtualView
        when(mockPlayer.getNickname()).thenReturn("nickname1");
        virtualView.setNickname("nickname2");
        ViewModel viewModel = virtualView.getModel();
        viewModel.setPlayers(List.of(mockPlayer));
        viewModel.setCurrentUser("nickname1");

        // Act
        virtualView.checkCharacterCard(mockCharacterCard);

        // Assert
        assertNotEquals(virtualView.getNickname(), viewModel.getCurrentUser().getNickname());
        verify(mockUI).printError(ErrorType.NOT_YOUR_TURN);
    }

    @Test
    void checkCharacterCard_Wrong_Move() throws IOException {
        // set up VirtualView
        when(mockPlayer.getNickname()).thenReturn("nickname");
        virtualView.setNickname("nickname");
        ViewModel viewModel = virtualView.getModel();
        viewModel.setPlayers(List.of(mockPlayer));
        viewModel.setCurrentUser("nickname");
        viewModel.setCurrentMove(Move.PICK_TILE);

        // Act
        virtualView.checkCharacterCard(mockCharacterCard);

        // Assert
        assertEquals(virtualView.getNickname(), viewModel.getCurrentUser().getNickname());
        assertEquals(Move.PICK_TILE, viewModel.getCurrentMove());
        verify(mockUI).printError(ErrorType.NOT_YOUR_TURN);
    }

    @Test
    void checkCharacterCard_Wrong_Card() throws IOException {
        // set up VirtualView
        when(mockPlayer.getNickname()).thenReturn("nickname");
        virtualView.setNickname("nickname");
        ViewModel viewModel = virtualView.getModel();
        viewModel.setUpperRow(List.of());
        viewModel.setPlayers(List.of(mockPlayer));
        viewModel.setCurrentUser("nickname");
        viewModel.setCurrentMove(Move.PICK_FROM_UP);

        // Act
        virtualView.checkCharacterCard(mockCharacterCard);

        // Assert
        assertEquals(virtualView.nickname, viewModel.getCurrentUser().getNickname());
        assertEquals(Move.PICK_FROM_UP, viewModel.getCurrentMove());
        assertFalse(viewModel.getUpperRow().contains(mockCharacterCard));
        verify(mockUI).printError(ErrorType.WRONG_CARD);
    }

    @Test
    void checkCharacterCard_Correct() throws IOException {
        // set up VirtualView
        when(mockPlayer.getNickname()).thenReturn("nickname");
        virtualView.setNickname("nickname");
        ViewModel viewModel = virtualView.getModel();
        viewModel.setLowerRow(List.of(mockCharacterCard));
        viewModel.setPlayers(List.of(mockPlayer));
        viewModel.setCurrentUser("nickname");
        viewModel.setCurrentMove(Move.PICK_FROM_DOWN);

        // Act
        virtualView.checkCharacterCard(mockCharacterCard);

        // Assert
        assertNull(viewModel.getCurrentUser());
        assertNull(viewModel.getCurrentMove());
        assertTrue(viewModel.getLowerRow().contains(mockCharacterCard));
    }

    @Test
    void checkBuildingCard_Wrong_Turn() throws IOException {
        // set up VirtualView
        when(mockPlayer.getNickname()).thenReturn("nickname1");
        virtualView.setNickname("nickname2");
        ViewModel viewModel = virtualView.getModel();
        viewModel.setPlayers(List.of(mockPlayer));
        viewModel.setCurrentUser("nickname1");

        // Act
        virtualView.checkBuildingCard(mockBuildingCard);

        // Assert
        assertNotEquals(virtualView.getNickname(), viewModel.getCurrentUser().getNickname());
        verify(mockUI).printError(ErrorType.NOT_YOUR_TURN);
    }

    @Test
    void checkBuildingCard_Wrong_Move() throws IOException {
        // set up VirtualView
        when(mockPlayer.getNickname()).thenReturn("nickname");
        virtualView.setNickname("nickname");
        ViewModel viewModel = virtualView.getModel();
        viewModel.setPlayers(List.of(mockPlayer));
        viewModel.setCurrentUser("nickname");
        viewModel.setCurrentMove(Move.PICK_TILE);

        // Act
        virtualView.checkBuildingCard(mockBuildingCard);

        // Assert
        assertEquals(virtualView.getNickname(), viewModel.getCurrentUser().getNickname());
        assertEquals(Move.PICK_TILE, viewModel.getCurrentMove());
        verify(mockUI).printError(ErrorType.NOT_YOUR_TURN);
    }

    @Test
    void checkBuildingCard_Wrong_Card() throws IOException {
        // set up VirtualView
        when(mockPlayer.getNickname()).thenReturn("nickname");
        virtualView.setNickname("nickname");
        ViewModel viewModel = virtualView.getModel();
        viewModel.setUpperBuildings(List.of());
        viewModel.setPlayers(List.of(mockPlayer));
        viewModel.setCurrentUser("nickname");
        viewModel.setCurrentMove(Move.PICK_FROM_UP);

        // Act
        virtualView.checkBuildingCard(mockBuildingCard);

        // Assert
        assertEquals(virtualView.nickname, viewModel.getCurrentUser().getNickname());
        assertEquals(Move.PICK_FROM_UP, viewModel.getCurrentMove());
        assertFalse(viewModel.getUpperBuildings().contains(mockBuildingCard));
        verify(mockUI).printError(ErrorType.WRONG_CARD);
    }

    @Test
    void checkBuildingCard_Not_Enough_Food() throws IOException {
        // set up VirtualView
        when(mockPlayer.getNickname()).thenReturn("nickname");
        when(mockPlayer.getParameters()).thenReturn(Map.of(Parameter.FOOD, 1));
        when(mockBuildingCard.getFoodCost()).thenReturn(2);
        virtualView.setNickname("nickname");
        ViewModel viewModel = virtualView.getModel();
        viewModel.setLowerBuildings(List.of(mockBuildingCard));
        viewModel.setPlayers(List.of(mockPlayer));
        viewModel.setCurrentUser("nickname");
        viewModel.setCurrentMove(Move.PICK_CARD);

        // Act
        virtualView.checkBuildingCard(mockBuildingCard);

        // Assert
        assertEquals(virtualView.nickname, viewModel.getCurrentUser().getNickname());
        assertEquals(Move.PICK_CARD, viewModel.getCurrentMove());
        assertTrue(viewModel.getLowerBuildings().contains(mockBuildingCard));
        assertFalse(mockPlayer.getParameters().get(Parameter.FOOD) >= mockBuildingCard.getFoodCost());
        verify(mockUI).printError(ErrorType.NOT_ENOUGH_FOOD);
    }

    @Test
    void checkBuildingCard_Correct() throws IOException {
        // set up VirtualView
        when(mockPlayer.getNickname()).thenReturn("nickname");
        when(mockPlayer.getParameters()).thenReturn(Map.of(Parameter.FOOD, 2));
        when(mockBuildingCard.getFoodCost()).thenReturn(2);
        virtualView.setNickname("nickname");
        ViewModel viewModel = virtualView.getModel();
        viewModel.setLowerBuildings(List.of(mockBuildingCard));
        viewModel.setPlayers(List.of(mockPlayer));
        viewModel.setCurrentUser("nickname");
        viewModel.setCurrentMove(Move.PICK_FROM_DOWN);

        // Act
        virtualView.checkBuildingCard(mockBuildingCard);

        // Assert
        assertNull(viewModel.getCurrentUser());
        assertNull(viewModel.getCurrentMove());
        assertTrue(viewModel.getLowerBuildings().contains(mockBuildingCard));
        assertTrue(mockPlayer.getParameters().get(Parameter.FOOD) >= mockBuildingCard.getFoodCost());
    }

    @Test
    void askPlayersNumber() throws IOException {
        // Act
        virtualView.askPlayersNumber();

        // Assert
        verify(mockUI).askPlayersNumber();
    }

    @Test
    void askNickname() throws IOException {
        // Act
        virtualView.askNickname();

        // Assert
        verify(mockUI).askNickname();
    }

    @Test
    void notifyTurn() throws IOException {
        // set up VirtualView
        when(mockPlayer.getNickname()).thenReturn("nickname");
        virtualView.getModel().setPlayers(List.of(mockPlayer));

        // Act
        virtualView.notifyTurn("nickname", Move.PICK_TILE);

        // Assert
        ViewModel viewModel = virtualView.getModel();
        assertEquals("nickname", viewModel.getCurrentUser().getNickname());
        assertEquals(Move.PICK_TILE, viewModel.getCurrentMove());
        verify(mockUI).printMove("nickname", Move.PICK_TILE);
    }

    @Test
    void notifyError() throws IOException {
        // Act
        virtualView.notifyError(ErrorType.NOT_YOUR_TURN);

        // Assert
        verify(mockUI).printError(ErrorType.NOT_YOUR_TURN);
    }

    @Test
    void update_PLAYERS() throws IOException {
        // set up Mock Players
        mockList = new ArrayList<>();
        for (int i = 0; i < 5; i++) mockList.add(mock(Player.class));

        // Act
        virtualView.update(ViewParameter.PLAYERS, mockList);

        // Assert
        List<Player> players = virtualView.getModel().getPlayers();
        assertEquals(mockList.size(), players.size());
        for (int i = 0; i < mockList.size(); i++) assertEquals(mockList.get(i), players.get(i));
        verify(mockUI).refresh(virtualView.getModel());
    }

    @Test
    void update_TILES() throws IOException {
        // set up Mock Tiles
        mockList = new ArrayList<>();
        for (int i = 0; i < 5; i++) mockList.add(mock(Tile.class));

        // Act
        virtualView.update(ViewParameter.TILES, mockList);

        // Assert
        List<Tile> tiles = virtualView.getModel().getTiles();
        assertEquals(mockList.size(), tiles.size());
        for (int i = 0; i < mockList.size(); i++) assertEquals(mockList.get(i), tiles.get(i));
        verify(mockUI).refresh(virtualView.getModel());
    }

    @Test
    void update_UPPER_ROW() throws IOException {
        // set up Mock Cards
        mockList = new ArrayList<>();
        for (int i = 0; i < 5; i++) mockList.add(mock(Card.class));

        // Act
        virtualView.update(ViewParameter.UPPER_ROW, mockList);

        // Assert
        List<Card> upperRow = virtualView.getModel().getUpperRow();
        assertEquals(mockList.size(), upperRow.size());
        for (int i = 0; i < mockList.size(); i++) assertEquals(mockList.get(i), upperRow.get(i));
        verify(mockUI).refresh(virtualView.getModel());
    }

    @Test
    void update_UPPER_BUILDINGS() throws IOException {
        // set up Mock Cards
        mockList = new ArrayList<>();
        for (int i = 0; i < 5; i++) mockList.add(mock(BuildingCard.class));

        // Act
        virtualView.update(ViewParameter.UPPER_BUILDINGS, mockList);

        // Assert
        List<BuildingCard> upperBuildings = virtualView.getModel().getUpperBuildings();
        assertEquals(mockList.size(), upperBuildings.size());
        for (int i = 0; i < mockList.size(); i++) assertEquals(mockList.get(i), upperBuildings.get(i));
        verify(mockUI).refresh(virtualView.getModel());
    }

    @Test
    void update_LOWER_ROW() throws IOException {
        // set up Mock Cards
        mockList = new ArrayList<>();
        for (int i = 0; i < 5; i++) mockList.add(mock(Card.class));

        // Act
        virtualView.update(ViewParameter.LOWER_ROW, mockList);

        // Assert
        List<Card> lowerRow = virtualView.getModel().getLowerRow();
        assertEquals(mockList.size(), lowerRow.size());
        for (int i = 0; i < mockList.size(); i++) assertEquals(mockList.get(i), lowerRow.get(i));
        verify(mockUI).refresh(virtualView.getModel());
    }

    @Test
    void update_LOWER_BUILDINGS() throws IOException {
        // set up Mock Cards
        mockList = new ArrayList<>();
        for (int i = 0; i < 5; i++) mockList.add(mock(BuildingCard.class));

        // Act
        virtualView.update(ViewParameter.LOWER_BUILDINGS, mockList);

        // Assert
        List<BuildingCard> lowerBuildings = virtualView.getModel().getLowerBuildings();
        assertEquals(mockList.size(), lowerBuildings.size());
        for (int i = 0; i < mockList.size(); i++) assertEquals(mockList.get(i), lowerBuildings.get(i));
        verify(mockUI).refresh(virtualView.getModel());
    }
}