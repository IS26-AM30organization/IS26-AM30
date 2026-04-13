package mesos.am30.server;

import mesos.am30.GameModel.*;
import mesos.am30.common.ErrorType;
import mesos.am30.server.Controller;
import mesos.am30.view.IF_GameView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    private Controller controller;
    private Player player1;
    private Player player2;
    private IF_GameView mockView1;
    private IF_GameView mockView2;
    private IF_GameModel mockBoard;

    @BeforeEach
    void setUp() {
        player1 = new Player("Alice");
        player2 = new Player("Bob");
        List<Player> players = Arrays.asList(player1, player2);

        mockView1 = mock(IF_GameView.class);
        mockView2 = mock(IF_GameView.class);

        ConcurrentHashMap<Player, IF_GameView> clientConnections = new ConcurrentHashMap<>();
        clientConnections.put(player1, mockView1);
        clientConnections.put(player2, mockView2);

        mockBoard = mock(IF_GameModel.class);
        controller = new Controller(mockBoard, players, clientConnections);

        controller.nextPlayer(player1, 2, 2);
    }
    @Test
    void pickTile_RightPlayer() throws IOException {
        Tile myTile = new Tile(5, 2, 1);

        controller.pickTile(player1, myTile);

        verify(mockView1, never()).notifyError(any());
        verify(mockView2, never()).notifyError(any());
    }

    @Test
    void pickTile_WrongPlayer() throws IOException {
        Tile myTile = new Tile(5, 2, 1);

        controller.pickTile(player2, myTile);

        verify(mockView2, times(1)).notifyError(ErrorType.NOT_YOUR_TURN);
        verify(mockView1, never()).notifyError(any());
    }

    @Test
    void pickCard_CorrectPlayer_HasMoves() throws IOException {
        CharacterCard targetCard = new CharacterCard(1, Parameter.SHAMAN, 3, 3);

        when(mockBoard.getUpperRow()).thenReturn(Arrays.asList(targetCard));
        controller.pickCard(player1, targetCard);

        verify(mockView1, never()).notifyError(any());
    }

    @Test
    void pickCard_CorrectPlayer_NoMoves() throws IOException {
        CharacterCard targetCard = new CharacterCard(1, Parameter.SHAMAN, 5, 3);

        when(mockBoard.getUpperRow()).thenReturn(Arrays.asList(targetCard));
        controller.nextPlayer(player1, 0, 0);
        controller.pickCard(player1, targetCard);

        verify(mockView1, times(1)).notifyError(ErrorType.WRONG_CARD);
    }

    @Test
    void pickCard_IncorrectPlayer() throws IOException {
        CharacterCard targetCard = new CharacterCard(1, Parameter.SHAMAN, 3, 3);

        controller.nextPlayer(player1, 0, 0);
        controller.pickCard(player2, targetCard);

        verify(mockView2, times(1)).notifyError(ErrorType.NOT_YOUR_TURN);
        verify(mockView1, never()).notifyError(any());
    }

    @Test
    void pickCard_CardNotInBoard() throws IOException {
        CharacterCard cardInBoard = new CharacterCard(1, Parameter.SHAMAN, 3, 3);
        CharacterCard cardRequested = new CharacterCard(2, Parameter.HUNTER, 2, 2);

        when(mockBoard.getUpperRow()).thenReturn(Arrays.asList(cardInBoard));
        when(mockBoard.getLowerRow()).thenReturn(Arrays.asList(cardInBoard));
        controller.pickCard(player1, cardRequested);

        verify(mockView1, times(1)).notifyError(ErrorType.WRONG_CARD);
    }
}