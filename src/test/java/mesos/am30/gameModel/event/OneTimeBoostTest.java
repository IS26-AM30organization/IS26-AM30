package mesos.am30.gameModel.event;


import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void getAttributes() {
        // set up the StingBuilders
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        // Act
        testingPPCard.getAttributes(ln1, ln2, ln3);

        // assert
        assertFalse(ln1.toString().isEmpty());
        assertTrue(ln2.toString().isEmpty());
        assertFalse(ln3.toString().isEmpty());
    }

    @Test
    void getInfo() {
        assertEquals("This Building gives " + testingPPCard.getGain() + " stars during a Shamanic Ritual.",
                testingPPCard.getInfo(new StringBuilder())
        );
    }

    @Test
    void getArt() {
        assertEquals(testingPPCard.getTarget().name().toLowerCase().charAt(0) + "o", testingPPCard.getArt());
    }
}