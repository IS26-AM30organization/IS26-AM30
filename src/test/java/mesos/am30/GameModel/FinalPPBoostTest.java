package mesos.am30.GameModel;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class FinalPPBoostTest {
    private FinalPPBoost testingCard;

    @Mock
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        testingCard = new FinalPPBoost(25);
    }

    @Test
    void handleEvent() {
        testingCard.handleEvent(mockPlayer);
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, 25);
    }
}