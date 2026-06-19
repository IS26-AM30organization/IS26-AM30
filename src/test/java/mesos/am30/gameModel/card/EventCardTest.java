package mesos.am30.gameModel.card;

import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.board.Board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventCardTest {
    @Mock private Board board;
    @Mock private Player p1;
    @Mock private Player p2;

    @Mock private IF_Event mockEvent;
    EventCard card;

    @BeforeEach
    void setUp() {
        card = new EventCard(1, 100, mockEvent);
    }

    @Test
    void getEvent() {
        assertEquals(mockEvent, card.getEvent());
    }

    @Test
    void drawUp() {
        card.drawUp(board);
        verify(board).drawUp(card);
    }

    @Test
    void drawDown() {
        card.drawDown(board);
        verify(board).drawUp(card);
    }

    @Test
    void discard() {
        // set up the Mock Players
        when(board.getPlayersOrder()).thenReturn(List.of(p1, p2));

        // Act
        card.discard(board);

        // Assert
        verify(mockEvent).handleEvent(p1);
        verify(mockEvent).handleEvent(p2);
        verify(board, times(1)).discard(card);
    }

    @Test
    void createRow() {
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();
        doNothing().when(mockEvent).getAttributes(any(), any(), any());

        card.createRow(ln1,ln2,ln3);

        assertFalse(ln1.toString().isEmpty());
        assertFalse(ln2.toString().isEmpty());
        assertFalse(ln3.toString().isEmpty());
    }

    @Test
    void getCardInfo() {
        when(mockEvent.getInfo(any())).thenReturn("Event");
        assertEquals(mockEvent.getInfo(new StringBuilder()), card.getCardInfo(new StringBuilder()));
    }

    @Test
    void getArt() {
        when(mockEvent.getArt()).thenReturn("Event");
        assertEquals(mockEvent.getArt(), card.getArt());
    }
}