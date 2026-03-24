package mesos.am30.GameModel;
//This test file, shows a better way to instance mock cards
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsBoostTest {
    private StatsBoost statsBoost;

    @Mock
    private Player player;

    @BeforeEach
    void setUp() {
        statsBoost = new StatsBoost(2, Parameter.HUNTER, EventType.EVENT);
    }

    @Test
    void handleEvent_BasicTest() {
        List<CharacterCard> hunters = new ArrayList<>();
        hunters.add(mock(CharacterCard.class));
        when(player.getCharacterType(Parameter.HUNTER)).thenReturn(hunters);

        statsBoost.handleEvent(player);
        verify(player).updateStats(Parameter.FOOD, 2);
    }

    @Test
    void handleEvent_SecondPick_Gives1Boost() {
        List<CharacterCard> hunters = new ArrayList<>();
        when(player.getCharacterType(Parameter.HUNTER)).thenReturn(hunters);


        hunters.add(mock(CharacterCard.class));
        statsBoost.handleEvent(player);
        verify(player).updateStats(Parameter.FOOD, 2);

        hunters.add(mock(CharacterCard.class));
        statsBoost.handleEvent(player);

        verify(player, times(2)).updateStats(Parameter.FOOD, 2);
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
    }
}