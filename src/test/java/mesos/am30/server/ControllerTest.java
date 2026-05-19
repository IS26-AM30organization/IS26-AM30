package mesos.am30.server;

import mesos.am30.gameModel.*;
import mesos.am30.gameModel.board.Board;
import mesos.am30.gameModel.IF_GameModel;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.Tile;
import mesos.am30.common.ErrorType;
import mesos.am30.common.Move;
import mesos.am30.client.IF_GameView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    private Controller controller;
    @Mock
    private Player player1;
    @Mock
    private Player player2;
    @Mock
    private IF_GameView mockView1;
    @Mock
    private IF_GameView mockView2;
    @Mock
    private Tile mockTile;
    @Mock
    private CharacterCard mockCharacterCard;
    @Mock
    private BuildingCard mockBuildingCard;
    @Mock
    private IF_GameModel mockBoard;
    @Mock
    private Server mockServer;

    @BeforeEach
    void setUp() throws IOException, NoSuchFieldException, IllegalAccessException {

        lenient().when(player1.getNickname()).thenReturn("Alice");
        lenient().when(player2.getNickname()).thenReturn("Bob");

        controller = new Controller(2);
        controller.setServer(mockServer);

        //must use Java Reflection to force mockPlayers to be used in connection
        //Otherwise mix of real players and mock players causes problems
        Map<Player, IF_GameView> mockConnections = new HashMap<>();
        mockConnections.put(player1, mockView1);
        mockConnections.put(player2, mockView2);
        java.lang.reflect.Field connectionsField = Controller.class.getDeclaredField("connections");
        connectionsField.setAccessible(true);
        connectionsField.set(controller, mockConnections);

        //must use Java Reflection to force mockBoard to be used instead
        java.lang.reflect.Field boardField = Controller.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boardField.set(controller, mockBoard);
    }

    @Test
    void getPlayersNumber() {
        assertEquals(2, controller.getPlayersNumber());
    }

    @Test
    void getClients() {
        assertEquals(Map.of(
                player1, mockView1,
                player2, mockView2
        ), controller.getClients());
    }

    @Test
    void getOccupiedSlots() {
        assertEquals(2, controller.getOccupiedSlots());
    }

    @Test
    void isFull() {
        assertTrue(controller.isFull());
        controller.getClients().remove(player2);
        assertFalse(controller.isFull());
        controller.getClients().put(player2, mockView2);
        assertTrue(controller.isFull());
    }

    @Test
    void connect_OnePlayer_NotFull() throws IOException, NoSuchFieldException, IllegalAccessException {
        lenient().when(player2.getNickname()).thenReturn("Bob");
        Map<Player, IF_GameView> empty = new HashMap<>();
        java.lang.reflect.Field f = Controller.class.getDeclaredField("connections");
        f.setAccessible(true);
        f.set(controller, empty);

        IF_GameView view = mock(IF_GameView.class);
        assertFalse(controller.connect(view, player1.getNickname()));
        verify(view).setController(controller);
    }

    @Test
    void connect_AllPlayers_Full() throws IOException, NoSuchFieldException, IllegalAccessException {
        Map<Player, IF_GameView> empty = new HashMap<>();
        java.lang.reflect.Field f = Controller.class.getDeclaredField("connections");
        f.setAccessible(true);
        f.set(controller, empty);

        IF_GameView v1 = mock(IF_GameView.class);
        IF_GameView v2 = mock(IF_GameView.class);
        controller.connect(v1, player1.getNickname());
        assertTrue(controller.connect(v2, player2.getNickname()));
    }

    @Test
    void isPlayerTurn() {
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        assertTrue(controller.isPlayerTurn(player1));
        assertFalse(controller.isPlayerTurn(player2));
    }

    @Test
    void isPlayerTurn_Null() {
        when(mockBoard.getCurrentPlayer()).thenReturn(null);
        assertFalse(controller.isPlayerTurn(player1));
        assertFalse(controller.isPlayerTurn(player2));
    }

    @Test
    void startGame_NormalFlow() throws IOException {
        try (MockedConstruction<Board> mocked = mockConstruction(Board.class,
                (mock, _) -> when(mock.getCurrentPlayer()).thenReturn(player1))) {
            controller.startGame();
            verify(mocked.constructed().getFirst()).prepare();
            verify(mocked.constructed().getFirst()).start();
            verify(mockView1).notifyTurn("Alice", Move.PICK_TILE);
        }
    }

    @Test
    void startGame_PrepareThrowsIOException() throws IOException {
        try (MockedConstruction<Board> mocked = mockConstruction(Board.class, (mock, _) -> {
            doThrow(new IOException()).when(mock).prepare();
            when(mock.getCurrentPlayer()).thenReturn(player1);
        })) {
            // Act
            controller.startGame();

            // Assert
            verify(mocked.constructed().getFirst()).prepare();
            verify(mocked.constructed().getFirst()).start();
            verify(mockServer, times(1)).handleDisconnection(mockView1);
            verify(mockServer, times(1)).handleDisconnection(mockView2);
        }
    }

    @Test
    void startGame_SendMoveThrowsIOException() {
        try (MockedConstruction<Board> ignored = mockConstruction(Board.class,
                (mock, _) -> {
                    doThrow(new IOException()).when(mockView1).notifyTurn(any(), any());
                    when(mock.getCurrentPlayer()).thenReturn(player1);
        })) {
            // Act
            controller.startGame();

            // Assert
            verify(mockServer, times(1)).handleDisconnection(mockView1);
            verify(mockServer, times(1)).handleDisconnection(mockView2);
        }
    }

    @Test
    void chooseTile_RightPlayer() throws IOException {
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        lenient().when(player1.hasNoMoves()).thenReturn(true);
        when(mockBoard.getTiles()).thenReturn(Collections.singletonList(mockTile));
        controller.chooseTile(player1.getNickname(), mockTile);

        verify(mockBoard, times(1)).pickTile(player1, mockTile);
        verify(mockView1, never()).notifyError(any());
        verify(mockView2, never()).notifyError(any());
    }

    @Test
    void chooseTile_WrongPlayer() throws IOException {
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        controller.chooseTile(player2.getNickname(), mockTile);

        verify(mockBoard, never()).pickTile(player2, mockTile);
        verify(mockView2, times(1)).notifyError(ErrorType.NOT_YOUR_TURN);
        verify(mockView1, never()).notifyError(any());
    }

    @Test
    void chooseTile_RightPlayer_HasMoves() throws IOException {
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);

        controller.chooseTile("Alice", mockTile);

        verify(mockBoard, never()).pickTile(player1, mockTile);
        verify(mockView1).notifyError(ErrorType.NOT_YOUR_TURN);
        verify(mockView2, never()).notifyError(any());
    }

    @Test
    void chooseTile_TileNotFound() throws IOException {
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(true);
        when(mockBoard.getTiles()).thenReturn(List.of());

        controller.chooseTile("Alice", mockTile);

        verify(mockBoard, never()).pickTile(player1, mockTile);
        verify(mockView1).notifyError(ErrorType.WRONG_TILE);
        verify(mockView2, never()).notifyError(any());
    }

    @Test
    void chooseCharacter_CorrectPlayer_HasNoMoves() throws IOException {
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(true);

        controller.chooseCharacter("Alice", mockCharacterCard);

        verify(mockBoard, never()).pickCard(player1, mockCharacterCard);
        verify(mockView1, times(1)).notifyError(ErrorType.NOT_YOUR_TURN);
        verify(player1, never()).decreaseRemainingUpMoves();
        verify(player1, never()).decreaseRemainingDownMoves();
        verify(mockView2, never()).notifyError(any());
        verify(player2, never()).decreaseRemainingUpMoves();
        verify(player2, never()).decreaseRemainingDownMoves();
    }

    @Test
    void chooseCharacter_IncorrectPlayer() throws IOException {
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        controller.chooseCharacter(player2.getNickname(), mockCharacterCard);

        verify(mockBoard, never()).pickCard(player2, mockCharacterCard);
        verify(mockView2, times(1)).notifyError(ErrorType.NOT_YOUR_TURN);
        verify(mockView1, never()).notifyError(any());
        verify(player1, never()).decreaseRemainingUpMoves();
        verify(player1, never()).decreaseRemainingDownMoves();
        verify(player2, never()).decreaseRemainingUpMoves();
        verify(player2, never()).decreaseRemainingDownMoves();
    }

    @Test
    void chooseCharacter_CorrectPlayer_UpperRow() throws IOException {
        when(mockBoard.pickCard(eq(player1), any(CharacterCard.class))).thenReturn(false);
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(true);
        when(mockBoard.getUpperRow()).thenReturn(Collections.singletonList(mockCharacterCard));

        controller.chooseCharacter("Alice", mockCharacterCard);

        verify(mockView1, never()).notifyError(any());
        verify(mockBoard, times(1)).pickCard(player1, mockCharacterCard);
        verify(player1, times(1)).decreaseRemainingUpMoves();
        verify(player1, never()).decreaseRemainingDownMoves();
        verify(mockView2, never()).notifyError(any());
        verify(player2, never()).decreaseRemainingUpMoves();
        verify(player2, never()).decreaseRemainingDownMoves();
    }

    @Test
    void chooseCharacter_CorrectPlayer_LowerRow() throws IOException {
        when(mockBoard.pickCard(eq(player1), any(CharacterCard.class))).thenReturn(false);
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(false);
        when(player1.hasEnoughDownMoves()).thenReturn(true);
        when(mockBoard.getLowerRow()).thenReturn(Collections.singletonList(mockCharacterCard));

        controller.chooseCharacter("Alice", mockCharacterCard);

        verify(mockBoard, times(1)).pickCard(player1, mockCharacterCard);
        verify(mockView1, never()).notifyError(any());
        verify(player1, never()).decreaseRemainingUpMoves();
        verify(player1, times(1)).decreaseRemainingDownMoves();
        verify(mockView2, never()).notifyError(any());
        verify(player2, never()).decreaseRemainingUpMoves();
        verify(player2, never()).decreaseRemainingDownMoves();
    }

    @Test
    void chooseCharacter_WrongCard() throws IOException {
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(true);
        when(mockBoard.getUpperRow()).thenReturn(List.of());
        when(player1.hasEnoughDownMoves()).thenReturn(true);
        when(mockBoard.getLowerRow()).thenReturn(List.of());

        controller.chooseCharacter("Alice", mockCharacterCard);

        verify(mockView1).notifyError(ErrorType.WRONG_CARD);
        verify(mockBoard, never()).pickCard(player1, mockCharacterCard);
        verify(player1, never()).decreaseRemainingUpMoves();
        verify(player1, never()).decreaseRemainingDownMoves();
        verify(mockView2, never()).notifyError(any());
        verify(player2, never()).decreaseRemainingUpMoves();
        verify(player2, never()).decreaseRemainingDownMoves();
    }

    @Test
    void chooseCharacter_PickCard_NoMoves() throws IOException {
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(false);
        when(player1.hasEnoughDownMoves()).thenReturn(false);

        controller.chooseCharacter("Alice", mockCharacterCard);

        verify(mockView1, times(1)).notifyError(ErrorType.WRONG_CARD);
        verify(mockBoard, never()).pickCard(player1, mockCharacterCard);
        verify(player1, never()).decreaseRemainingUpMoves();
        verify(player1, never()).decreaseRemainingDownMoves();
        verify(mockView2, never()).notifyError(any());
        verify(player2, never()).decreaseRemainingUpMoves();
        verify(player2, never()).decreaseRemainingDownMoves();
    }

    @Test
    void chooseCharacter_NextRound() throws IOException {
        when(mockBoard.pickCard(eq(player1), any(CharacterCard.class))).thenReturn(true);
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(true);
        when(mockBoard.getUpperRow()).thenReturn(Collections.singletonList(mockCharacterCard));

        controller.chooseCharacter("Alice", mockCharacterCard);

        verify(mockView1, never()).notifyError(any());
        verify(mockBoard, times(1)).pickCard(player1, mockCharacterCard);
        verify(player1, times(1)).decreaseRemainingUpMoves();
        verify(player1, never()).decreaseRemainingDownMoves();
        verify(mockView2, never()).notifyError(any());
        verify(player2, never()).decreaseRemainingUpMoves();
        verify(player2, never()).decreaseRemainingDownMoves();

        verify(mockBoard, times(1)).nextRound();
    }

    @Test
    void chooseBuilding_CorrectPlayer_HasNoMoves() throws IOException {
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(true);

        controller.chooseBuilding("Alice", mockBuildingCard);

        verify(mockView1, times(1)).notifyError(ErrorType.NOT_YOUR_TURN);
        verify(mockBoard, never()).pickCard(player1, mockBuildingCard);
        verify(player1, never()).decreaseRemainingUpMoves();
        verify(player1, never()).decreaseRemainingDownMoves();
        verify(mockView2, never()).notifyError(any());
        verify(player2, never()).decreaseRemainingUpMoves();
        verify(player2, never()).decreaseRemainingDownMoves();
    }

    @Test
    void chooseBuilding_IncorrectPlayer() throws IOException {
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        controller.chooseBuilding(player2.getNickname(), mockBuildingCard);

        verify(mockBoard, never()).pickCard(player2, mockBuildingCard);
        verify(mockView2, times(1)).notifyError(ErrorType.NOT_YOUR_TURN);
        verify(mockView1, never()).notifyError(any());
        verify(player1, never()).decreaseRemainingUpMoves();
        verify(player1, never()).decreaseRemainingDownMoves();
        verify(player2, never()).decreaseRemainingUpMoves();
        verify(player2, never()).decreaseRemainingDownMoves();
    }

    @Test
    void chooseBuilding_CorrectPlayer_NotEnoughFood_UpperRow() throws IOException {
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);

        when(mockBoard.getUpperBuildings()).thenReturn(Collections.singletonList(mockBuildingCard));
        when(player1.hasEnoughUpMoves()).thenReturn(true);
        when(mockBuildingCard.canBeBought(player1)).thenReturn(false);

        controller.chooseBuilding("Alice", mockBuildingCard);

        verify(mockView1, times(1)).notifyError(ErrorType.NOT_ENOUGH_FOOD);
        verify(mockBoard, never()).pickCard(player1, mockBuildingCard);
        verify(player1, never()).decreaseRemainingUpMoves();
        verify(player1, never()).decreaseRemainingDownMoves();
        verify(mockView2, never()).notifyError(any());
        verify(player2, never()).decreaseRemainingUpMoves();
        verify(player2, never()).decreaseRemainingDownMoves();
    }

    @Test
    void chooseBuilding_CorrectPlayer_NotEnoughFood_LowerRow() throws IOException {
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);

        when(mockBoard.getLowerBuildings()).thenReturn(Collections.singletonList(mockBuildingCard));
        when(player1.hasEnoughUpMoves()).thenReturn(false);
        when(player1.hasEnoughDownMoves()).thenReturn(true);
        when(mockBuildingCard.canBeBought(player1)).thenReturn(false);

        controller.chooseBuilding("Alice", mockBuildingCard);

        verify(mockView1, times(1)).notifyError(ErrorType.NOT_ENOUGH_FOOD);
        verify(mockBoard, never()).pickCard(player1, mockBuildingCard);
        verify(player1, never()).decreaseRemainingUpMoves();
        verify(player1, never()).decreaseRemainingDownMoves();
        verify(mockView2, never()).notifyError(any());
        verify(player2, never()).decreaseRemainingUpMoves();
        verify(player2, never()).decreaseRemainingDownMoves();
    }

    @Test
    void chooseBuilding_UpperRow() throws IOException {
        when(mockBoard.pickCard(eq(player1), any(BuildingCard.class))).thenReturn(false);
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);

        when(mockBoard.getUpperBuildings()).thenReturn(Collections.singletonList(mockBuildingCard));
        when(player1.hasEnoughUpMoves()).thenReturn(true);
        when(mockBuildingCard.canBeBought(player1)).thenReturn(true);

        controller.chooseBuilding("Alice", mockBuildingCard);

        verify(mockBoard, times(1)).pickCard(player1, mockBuildingCard);
        verify(mockView1, never()).notifyError(any());
        verify(player1, times(1)).decreaseRemainingUpMoves();
        verify(player1, never()).decreaseRemainingDownMoves();
        verify(mockView2, never()).notifyError(any());
        verify(player2, never()).decreaseRemainingUpMoves();
        verify(player2, never()).decreaseRemainingDownMoves();
    }


    @Test
    void chooseBuilding_LowerRow() throws IOException {
        when(mockBoard.pickCard(eq(player1), any(BuildingCard.class))).thenReturn(false);
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(true);
        when(mockBoard.getUpperBuildings()).thenReturn(List.of());
        when(player1.hasEnoughDownMoves()).thenReturn(true);
        when(mockBoard.getLowerBuildings()).thenReturn(Collections.singletonList(mockBuildingCard));
        when(mockBuildingCard.canBeBought(player1)).thenReturn(true);

        controller.chooseBuilding("Alice", mockBuildingCard);

        verify(mockBoard, times(1)).pickCard(player1, mockBuildingCard);
        verify(mockView1, never()).notifyError(any());
        verify(player1, never()).decreaseRemainingUpMoves();
        verify(player1, times(1)).decreaseRemainingDownMoves();
        verify(mockView2, never()).notifyError(any());
        verify(player2, never()).decreaseRemainingUpMoves();
        verify(player2, never()).decreaseRemainingDownMoves();
    }

    @Test
    void chooseBuilding_WrongCard() throws IOException {
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(false);
        when(player1.hasEnoughDownMoves()).thenReturn(true);
        when(mockBoard.getLowerBuildings()).thenReturn(List.of());

        controller.chooseBuilding("Alice", mockBuildingCard);

        verify(mockView1).notifyError(ErrorType.WRONG_CARD);
        verify(mockBoard, never()).pickCard(player1, mockBuildingCard);
        verify(player1, never()).decreaseRemainingUpMoves();
        verify(player1, never()).decreaseRemainingDownMoves();
        verify(mockView2, never()).notifyError(any());
        verify(player2, never()).decreaseRemainingUpMoves();
        verify(player2, never()).decreaseRemainingDownMoves();
    }

    @Test
    void chooseBuilding_PickCard_NoMoves() throws IOException {
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(false);
        when(player1.hasEnoughDownMoves()).thenReturn(false);

        controller.chooseBuilding("Alice", mockBuildingCard);

        verify(mockView1, times(1)).notifyError(ErrorType.WRONG_CARD);
        verify(mockBoard, never()).pickCard(player1, mockBuildingCard);
        verify(player1, never()).decreaseRemainingUpMoves();
        verify(player1, never()).decreaseRemainingDownMoves();
        verify(mockView2, never()).notifyError(any());
        verify(player2, never()).decreaseRemainingUpMoves();
        verify(player2, never()).decreaseRemainingDownMoves();
    }

    @Test
    void chooseBuilding_NextRound() throws IOException {
        when(mockBoard.pickCard(eq(player1), any(BuildingCard.class))).thenReturn(true);
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(true);
        when(mockBoard.getUpperBuildings()).thenReturn(Collections.singletonList(mockBuildingCard));
        when(mockBuildingCard.canBeBought(player1)).thenReturn(true);

        controller.chooseBuilding("Alice", mockBuildingCard);

        verify(mockView1, never()).notifyError(any());
        verify(mockBoard, times(1)).pickCard(player1, mockBuildingCard);
        verify(player1, times(1)).decreaseRemainingUpMoves();
        verify(player1, never()).decreaseRemainingDownMoves();
        verify(mockView2, never()).notifyError(any());
        verify(player2, never()).decreaseRemainingUpMoves();
        verify(player2, never()).decreaseRemainingDownMoves();

        verify(mockBoard, times(1)).nextRound();
    }

    @Test
    void showRankings_Positive() throws IOException {
        controller.showRankings("Alice", true);
        verify(mockView1, times(1)).showRankings(any(), any());
        verify(mockView1, never()).notifyError(any());
        verify(mockView1, times(1)).end();
        verify(mockView2, never()).showRankings(any(), any());
        verify(mockView2, never()).notifyError(any());
        verify(mockView2, never()).end();
    }

    @Test
    void showRankings_Negative() throws IOException {
        controller.showRankings("Alice", false);
        verify(mockView1, never()).showRankings(any(), any());
        verify(mockView1, never()).notifyError(any());
        verify(mockView1, times(1)).end();
        verify(mockView2, never()).showRankings(any(), any());
        verify(mockView2, never()).notifyError(any());
        verify(mockView2, never()).end();
    }

    @Test
    void sendMove() throws IOException {
        // Act - Player 1
        controller.sendMove(player1, Move.PICK_TILE);
        verify(mockView1, times(1)).notifyTurn(player1.getNickname(), Move.PICK_TILE);
        verify(mockView2, never()).notifyTurn(eq(player2.getNickname()), any());

        // Act - Player2
        controller.sendMove(player2, Move.PICK_TILE);
        verify(mockView1, times(1)).notifyTurn(eq(player1.getNickname()), any());
        verify(mockView2, times(1)).notifyTurn(player2.getNickname(), Move.PICK_TILE);

        // Act - Nobody
        controller.sendMove(mock(Player.class), Move.PICK_TILE);
        verify(mockView1, times(1)).notifyTurn(eq(player1.getNickname()), any());
        verify(mockView2, times(1)).notifyTurn(eq(player2.getNickname()), any());

    }

    @Test
    void handleError_NullConnection() throws IOException, NoSuchFieldException, IllegalAccessException {
        Map<Player, IF_GameView> nullConnections = new HashMap<>();
        nullConnections.put(player1, null);
        nullConnections.put(player2, mockView2);
        java.lang.reflect.Field f = Controller.class.getDeclaredField("connections");
        f.setAccessible(true);
        f.set(controller, nullConnections);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);

        controller.chooseTile("Alice", mockTile);

        verify(mockView2, never()).notifyError(any());
    }
}

