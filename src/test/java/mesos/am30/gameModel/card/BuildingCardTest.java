package mesos.am30.gameModel.card;

import mesos.am30.gameModel.EventType;
import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.board.Board;
import mesos.am30.gameModel.eventIF.FullSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuildingCardTest {
    @Mock private Board board;
    @Mock private Player player;

    BuildingCard card1;
    BuildingCard card2;
    IF_Event event;

    @BeforeEach
    void setUp() {
        event = new FullSet(3);
        card1 = new BuildingCard(1, 100, event, EventType.ROUND, 3, 1);
        card2 = new BuildingCard(1, 101, event, EventType.ROUND, 10, 3);
    }

    @Test
    void getEvent() {
        assertEquals(event, card1.getEvent());
        assertEquals(event, card2.getEvent());
    }

    @Test
    void getEventType() {
        assertEquals(EventType.ROUND, card1.getEventType());
        assertEquals(EventType.ROUND, card2.getEventType());
    }

    @Test
    void getFoodCost() {
        assertEquals(3, card1.getFoodCost());
        assertEquals(10, card2.getFoodCost());
    }

    @Test
    void getPpGain() {
        assertEquals(1, card1.getPpGain());
        assertEquals(3, card2.getPpGain());
    }

    @Test
    void isPickable() {
        assertTrue(card1.isPickable());
        assertTrue(card2.isPickable());
    }

    @Test
    void drawUp() {
        card1.drawUp(board);
        card2.drawUp(board);
        verifyNoInteractions(board);
    }

    @Test
    void drawDown() {
        card1.drawDown(board);
        card2.drawDown(board);
        verifyNoInteractions(board);
    }

    @Test
    void discard() {
        card1.discard(board);
        card2.discard(board);
        verify(board).discard(card1);
        verify(board).discard(card2);
    }

    @Test
    void createRow_Normal() {
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        card1.createRow(ln1,ln2,ln3);

        assertFalse(ln1.toString().isEmpty());
        assertFalse(ln2.toString().isEmpty());
        assertFalse(ln3.toString().isEmpty());
    }

    @Test
    void createRow_EmptyBuildingCard() {
        // set up the StingBuilders
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        // Act
        BuildingCard buildingCard = new BuildingCard(1, 100, event, EventType.ROUND, 0, 0);
        buildingCard.createRow(ln1,ln2,ln3);

        // assert
        assertFalse(ln1.toString().isEmpty());
        assertFalse(ln2.toString().isEmpty());
        assertFalse(ln3.toString().isEmpty());
    }

    @Test
    void getCardInfo() {
        assertEquals(event.getInfo(new StringBuilder()), card1.getCardInfo(new StringBuilder()));
        assertEquals(event.getInfo(new StringBuilder()), card2.getCardInfo(new StringBuilder()));
    }

    @Test
    void getArt() {
        assertEquals(card1.getPpGain() + event.getArt() + card1.getFoodCost(), card1.getArt());
        assertEquals(card2.getPpGain() + event.getArt() + card2.getFoodCost(), card2.getArt());
    }

    @Test
    void canBeBought_True() {
        Map<Parameter, Integer> parameters = new HashMap<>();
        parameters.put(Parameter.FOOD, 6);
        parameters.put(Parameter.BUILDER, 5);

        when(player.getParameters()).thenReturn(parameters);

        assertTrue(card2.canBeBought(player));
    }

    @Test
    void canBeBought_False() {
        Map<Parameter, Integer> params = new HashMap<>();
        params.put(Parameter.FOOD, 4);
        params.put(Parameter.BUILDER, 3);

        when(player.getParameters()).thenReturn(params);

        assertFalse(card2.canBeBought(player));
    }
}