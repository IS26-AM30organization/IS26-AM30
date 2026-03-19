package mesos.am30.GameModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ShamanicRitualTest {
    private ShamanicRitual shamanicRitualCard;
    private List<Player> mockPlayers;

    @Mock
    private Player mockPlayer1;
    @Mock
    private Player mockPlayer2;
    @Mock
    private Player mockPlayer3;
    @Mock
    private Player mockPlayer4;

    @Mock
    private BuildingCard mockBuildingFirst;
    @Mock
    private BuildingCard mockBuildingLast;

    @Mock
    private ShamanBoost mockBoostFirst;
    @Mock
    private ShamanBoost mockBoostLast;

    @BeforeEach
    void setUp() {
        shamanicRitualCard = new ShamanicRitual(4,-3,5);
        mockPlayers = List.of(mockPlayer1, mockPlayer2, mockPlayer3, mockPlayer4);
    }

    @Test
    void handleEvent_MaxMin() {
        // set the Mock Players
        when(mockPlayer1.getParameters()).thenReturn(Map.of(
                Parameter.SHAMAN, 3
        ));
        when(mockPlayer2.getParameters()).thenReturn(Map.of(
                Parameter.SHAMAN, 4
        ));
        when(mockPlayer3.getParameters()).thenReturn(Map.of(
                Parameter.SHAMAN, 5
        ));
        when(mockPlayer4.getParameters()).thenReturn(Map.of(
                Parameter.SHAMAN, 5
        ));

        // Act
        for (Player p : mockPlayers) {
            shamanicRitualCard.handleEvent(p);
        }

        // Assert
        verify(mockPlayer1).updateStats(Parameter.PRESTIGE_POINTS, shamanicRitualCard.getLostPrestigePoints());
        verify(mockPlayer3).updateStats(Parameter.PRESTIGE_POINTS, shamanicRitualCard.getGainedPrestigePoints());
        verify(mockPlayer4).updateStats(Parameter.PRESTIGE_POINTS, shamanicRitualCard.getGainedPrestigePoints());
    }

    @Test
    void handleEvent_AllSame() {
        // set the Mock Players
        for (Player p : mockPlayers) {
            when(p.getParameters()).thenReturn(Map.of(
                    Parameter.SHAMAN, 3
            ));
        }

        // Act
        for (Player p : mockPlayers) {
            shamanicRitualCard.handleEvent(p);
        }

        // Assert
        for (Player p : mockPlayers) {
            verify(p).updateStats(Parameter.PRESTIGE_POINTS, shamanicRitualCard.getLostPrestigePoints());
            verify(p).updateStats(Parameter.PRESTIGE_POINTS, shamanicRitualCard.getGainedPrestigePoints());
        }
    }

    @Test
    void handleEvent_Buildings() {
        // set the Mock BuildingCards
        when(mockBuildingFirst.getEventType()).thenReturn(EventType.SHAMANIC_RITUAL);
        when(mockBuildingFirst.getEvent()).thenReturn(mockBoostFirst);
        when(mockBoostFirst.isFirstOrLast()).thenReturn(true);
        when(mockBuildingLast.getEventType()).thenReturn(EventType.SHAMANIC_RITUAL);
        when(mockBuildingLast.getEvent()).thenReturn(mockBoostLast);
        when(mockBoostLast.isFirstOrLast()).thenReturn(false);

        // set the Mock Players
        when(mockPlayer1.getBuildings()).thenReturn(Set.of(mockBuildingLast));
        when(mockPlayer1.getParameters()).thenReturn(Map.of(
                Parameter.SHAMAN, 3
        ));
        when(mockPlayer2.getParameters()).thenReturn(Map.of(
                Parameter.SHAMAN, 4
        ));
        when(mockPlayer3.getBuildings()).thenReturn(Set.of(mockBuildingFirst));
        when(mockPlayer3.getParameters()).thenReturn(Map.of(
                Parameter.SHAMAN, 5
        ));
        when(mockPlayer4.getParameters()).thenReturn(Map.of(
                Parameter.SHAMAN, 5
        ));

        // Act
        for (Player p : mockPlayers) {
            shamanicRitualCard.handleEvent(p);
        }

        // Assert
        verify(mockBoostLast).handleEvent(mockPlayer1);
        verify(mockPlayer1).updateStats(Parameter.PRESTIGE_POINTS, shamanicRitualCard.getLostPrestigePoints());
        verify(mockBoostFirst).handleEvent(mockPlayer3);
        verify(mockPlayer3).updateStats(Parameter.PRESTIGE_POINTS, shamanicRitualCard.getGainedPrestigePoints());
        verify(mockPlayer4).updateStats(Parameter.PRESTIGE_POINTS, shamanicRitualCard.getGainedPrestigePoints());
    }
}