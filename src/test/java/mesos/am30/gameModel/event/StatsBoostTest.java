package mesos.am30.gameModel.event;

import mesos.am30.gameModel.EventType;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.CharacterCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsBoostTest {
    private StatsBoost statsBoost;

    @Mock
    private Player player;

    @BeforeEach
    void setUp() {
        statsBoost = new StatsBoost(2, 3, Parameter.HUNTER, EventType.ROUND);
    }

    @Test
    void handleEvent_BasicTest() {
        List<CharacterCard> hunters = new ArrayList<>();
        hunters.add(mock(CharacterCard.class));
        when(player.getCharacterType(Parameter.HUNTER)).thenReturn(hunters);

        statsBoost.handleEvent(player);
        verify(player).updateStats(Parameter.FOOD, 2);
        verify(player).updateStats(Parameter.PRESTIGE_POINTS, 3);
    }

    @Test
    void handleEvent_SecondPick_Gives1Boost() {
        List<CharacterCard> hunters = new ArrayList<>();
        when(player.getCharacterType(Parameter.HUNTER)).thenReturn(hunters);


        hunters.add(mock(CharacterCard.class));
        statsBoost.handleEvent(player);
        verify(player).updateStats(Parameter.FOOD, 2);
        verify(player).updateStats(Parameter.PRESTIGE_POINTS, 3);

        hunters.add(mock(CharacterCard.class));
        statsBoost.handleEvent(player);

        verify(player, times(2)).updateStats(Parameter.FOOD, 2);
        verify(player, times(2)).updateStats(Parameter.PRESTIGE_POINTS, 3);
    }

    @Test
    void handleEvent_MultipleNewCards() {
        List<CharacterCard> hunters = new ArrayList<>();
        when(player.getCharacterType(Parameter.HUNTER)).thenReturn(hunters);

        hunters.add(mock(CharacterCard.class));
        hunters.add(mock(CharacterCard.class));
        hunters.add(mock(CharacterCard.class));

        statsBoost.handleEvent(player);

        verify(player).updateStats(Parameter.FOOD, 6);
        verify(player).updateStats(Parameter.PRESTIGE_POINTS, 9);
    }

    @Test
    void handleEvent_ZeroCards() {
        // Act
        when(player.getCharacterType(Parameter.HUNTER)).thenReturn(List.of());
        statsBoost.handleEvent(player);

        // Assert
        verify(player, never()).updateStats(eq(Parameter.FOOD), anyInt());
        verify(player, never()).updateStats(eq(Parameter.PRESTIGE_POINTS), anyInt());
    }

    @Test
    void handleEvent_NullValues() {
        // set up the Events
        StatsBoost statsBoost1 = new StatsBoost(2, 0, Parameter.HUNTER, EventType.ROUND);
        StatsBoost statsBoost2 = new StatsBoost(0, 3, Parameter.HUNTER, EventType.ROUND);

        // set up the Mock Player
        List<CharacterCard> hunters = new ArrayList<>();
        hunters.add(mock(CharacterCard.class));
        when(player.getCharacterType(Parameter.HUNTER)).thenReturn(hunters);

        // Act
        statsBoost1.handleEvent(player);
        statsBoost2.handleEvent(player);

        // Assert
        verify(player, times(1)).updateStats(Parameter.FOOD, 2);
        verify(player, times(1)).updateStats(Parameter.PRESTIGE_POINTS, 3);
    }

    @Test
    void getAttributes() {
        // set up the StingBuilders
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        // Act
        statsBoost.getAttributes(ln1, ln2, ln3);

        // assert
        assertFalse(ln1.toString().isEmpty());
        assertFalse(ln2.toString().isEmpty());
        assertFalse(ln3.toString().isEmpty());
    }

    @Test
    void getInfo() {
        assertEquals("This Building gives " + statsBoost.getFood() + " food and " + statsBoost.getPrestigePoints() +
                        " pP for each " + statsBoost.getRole() + " in owner's tribe, during " +
                        statsBoost.getType() + " event.",
                statsBoost.getInfo(new StringBuilder())
        );
    }

    @Test
    void getArt() {
        assertEquals(statsBoost.getRole().name().toLowerCase().charAt(0) + "s", statsBoost.getArt());
    }
}