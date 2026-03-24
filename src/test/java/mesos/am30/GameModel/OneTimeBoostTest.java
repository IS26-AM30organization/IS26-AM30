package mesos.am30.GameModel;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class OneTimeBoostTest {
    private OneTimeBoost testingPPCard;
    private OneTimeBoost testingShamanCard;

    @Mock
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        testingPPCard = new OneTimeBoost(25, Parameter.PRESTIGE_POINTS);
        testingShamanCard = new OneTimeBoost(3, Parameter.SHAMAN);
    }

    @Test
    void prestigeTest() {
        testingPPCard.handleEvent(mockPlayer);
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, 25);
    }

    @Test
    void shamanTest(){
        testingShamanCard.handleEvent(mockPlayer);
        verify(mockPlayer).updateStats(Parameter.SHAMAN, 3);
    }
}