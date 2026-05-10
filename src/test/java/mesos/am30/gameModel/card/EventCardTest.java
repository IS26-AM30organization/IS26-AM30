package mesos.am30.gameModel.card;

import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.board.Board;
import mesos.am30.gameModel.eventIF.Hunt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventCardTest {
    @Mock private Board board;
    @Mock private Player p1;
    @Mock private Player p2;

    private IF_Event event = new Hunt(3)  ;
    EventCard card1 = new EventCard(1, event,100);
    EventCard card2 = new EventCard(1, event, 101);

    @Test
    void drawUp() {
        card1.drawUp(board);
        verify(board).drawUp(card1);
    }

    @Test
    void drawDown() {
        card1.drawDown(board);
        verify(board).drawUp(card1);
    }

    @Test
    void discard() {
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        when(board.getPlayersOrder()).thenReturn(players);

        CharacterCard card = new CharacterCard(1,Parameter.HUNTER,3,3,100);
        Map<Parameter, List<CharacterCard>> mockTribe = new HashMap<>();
        mockTribe.put(Parameter.HUNTER, List.of(card));
        when(p1.getTribe()).thenReturn(mockTribe);

        Map<Parameter, List<CharacterCard>> tribeP2 = new HashMap<>();
        tribeP2.put(Parameter.HUNTER, List.of(card));
        when(p2.getTribe()).thenReturn(tribeP2);

        card1.discard(board);

        verify(board, times(1)).discard(card1);
    }

    @Test
    void reorder() {
    }

    @Test
    void createRow() {
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        card1.createRow(ln1,ln2,ln3);

        assertFalse(ln1.toString().isEmpty());
        assertFalse(ln2.toString().isEmpty());
    }
}