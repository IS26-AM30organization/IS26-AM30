package mesos.am30.GameModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import java.util.*;


@ExtendWith(MockitoExtension.class)
class SustenanceTest {
    private Sustenance sustenanceCard;

    @Mock
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        sustenanceCard = new Sustenance(-4);
        Map<Parameter, List<CharacterCard>> tribe = new HashMap<>();
        tribe.put(Parameter.INVENTOR, mockCards(5));
        tribe.put(Parameter.BUILDER, mockCards(2));
        tribe.put(Parameter.GATHERER, mockCards(2));
        tribe.put(Parameter.SHAMAN, mockCards(1));
        when(mockPlayer.getTribe()).thenReturn(tribe);
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
        int foodDiscount = mockPlayer.getTribe().get(Parameter.GATHERER).size() * 3;
        when(mockPlayer.getParameters()).thenReturn(Map.of(
                Parameter.GATHERER, foodDiscount,
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
    }

    @Test
    void handleEvent_Perfect() {
        // set the Mock Player
        int foodDiscount = mockPlayer.getTribe().get(Parameter.GATHERER).size() * 3;
        when(mockPlayer.getParameters()).thenReturn(Map.of(
                Parameter.GATHERER, foodDiscount,
                Parameter.FOOD, 4,
                Parameter.PRESTIGE_POINTS, 10
        ));

        // Act
        sustenanceCard.handleEvent(mockPlayer);

        // Assert
        int foodCost = 0;
        for (Parameter p : mockPlayer.getTribe().keySet()) {
            foodCost += mockPlayer.getTribe().get(p).size();
        }
        verify(mockPlayer).updateStats(Parameter.FOOD, -4);
    }

    @Test
    void handleEvent_Negative() {
        // set the Mock Player
        int foodDiscount = mockPlayer.getTribe().get(Parameter.GATHERER).size() * 3;
        when(mockPlayer.getParameters()).thenReturn(Map.of(
                Parameter.GATHERER, foodDiscount,
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

}