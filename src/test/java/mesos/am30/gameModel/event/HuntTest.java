package mesos.am30.gameModel.event;

import mesos.am30.gameModel.EventType;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.CharacterCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class HuntTest {
    private Hunt huntCard;

    @Mock
    private Player mockPlayer;

    @Mock
    private BuildingCard mockBuilding;

    @Mock
    private StatsBoost mockStatsBoost;

    @BeforeEach
    void setUp() {
        huntCard = new Hunt(3);
    }

    @Test
    void handleEvent() {
        // set the Mock Player
        List<CharacterCard> hunters = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            hunters.add(mock(CharacterCard.class));
        }
        when(mockPlayer.getTribe()).thenReturn(Map.of(Parameter.HUNTER, hunters));

        // Act
        huntCard.handleEvent(mockPlayer);

        // Assert
        verify(mockPlayer).updateStats(Parameter.FOOD, 1);
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, huntCard.getPrestigePoints() * hunters.size());
    }

    @Test
    void handleEvent_BuildingsCorrect() {
        // set the Mock Building
        when(mockBuilding.getEventType()).thenReturn(EventType.HUNT);
        when(mockBuilding.getEvent()).thenReturn(mockStatsBoost);

        // set the Mock Player
        List<CharacterCard> hunters = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            hunters.add(mock(CharacterCard.class));
        }
        when(mockPlayer.getTribe()).thenReturn(Map.of(Parameter.HUNTER, hunters));
        when(mockPlayer.getBuildings()).thenReturn(List.of(mockBuilding));

        // Act
        huntCard.handleEvent(mockPlayer);

        // Assert
        verify(mockStatsBoost).handleEvent(mockPlayer);
        verify(mockPlayer).updateStats(Parameter.FOOD, 1);
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, huntCard.getPrestigePoints() * hunters.size());
    }

    @Test
    void handleEvent_BuildingsWrong() {
        // set the Mock Building
        when(mockBuilding.getEventType()).thenReturn(EventType.CAVE_PAINTINGS);

        // set the Mock Player
        List<CharacterCard> hunters = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            hunters.add(mock(CharacterCard.class));
        }
        when(mockPlayer.getTribe()).thenReturn(Map.of(Parameter.HUNTER, hunters));
        when(mockPlayer.getBuildings()).thenReturn(List.of(mockBuilding));

        // Act
        huntCard.handleEvent(mockPlayer);

        // Assert
        verifyNoInteractions(mockStatsBoost);
        verify(mockPlayer).updateStats(Parameter.FOOD, 1);
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, huntCard.getPrestigePoints() * hunters.size());
    }

    @Test
    void getAttributes() {
        // set up the StingBuilders
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        // Act
        huntCard.getAttributes(ln1, ln2, ln3);

        // assert
        assertFalse(ln1.toString().isEmpty());
        assertFalse(ln2.toString().isEmpty());
        assertFalse(ln3.toString().isEmpty());
    }

    @Test
    void getInfo() {
        assertEquals("This is a Hunt Event Card: when resolved, the player receives 1 food and " +
                        huntCard.getPrestigePoints() +
                        " pP for each Hunter in player's tribe.",
                huntCard.getInfo(new StringBuilder())
        );
    }

    @Test
    void getArt() {
        assertEquals(huntCard.getPrestigePoints() + "h0h1", huntCard.getArt());
    }
}