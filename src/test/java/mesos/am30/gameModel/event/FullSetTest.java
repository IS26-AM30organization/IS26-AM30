package mesos.am30.gameModel.event;

import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.eventIF.FullSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FullSetTest {
    private FullSet testerFullSet;

    @Mock
    private Player player;

    private Map<Parameter, List<CharacterCard>> tribeMap;

    @BeforeEach
    void setUp() {
        testerFullSet = new FullSet(6);
        tribeMap = new HashMap<>();

        Parameter[] roles = {
                Parameter.INVENTOR, Parameter.ARTIST, Parameter.BUILDER,
                Parameter.GATHERER, Parameter.HUNTER, Parameter.SHAMAN
        };

        for (Parameter p : roles) {
            List<CharacterCard> list = new ArrayList<>();
            tribeMap.put(p, list);
            lenient().when(player.getCharacterType(p)).thenReturn(list);
        }
        when(player.getTribe()).thenReturn(tribeMap);
    }

    @Test
    void handleEvent_BasicTest() {
        Parameter[] roles = {
                Parameter.INVENTOR, Parameter.ARTIST, Parameter.BUILDER,
                Parameter.GATHERER, Parameter.HUNTER, Parameter.SHAMAN
        };

        for (Parameter role : roles) {
            CharacterCard card = mock(CharacterCard.class);
            when(card.getRole()).thenReturn(role);

            tribeMap.get(role).add(card);
            testerFullSet.handleEvent(player);
        }

        verify(player, times(1)).updateStats(Parameter.FOOD, 6);
    }

    @Test
    void handleEvent_IncompleteSet() {
        Parameter[] roles = {
                Parameter.INVENTOR, Parameter.ARTIST, Parameter.BUILDER,
                Parameter.GATHERER, Parameter.HUNTER, Parameter.SHAMAN
        };

        for (Parameter role : roles) {
            if(!(role.equals(Parameter.INVENTOR))) {
                CharacterCard card = mock(CharacterCard.class);
                when(card.getRole()).thenReturn(role);
                tribeMap.get(role).add(card);
                testerFullSet.handleEvent(player);
            }
        }
        //never called
        verify(player, never()).updateStats(any(), anyInt());
    }

    @Test
    void handleEvent_TwoCompleteSets() {
        Parameter[] roles = {Parameter.INVENTOR, Parameter.ARTIST, Parameter.BUILDER,
                Parameter.GATHERER, Parameter.HUNTER, Parameter.SHAMAN};

        for (Parameter p : roles) {
            List<CharacterCard> list = new ArrayList<>();
            tribeMap.put(p, list);
            when(player.getCharacterType(p)).thenReturn(list);
        }
        when(player.getTribe()).thenReturn(tribeMap);

        //1 Set
        for (Parameter p : roles) {
            CharacterCard card = mock(CharacterCard.class);
            when(card.getRole()).thenReturn(p);
            tribeMap.get(p).add(card);
            testerFullSet.handleEvent(player);
        }

        //2 Set
        for (Parameter p : roles) {
            CharacterCard card = mock(CharacterCard.class);
            when(card.getRole()).thenReturn(p);
            tribeMap.get(p).add(card);
            testerFullSet.handleEvent(player);
        }

        verify(player, times(2)).updateStats(Parameter.FOOD, 6);
    }
}
