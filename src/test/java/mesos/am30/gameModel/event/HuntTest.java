package mesos.am30.gameModel.event;

import mesos.am30.gameModel.EventType;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.eventIF.Hunt;
import mesos.am30.gameModel.eventIF.StatsBoost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

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
    void handleEvent_Buildings() {
        // set the Mock Building
        when(mockBuilding.getEventType()).thenReturn(EventType.HUNT);
        when(mockBuilding.getEvent()).thenReturn(mockStatsBoost);

        // set the Mock Player
        List<CharacterCard> hunters = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            hunters.add(mock(CharacterCard.class));
        }
        when(mockPlayer.getTribe()).thenReturn(Map.of(Parameter.HUNTER, hunters));
        when(mockPlayer.getBuildings()).thenReturn(Set.of(mockBuilding));

        // Act
        huntCard.handleEvent(mockPlayer);

        // Assert
        verify(mockStatsBoost).handleEvent(mockPlayer);
        verify(mockPlayer).updateStats(Parameter.FOOD, 1);
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, huntCard.getPrestigePoints() * hunters.size());
    }
}