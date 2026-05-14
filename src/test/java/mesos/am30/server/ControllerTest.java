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
    private IF_GameView mockView1;
    private IF_GameView mockView2;
    private IF_GameModel mockBoard;

    @BeforeEach
    void setUp() throws IOException, NoSuchFieldException, IllegalAccessException {

        when(player1.getNickname()).thenReturn("Alice");
        when(player2.getNickname()).thenReturn("Bob");

        mockView1 = mock(IF_GameView.class);
        mockView2 = mock(IF_GameView.class);
        mockBoard = mock(Board.class);

        controller = new Controller(2);

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
    void startGame_NormalFlow() throws IOException {
        lenient().when(player2.getNickname()).thenReturn("Bob");
        try (MockedConstruction<Board> mocked = mockConstruction(Board.class,
                (mock, _) -> when(mock.getCurrentPlayer()).thenReturn(player1))) {
            controller.startGame();
            verify(mocked.constructed().getFirst()).prepare();
            verify(mocked.constructed().getFirst()).start();
            verify(mockView1).notifyTurn("Alice", Move.PICK_TILE);
        }
    }

    @Test
    void startGame_PrepareThrowsIOException(){
        lenient().when(player2.getNickname()).thenReturn("Bob");
        try (MockedConstruction<Board> mocked = mockConstruction(Board.class, (mock, _) -> {
            doThrow(new IOException()).when(mock).prepare();
            when(mock.getCurrentPlayer()).thenReturn(player1);
        })) {
            controller.startGame();
            verify(mocked.constructed().getFirst()).start();
        }
    }

    @Test
    void startGame_SendMoveThrowsIOException() throws IOException {
        lenient().when(player2.getNickname()).thenReturn("Bob");
        try (MockedConstruction<Board> ignored = mockConstruction(Board.class,
                (mock, _) -> when(mock.getCurrentPlayer()).thenReturn(player1))) {
            doThrow(new IOException()).when(mockView1).notifyTurn(any(), any());
            controller.startGame();
        }
    }

    @Test
    void chooseTile_RightPlayer() throws IOException {
        Tile myTile = new Tile(5, 2, 1);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        lenient().when(player1.hasNoMoves()).thenReturn(true);
        when(mockBoard.getTiles()).thenReturn(Collections.singletonList(myTile));
        controller.chooseTile(player1.getNickname(), myTile);

        verify(mockView1, never()).notifyError(any());
        verify(mockView2, never()).notifyError(any());
    }

    @Test
    void chooseTile_WrongPlayer() throws IOException {
        Tile myTile = new Tile(5, 2, 1);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        controller.chooseTile(player2.getNickname(), myTile);

        verify(mockView2, times(1)).notifyError(ErrorType.NOT_YOUR_TURN);
        verify(mockView1, never()).notifyError(any());
    }

    @Test
    void chooseTile_RightPlayer_HasMoves() throws IOException {
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);

        controller.chooseTile("Alice", new Tile(5, 2, 1));

        verify(mockView1).notifyError(ErrorType.NOT_YOUR_TURN);
    }

    @Test
    void chooseTile_TileNotFound() throws IOException {
        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(true);
        when(mockBoard.getTiles()).thenReturn(Collections.singletonList(new Tile(3, 1, 0)));

        controller.chooseTile("Alice", new Tile(5, 2, 1));

        verify(mockView1).notifyError(ErrorType.WRONG_TILE);
    }

    @Test
    void chooseCharacter_CorrectPlayer_HasMoves() throws IOException {
        CharacterCard targetCard = new CharacterCard(1, Parameter.SHAMAN, 3, 3, 100);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);

        when(player1.hasEnoughUpMoves()).thenReturn(true);
        when(mockBoard.getUpperRow()).thenReturn(Collections.singletonList(targetCard));

        controller.chooseCharacter("Alice", targetCard);

        verify(mockView1, never()).notifyError(any());
    }

    @Test
    void chooseCharacter_CorrectPlayer_NoMoves() throws IOException {
        CharacterCard targetCard = new CharacterCard(1, Parameter.SHAMAN, 5, 3, 101);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(true);

        controller.chooseCharacter("Alice", targetCard);

        verify(mockView1, times(1)).notifyError(ErrorType.NOT_YOUR_TURN);
    }

    @Test
    void chooseCharacter_IncorrectPlayer() throws IOException {
        CharacterCard targetCard = new CharacterCard(1, Parameter.SHAMAN, 3, 3, 102);

        controller.chooseCharacter(player2.getNickname(), targetCard);

        verify(mockView2, times(1)).notifyError(ErrorType.NOT_YOUR_TURN);
        verify(mockView1, never()).notifyError(any());
    }

    @Test
    void chooseCharacter_CorrectPlayer_LowerRow() throws IOException {
        CharacterCard card = new CharacterCard(1, Parameter.SHAMAN, 3, 3, 200);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(false);
        when(player1.hasEnoughDownMoves()).thenReturn(true);
        when(mockBoard.getLowerRow()).thenReturn(Collections.singletonList(card));

        controller.chooseCharacter("Alice", card);

        verify(player1).decreaseRemainingDownMoves();
        verify(mockView1, never()).notifyError(any());
    }

    @Test
    void chooseCharacter_WrongCard() throws IOException {
        CharacterCard card = new CharacterCard(1, Parameter.SHAMAN, 3, 3, 201);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(false);
        when(player1.hasEnoughDownMoves()).thenReturn(false);

        controller.chooseCharacter("Alice", card);

        verify(mockView1).notifyError(ErrorType.WRONG_CARD);
    }

    @Test
    void chooseCharacter_PickCard_NextRoundContinues() throws IOException {
        CharacterCard card = new CharacterCard(1, Parameter.SHAMAN, 3, 3, 202);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(true);
        when(mockBoard.getUpperRow()).thenReturn(Collections.singletonList(card));
        when(mockBoard.pickCard(player1, card)).thenReturn(true);
        when(mockBoard.nextRound()).thenReturn(false);

        controller.chooseCharacter("Alice", card);

        verify(mockBoard).nextRound();
        verify(mockView1, never()).end();
    }

    @Test
    void chooseCharacter_GameEnds() throws IOException {
        CharacterCard card = new CharacterCard(1, Parameter.SHAMAN, 3, 3, 203);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(true);
        when(mockBoard.getUpperRow()).thenReturn(Collections.singletonList(card));
        when(mockBoard.pickCard(player1, card)).thenReturn(true);
        when(mockBoard.nextRound()).thenReturn(true);

        controller.chooseCharacter("Alice", card);

        verify(mockView1).end();
        verify(mockView2).end();
    }

    @Test
    void chooseBuilding_CorrectPlayer_NoMoves() throws IOException {
        BuildingCard card = mock(BuildingCard.class);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(true);

        controller.chooseBuilding("Alice", card);

        verify(mockView1, times(1)).notifyError(ErrorType.NOT_YOUR_TURN);
        verify(mockBoard, never()).pickCard(player1, card);
    }

    @Test
    void chooseBuilding_CorrectPlayer_NotEnoughFood() throws IOException {
        BuildingCard card = mock(BuildingCard.class);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);

        when(mockBoard.getUpperBuildings()).thenReturn(Collections.singletonList(card));
        when(player1.hasEnoughUpMoves()).thenReturn(true);
        when(card.canBeBought(player1)).thenReturn(false);

        controller.chooseBuilding("Alice", card);

        verify(mockView1, times(1)).notifyError(ErrorType.NOT_ENOUGH_FOOD);
        verify(mockBoard, never()).pickCard(player1, card);
    }

    @Test
    void chooseBuilding_CorrectPlayer() throws IOException {
        BuildingCard card = mock(BuildingCard.class);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);

        when(mockBoard.getUpperBuildings()).thenReturn(Collections.singletonList(card));
        when(player1.hasEnoughUpMoves()).thenReturn(true);
        when(card.canBeBought(player1)).thenReturn(true);

        controller.chooseBuilding("Alice", card);

        verify(mockBoard).pickCard(player1, card);
        verify(mockView1, never()).notifyError(any());
    }


    @Test
    void chooseBuilding_LowerRow() throws IOException {
        BuildingCard card = mock(BuildingCard.class);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(false);
        when(player1.hasEnoughDownMoves()).thenReturn(true);
        when(mockBoard.getLowerBuildings()).thenReturn(Collections.singletonList(card));
        when(card.canBeBought(player1)).thenReturn(true);

        controller.chooseBuilding("Alice", card);

        verify(player1).decreaseRemainingDownMoves();
        verify(mockView1, never()).notifyError(any());
    }

    @Test
    void chooseBuilding_WrongCard() throws IOException {
        BuildingCard card = mock(BuildingCard.class);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(false);
        when(player1.hasEnoughDownMoves()).thenReturn(false);

        controller.chooseBuilding("Alice", card);

        verify(mockView1).notifyError(ErrorType.WRONG_CARD);
    }

    @Test
    void chooseBuilding_PickCard_NextRoundContinues() throws IOException {
        BuildingCard card = mock(BuildingCard.class);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(true);
        when(mockBoard.getUpperBuildings()).thenReturn(Collections.singletonList(card));
        when(card.canBeBought(player1)).thenReturn(true);
        when(mockBoard.pickCard(player1, card)).thenReturn(true);
        when(mockBoard.nextRound()).thenReturn(false);

        controller.chooseBuilding("Alice", card);

        verify(mockBoard).nextRound();
        verify(mockView1, never()).end();
    }

    @Test
    void chooseBuilding_GameEnds() throws IOException {
        BuildingCard card = mock(BuildingCard.class);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(true);
        when(mockBoard.getUpperBuildings()).thenReturn(Collections.singletonList(card));
        when(card.canBeBought(player1)).thenReturn(true);
        when(mockBoard.pickCard(player1, card)).thenReturn(true);
        when(mockBoard.nextRound()).thenReturn(true);

        controller.chooseBuilding("Alice", card);

        verify(mockView1).end();
        verify(mockView2).end();
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
    void handleError_NullConnection() throws IOException, NoSuchFieldException, IllegalAccessException {
        Map<Player, IF_GameView> nullConnections = new HashMap<>();
        nullConnections.put(player1, null);
        nullConnections.put(player2, mockView2);
        java.lang.reflect.Field f = Controller.class.getDeclaredField("connections");
        f.setAccessible(true);
        f.set(controller, nullConnections);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);

        controller.chooseTile("Alice", new Tile(5, 2, 1));

        verify(mockView2, never()).notifyError(any());
    }

    @Test
    void sendEnd_WithNullConnection() throws IOException, NoSuchFieldException, IllegalAccessException {
        CharacterCard card = new CharacterCard(1, Parameter.SHAMAN, 3, 3, 300);

        Map<Player, IF_GameView> mixedConnections = new HashMap<>();
        mixedConnections.put(player1, null);
        mixedConnections.put(player2, mockView2);
        java.lang.reflect.Field f = Controller.class.getDeclaredField("connections");
        f.setAccessible(true);
        f.set(controller, mixedConnections);

        when(mockBoard.getCurrentPlayer()).thenReturn(player1);
        when(player1.hasNoMoves()).thenReturn(false);
        when(player1.hasEnoughUpMoves()).thenReturn(true);
        when(mockBoard.getUpperRow()).thenReturn(Collections.singletonList(card));
        when(mockBoard.pickCard(player1, card)).thenReturn(true);
        when(mockBoard.nextRound()).thenReturn(true);

        controller.chooseCharacter("Alice", card);

        verify(mockView2).end();
    }

}

