package mesos.am30.gameModel.event;

import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.CharacterCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class FinalBuilderBoostTest {
    private FinalBuilderBoost testingCard;

    @Mock
    Player mockPlayer;
    @Mock
    CharacterCard mockCard1;
    @Mock
    CharacterCard mockCard2;

    @BeforeEach
    void setUp() {
        testingCard= new FinalBuilderBoost(2);
    }

    @Test
    void normalCase() {
        List<CharacterCard> builders=new ArrayList<>();
        builders.add(mockCard1);
        builders.add(mockCard2);
        when(mockCard1.getPrestigePoints()).thenReturn(3);
        when(mockCard2.getPrestigePoints()).thenReturn(2);
        when(mockPlayer.getTribe()).thenReturn(Map.of(
                Parameter.BUILDER, builders
        ));
        testingCard.handleEvent(mockPlayer);
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS,5);
    }
    @Test
    void nullCase() {

        when(mockPlayer.getTribe()).thenReturn(Map.of(
                Parameter.BUILDER, new ArrayList<>()
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
        assertEquals("This Building gives " + testingCard.getMultiplier() +
                        " x pP showed on owner's Builders once the game has ended.",
                testingCard.getInfo(new StringBuilder())
        );
    }

    @Test
    void getArt() {
        assertEquals("fb", testingCard.getArt());
    }
}