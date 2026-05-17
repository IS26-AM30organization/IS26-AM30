package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.EventType;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.BuildingCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

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
    void handleEvent_BuildingsUsed() {
        // set the Mock BuildingCards
        when(mockBuildingFirst.getEventType()).thenReturn(EventType.SHAMANIC_RITUAL);
        when(mockBuildingFirst.getEvent()).thenReturn(mockBoostFirst);
        when(mockBoostFirst.isFirstOrLast()).thenReturn(true);
        when(mockBuildingLast.getEventType()).thenReturn(EventType.SHAMANIC_RITUAL);
        when(mockBuildingLast.getEvent()).thenReturn(mockBoostLast);
        when(mockBoostLast.isFirstOrLast()).thenReturn(false);

        // set the Mock Players
        when(mockPlayer1.getBuildings()).thenReturn(List.of(mockBuildingLast));
        when(mockPlayer1.getParameters()).thenReturn(Map.of(
                Parameter.SHAMAN, 3
        ));
        when(mockPlayer2.getParameters()).thenReturn(Map.of(
                Parameter.SHAMAN, 4
        ));
        when(mockPlayer3.getBuildings()).thenReturn(List.of(mockBuildingFirst));
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

    @Test
    void handleEvent_BuildingsNotUsed() {
        // set the Mock BuildingCards
        when(mockBuildingFirst.getEventType()).thenReturn(EventType.SHAMANIC_RITUAL);
        when(mockBuildingFirst.getEvent()).thenReturn(mockBoostFirst);
        when(mockBoostFirst.isFirstOrLast()).thenReturn(true);
        when(mockBuildingLast.getEventType()).thenReturn(EventType.SHAMANIC_RITUAL);
        when(mockBuildingLast.getEvent()).thenReturn(mockBoostLast);
        when(mockBoostLast.isFirstOrLast()).thenReturn(false);

        // set the Mock Players
        when(mockPlayer1.getBuildings()).thenReturn(List.of(mockBuildingFirst));
        when(mockPlayer1.getParameters()).thenReturn(Map.of(
                Parameter.SHAMAN, 3
        ));
        when(mockPlayer2.getParameters()).thenReturn(Map.of(
                Parameter.SHAMAN, 4
        ));
        when(mockPlayer3.getBuildings()).thenReturn(List.of(mockBuildingLast));
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
        verify(mockBoostFirst, never()).handleEvent(mockPlayer1);
        verify(mockPlayer1).updateStats(Parameter.PRESTIGE_POINTS, shamanicRitualCard.getLostPrestigePoints());
        verify(mockBoostLast, never()).handleEvent(mockPlayer3);
        verify(mockPlayer3).updateStats(Parameter.PRESTIGE_POINTS, shamanicRitualCard.getGainedPrestigePoints());
        verify(mockPlayer4).updateStats(Parameter.PRESTIGE_POINTS, shamanicRitualCard.getGainedPrestigePoints());
    }

    @Test
    void handleEvent_BuildingsWrong() {
        // set the Mock BuildingCards
        when(mockBuildingFirst.getEventType()).thenReturn(EventType.SUSTENANCE);
        when(mockBuildingLast.getEventType()).thenReturn(EventType.SUSTENANCE);

        // set the Mock Players
        when(mockPlayer1.getBuildings()).thenReturn(List.of(mockBuildingLast));
        when(mockPlayer1.getParameters()).thenReturn(Map.of(
                Parameter.SHAMAN, 3
        ));
        when(mockPlayer2.getParameters()).thenReturn(Map.of(
                Parameter.SHAMAN, 4
        ));
        when(mockPlayer3.getBuildings()).thenReturn(List.of(mockBuildingFirst));
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
        verifyNoInteractions(mockBoostFirst);
        verifyNoInteractions(mockBoostLast);
        verify(mockPlayer1).updateStats(Parameter.PRESTIGE_POINTS, shamanicRitualCard.getLostPrestigePoints());
        verify(mockPlayer3).updateStats(Parameter.PRESTIGE_POINTS, shamanicRitualCard.getGainedPrestigePoints());
        verify(mockPlayer4).updateStats(Parameter.PRESTIGE_POINTS, shamanicRitualCard.getGainedPrestigePoints());
    }

    @Test
    void getAttributes() {
        // set up the StingBuilders
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        // Act
        shamanicRitualCard.getAttributes(ln1, ln2, ln3);

        // assert
        assertFalse(ln1.toString().isEmpty());
        assertFalse(ln2.toString().isEmpty());
        assertFalse(ln3.toString().isEmpty());
    }

    @Test
    void getInfo() {
        assertEquals("This is a ShamanicRitual Event Card: when resolved, the player with the most amount of starts gains " +
                        shamanicRitualCard.getGainedPrestigePoints() +
                        " pP, the one with the least amount loses " +
                        shamanicRitualCard.getLostPrestigePoints() + " pP.",
                shamanicRitualCard.getInfo(new StringBuilder())
        );
    }

    @Test
    void getArt() {
        assertEquals(
                shamanicRitualCard.getGainedPrestigePoints() + "r" +
                        (shamanicRitualCard.getLostPrestigePoints() * -1) + "r0",
                shamanicRitualCard.getArt()
        );
    }
}