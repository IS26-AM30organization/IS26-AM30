package mesos.am30.gameModel.event;

import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.CharacterCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoubleInventionsTest {
    private DoubleInventions testingInventions;

    @Mock
    private Player player;

    @BeforeEach
    void setUp() {
        testingInventions = new DoubleInventions(3);
    }

    @Test
    void handleEvent_NoInventions() {
        when(player.getCharacterType(Parameter.INVENTOR)).thenReturn(List.of());

        // Act
        testingInventions.handleEvent(player);

        // Assert
        verify(player, times(0)).updateStats(Parameter.FOOD, testingInventions.getFoodGain());
        assertTrue(testingInventions.getUniqueInventions().contains(0));
    }

    @Test
    void handleEvent_SimpleAddFood() {
        List<CharacterCard> cards = new ArrayList<>();
        when(player.getCharacterType(Parameter.INVENTOR)).thenReturn(cards);

        CharacterCard card1 = mock(CharacterCard.class);
        when(card1.getValue()).thenReturn(10);
        cards.add(card1);
        testingInventions.handleEvent(player);

        CharacterCard card2 = mock(CharacterCard.class);
        when(card2.getValue()).thenReturn(10);
        cards.add(card2);
        testingInventions.handleEvent(player);

        verify(player, times(1)).updateStats(Parameter.FOOD, testingInventions.getFoodGain());
        assertFalse(testingInventions.getUniqueInventions().contains(10));
    }

    @Test
    void handleEvent_AddMoreFood() {
        List<CharacterCard> cards = new ArrayList<>();
        when(player.getCharacterType(Parameter.INVENTOR)).thenReturn(cards);

        CharacterCard c1 = mock(CharacterCard.class);
        when(c1.getValue()).thenReturn(5);
        cards.add(c1);
        testingInventions.handleEvent(player);

        CharacterCard c2 = mock(CharacterCard.class);
        when(c2.getValue()).thenReturn(5);
        cards.add(c2);
        testingInventions.handleEvent(player);

        verify(player).updateStats(Parameter.FOOD, testingInventions.getFoodGain());

        org.mockito.Mockito.clearInvocations(player);

        CharacterCard c3 = mock(CharacterCard.class);
        when(c3.getValue()).thenReturn(5);
        cards.add(c3);
        testingInventions.handleEvent(player);

        verify(player, never()).updateStats(any(), anyInt());
    }

    @Test
    void handleEvent_NoFood() {
        List<CharacterCard> cards = new ArrayList<>();
        when(player.getCharacterType(Parameter.INVENTOR)).thenReturn(cards);

        CharacterCard c1 = mock(CharacterCard.class);
        when(c1.getValue()).thenReturn(1);
        cards.add(c1);
        testingInventions.handleEvent(player);
        CharacterCard c2 = mock(CharacterCard.class);
        when(c2.getValue()).thenReturn(2);
        cards.add(c2);
        testingInventions.handleEvent(player);
        CharacterCard c3 = mock(CharacterCard.class);
        when(c3.getValue()).thenReturn(3);
        cards.add(c3);
        testingInventions.handleEvent(player);

        verify(player, never()).updateStats(Parameter.FOOD, testingInventions.getFoodGain());
    }

    @Test
    void handleEvent_setCleaning() {
        List<CharacterCard> cards = new ArrayList<>();
        when(player.getCharacterType(Parameter.INVENTOR)).thenReturn(cards);

        for (int i = 0; i < 3; i++) {
            CharacterCard card = mock(CharacterCard.class);
            when(card.getValue()).thenReturn(5);
            cards.add(card);
            testingInventions.handleEvent(player);
        }

        verify(player, times(1)).updateStats(Parameter.FOOD, testingInventions.getFoodGain());
        assertTrue(testingInventions.getUniqueInventions().contains(5));
    }

    @Test
    void getAttributes() {
        // set up the StingBuilders
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        // Act
        testingInventions.getAttributes(ln1, ln2, ln3);

        // assert
        assertFalse(ln1.toString().isEmpty());
        assertFalse(ln2.toString().isEmpty());
        assertTrue(ln3.toString().isEmpty());
    }

    @Test
    void getInfo() {
        assertEquals(
                "This Building gives " + testingInventions.getFoodGain() +
                        " food to its owner when he acquires two inventions of the same type.",
                testingInventions.getInfo(new StringBuilder())
        );
    }

    @Test
    void getArt() {
        assertEquals("di", testingInventions.getArt());
    }
}