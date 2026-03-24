package mesos.am30.GameModel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShamanBoostTest {

    @Mock
    private Player mockPlayer;

    @Test
    void handleEvent_First() {
        // set the EventCard
        ShamanBoost shamanBoostCard = new ShamanBoost(true);

        // Act
        shamanBoostCard.setEventPrestigePoints(10);
        shamanBoostCard.handleEvent(mockPlayer);

        // Assert
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, 10);
    }

    @Test
    void handleEvent_Last() {
        // set the EventCard
        ShamanBoost shamanBoostCard = new ShamanBoost(false);

        // Act
        shamanBoostCard.setEventPrestigePoints(-10);
        shamanBoostCard.handleEvent(mockPlayer);

        // Assert
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, 10);
    }
}