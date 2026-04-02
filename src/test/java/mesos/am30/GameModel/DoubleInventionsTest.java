package mesos.am30.GameModel;

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
    private List<CharacterCard> inventorsList;

    @BeforeEach
    void setUp() {
        testingInventions = new DoubleInventions(3);
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

        verify(player, times(1)).updateStats(Parameter.FOOD, 3);
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

        verify(player).updateStats(Parameter.FOOD, 3);

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

        verify(player, never()).updateStats(Parameter.FOOD, 3);
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

        verify(player, times(1)).updateStats(Parameter.FOOD, 3);
        assertTrue(testingInventions.getUniqueInventions().contains(5));
    }
}