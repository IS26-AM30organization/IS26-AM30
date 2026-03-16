package mesos.am30.GameModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
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

    @BeforeEach
    void setUp() {
        shamanicRitualCard = new ShamanicRitual(4,-3,5);
        mockPlayers = List.of(mockPlayer1, mockPlayer2, mockPlayer3, mockPlayer4);
    }

    @Test
    void handleEvent_MAxMin() {
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


}