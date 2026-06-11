package mesos.am30.gameModel.eventIF;

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

import java.util.*;

@ExtendWith(MockitoExtension.class)
class SustenanceTest {
    private Sustenance sustenanceCard;

    @Mock
    private Player mockPlayer;

    @Mock
    private BuildingCard mockBuilding;

    @Mock
    private StatsBoost mockStatsBoost;

    @BeforeEach
    void setUp() {
        sustenanceCard = new Sustenance(-4);
    }

    private Map<Parameter, List<CharacterCard>> setTribe() {
        Map<Parameter, List<CharacterCard>> tribe = new HashMap<>();
        tribe.put(Parameter.INVENTOR, mockCards(5));
        tribe.put(Parameter.BUILDER, mockCards(2));
        tribe.put(Parameter.GATHERER, mockCards(2));
        tribe.put(Parameter.SHAMAN, mockCards(1));
        return tribe;
    }

    private List<CharacterCard> mockCards(int count) {
        List<CharacterCard> cards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            cards.add(mock(CharacterCard.class));
        }
        return cards;
    }

    @Test
    void handleEvent_Positive() {
        // set the Mock Player
        when(mockPlayer.getTribe()).thenReturn(setTribe());
        int foodDiscount = mockPlayer.getTribe().get(Parameter.GATHERER).size() * 3;
        when(mockPlayer.getParameters()).thenReturn(Map.of(
                Parameter.GATHERER, -foodDiscount,
                Parameter.FOOD, 10,
                Parameter.PRESTIGE_POINTS, 10
        ));

        // Act
        sustenanceCard.handleEvent(mockPlayer);

        // Assert
        int foodCost = 0;
        for (Parameter p : mockPlayer.getTribe().keySet()) {
            foodCost += mockPlayer.getTribe().get(p).size();
        }
        verify(mockPlayer).updateStats(Parameter.FOOD, foodDiscount - foodCost);
        verify(mockPlayer,never()).updateStats(eq(Parameter.PRESTIGE_POINTS), anyInt());
    }

    @Test
    void handleEvent_Perfect() {
        // set the Mock Player
        when(mockPlayer.getTribe()).thenReturn(setTribe());
        int foodDiscount = mockPlayer.getTribe().get(Parameter.GATHERER).size() * 3;
        when(mockPlayer.getParameters()).thenReturn(Map.of(
                Parameter.GATHERER, -foodDiscount,
                Parameter.FOOD, 4,
                Parameter.PRESTIGE_POINTS, 10
        ));

        // Act
        sustenanceCard.handleEvent(mockPlayer);

        // Assert
        verify(mockPlayer).updateStats(Parameter.FOOD, -4);
        verify(mockPlayer,never()).updateStats(eq(Parameter.PRESTIGE_POINTS), anyInt());
    }

    @Test
    void handleEvent_Negative() {
        // set the Mock Player
        when(mockPlayer.getTribe()).thenReturn(setTribe());
        int foodDiscount = mockPlayer.getTribe().get(Parameter.GATHERER).size() * 3;
        when(mockPlayer.getParameters()).thenReturn(Map.of(
                Parameter.GATHERER, -foodDiscount,
                Parameter.FOOD, 2,
                Parameter.PRESTIGE_POINTS, 10
        ));

        // Act
        sustenanceCard.handleEvent(mockPlayer);

        // Assert
        int foodCost = 0;
        for (Parameter p : mockPlayer.getTribe().keySet()) {
            foodCost += mockPlayer.getTribe().get(p).size();
        }
        verify(mockPlayer).updateStats(Parameter.FOOD, -2);
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, sustenanceCard.getPrestigePoints() * Math.abs(2 - (foodCost - foodDiscount)));
    }

    @Test
    void handleEvent_Free() {
        // set the Mock Player
        when(mockPlayer.getTribe()).thenReturn(setTribe());
        when(mockPlayer.getParameters()).thenReturn(Map.of(
                Parameter.GATHERER, -12,
                Parameter.FOOD, 2,
                Parameter.PRESTIGE_POINTS, 10
        ));

        // Act
        sustenanceCard.handleEvent(mockPlayer);

        // Assert
        verify(mockPlayer, never()).updateStats(eq(Parameter.FOOD), anyInt());
        verify(mockPlayer,never()).updateStats(eq(Parameter.PRESTIGE_POINTS), anyInt());
    }

    @Test
    void handleEvent_BuildingsUsed() {
        // set the Mock Building
        when(mockBuilding.getEventType()).thenReturn(EventType.SUSTENANCE);
        when(mockBuilding.getEvent()).thenReturn(mockStatsBoost);

        // set the Mock Player
        when(mockPlayer.getTribe()).thenReturn(setTribe());
        int foodDiscount = -mockPlayer.getTribe().get(Parameter.GATHERER).size() * 3;
        when(mockPlayer.getBuildings()).thenReturn(List.of(mockBuilding));
        when(mockPlayer.getParameters()).thenReturn(Map.of(
                Parameter.GATHERER,-foodDiscount,
                Parameter.FOOD, 2,
                Parameter.PRESTIGE_POINTS, 10
        ));

        // Act
        sustenanceCard.handleEvent(mockPlayer);

        // Assert
        int foodCost = 0;
        for (Parameter p : mockPlayer.getTribe().keySet()) {
            foodCost += mockPlayer.getTribe().get(p).size();
        }
        verify(mockStatsBoost).handleEvent(mockPlayer);
        verify(mockPlayer).updateStats(Parameter.FOOD, -2);
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, sustenanceCard.getPrestigePoints() * Math.abs(2 - (foodCost - foodDiscount)));
    }

    @Test
    void handleEvent_BuildingsNotUsed() {
        // set the Mock Building
        when(mockBuilding.getEventType()).thenReturn(EventType.SHAMANIC_RITUAL);

        // set the Mock Player
        when(mockPlayer.getTribe()).thenReturn(setTribe());
        int foodDiscount = mockPlayer.getTribe().get(Parameter.GATHERER).size() * 3;
        when(mockPlayer.getBuildings()).thenReturn(List.of(mockBuilding));
        when(mockPlayer.getParameters()).thenReturn(Map.of(
                Parameter.GATHERER, -foodDiscount,
                Parameter.FOOD, 2,
                Parameter.PRESTIGE_POINTS, 10
        ));

        // Act
        sustenanceCard.handleEvent(mockPlayer);

        // Assert
        int foodCost = 0;
        for (Parameter p : mockPlayer.getTribe().keySet()) {
            foodCost += mockPlayer.getTribe().get(p).size();
        }
        verifyNoInteractions(mockStatsBoost);
        verify(mockPlayer).updateStats(Parameter.FOOD, -2);
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, sustenanceCard.getPrestigePoints() * Math.abs(2 - (foodCost - foodDiscount)));
    }

    @Test
    void getAttributes() {
        // set up the StingBuilders
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        // Act
        sustenanceCard.getAttributes(ln1, ln2, ln3);

        // assert
        assertFalse(ln1.toString().isEmpty());
        assertFalse(ln2.toString().isEmpty());
        assertTrue(ln3.toString().isEmpty());
    }

    @Test
    void getInfo() {
        assertEquals("This is a Sustenance Event Card: when resolved, each player must pay 1 food for each Character in player's tribe." +
                "\nIf player's food is not enough, he loses " + sustenanceCard.getPrestigePoints() +
                " for each Character remaining.",
                sustenanceCard.getInfo(new StringBuilder())
        );
    }

    @Test
    void getArt() {
        assertEquals("0s" + (sustenanceCard.getPrestigePoints() * -1) + "s1", sustenanceCard.getArt());
    }
}