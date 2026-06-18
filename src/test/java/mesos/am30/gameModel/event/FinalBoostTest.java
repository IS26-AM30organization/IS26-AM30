package mesos.am30.gameModel.event;

import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.CharacterCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@ExtendWith(MockitoExtension.class)
class FinalBoostTest {
    private FinalBoost testingCard;
    @Mock
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        testingCard=new FinalBoost(Parameter.GATHERER,2);
    }

    @Test
    void normalCase() {
        List<CharacterCard> gatherers=new ArrayList<>();
        for (int i=0; i<4; i++)
            gatherers.add(mock(CharacterCard.class));
        when(mockPlayer.getTribe()).thenReturn(Map.of(
                Parameter.GATHERER, gatherers
        ));
        testingCard.handleEvent(mockPlayer);

        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, 8);
    }

    @Test
    void nullCase() {

        when(mockPlayer.getTribe()).thenReturn(Map.of(
                Parameter.GATHERER, new ArrayList<>()
        ));
        testingCard.handleEvent(mockPlayer);

        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, 0);
    }

    @Test
    void getAttributes() {
        // set up the StingBuilders
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        // Act
        testingCard.getAttributes(ln1, ln2, ln3);

        // assert
        assertFalse(ln1.toString().isEmpty());
        assertTrue(ln2.toString().isEmpty());
        assertFalse(ln3.toString().isEmpty());
    }

    @Test
    void getInfo() {
        assertEquals("This Building gives x" + testingCard.getMultiplier() +
                " pP for #" + testingCard.getTarget() + " Characters once the game has ended.",
            testingCard.getInfo(new StringBuilder())
        );
    }

    @Test
    void getArt() {
        assertEquals(testingCard.getTarget().name().toLowerCase().charAt(0) + "b", testingCard.getArt());
    }
}