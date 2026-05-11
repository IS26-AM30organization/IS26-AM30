package mesos.am30.server;

import mesos.am30.gameModel.*;
import mesos.am30.gameModel.board.Board;
import mesos.am30.gameModel.IF_GameModel;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.Tile;
import mesos.am30.common.ErrorType;
import mesos.am30.client.IF_GameView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

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
        List<Player> players = Arrays.asList(player1, player2);

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


}

