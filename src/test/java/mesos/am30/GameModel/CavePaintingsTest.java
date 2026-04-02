package mesos.am30.GameModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@ExtendWith(MockitoExtension.class)
class CavePaintingsTest {
    private CavePaintings cavePaintingsCard;

    @Mock
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        cavePaintingsCard = new CavePaintings(3,-2,4);
    }

    @Mock
    private BuildingCard mockBuilding;

    @Mock
    private StatsBoost mockStatsBoost;

    @Test
    void handleEvent_Positive() {
        // set the Mock Player
        List<CharacterCard> artists = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            artists.add(mock(CharacterCard.class));
        }
        when(mockPlayer.getTribe()).thenReturn(Map.of(Parameter.ARTIST, artists));

        // Act
        cavePaintingsCard.handleEvent(mockPlayer);

        // Assert
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, cavePaintingsCard.getGainedPrestigePoints() * 3);
    }

    @Test
    void handleEvent_Negative() {
        // set the Mock Player
        List<CharacterCard> artists = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            artists.add(mock(CharacterCard.class));
        }
        when(mockPlayer.getTribe()).thenReturn(Map.of(Parameter.ARTIST, artists));

        // Act
        cavePaintingsCard.handleEvent(mockPlayer);

        // Assert
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, cavePaintingsCard.getLostPrestigePoints());
    }

    @Test
    void handleEvent_Buildings() {
        // set the Mock Building
        when(mockBuilding.getEventType()).thenReturn(EventType.CAVE_PAINTINGS);
        when(mockBuilding.getEvent()).thenReturn(mockStatsBoost);

        // set the Mock Player
        List<CharacterCard> artists = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            artists.add(mock(CharacterCard.class));
        }
        when(mockPlayer.getTribe()).thenReturn(Map.of(Parameter.ARTIST, artists));
        when(mockPlayer.getBuildings()).thenReturn(Set.of(mockBuilding));

        // Act
        cavePaintingsCard.handleEvent(mockPlayer);

        // Assert
        verify(mockStatsBoost).handleEvent(mockPlayer);
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, cavePaintingsCard.getLostPrestigePoints());
    }
}