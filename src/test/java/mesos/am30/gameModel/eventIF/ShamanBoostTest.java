package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShamanBoostTest {

    @Mock
    private Player mockPlayer;

    @Test
    void isFirstOrLast() {
        ShamanBoost shamanBoostCard1 = new ShamanBoost(true);
        ShamanBoost shamanBoostCard2 = new ShamanBoost(false);
        assertTrue(shamanBoostCard1.isFirstOrLast());
        assertFalse(shamanBoostCard2.isFirstOrLast());
    }

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

    @Test
    void getAttributes() {
        ShamanBoost shamanBoostCard = new ShamanBoost(false);

        // set up the StingBuilders
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        // Act
        shamanBoostCard.getAttributes(ln1, ln2, ln3);

        // assert
        assertFalse(ln1.toString().isEmpty());
        assertTrue(ln2.toString().isEmpty());
        assertFalse(ln3.toString().isEmpty());
    }

    @Test
    void getInfo() {
        ShamanBoost shamanBoostCard1 = new ShamanBoost(true);
        ShamanBoost shamanBoostCard2 = new ShamanBoost(false);
        assertEquals("This Building gives a boost during the Shamanic Ritual Event; if the Player has the " +
                "most amount of stars, it doubles the won pP.",
                shamanBoostCard1.getInfo(new StringBuilder())
        );
        assertEquals("This Building gives a boost during the Shamanic Ritual Event; if the Player has the " +
                        "least amount of stars, it recovers the lost pP.",
                shamanBoostCard2.getInfo(new StringBuilder())
        );
    }

    @Test
    void getArt() {
        ShamanBoost shamanBoostCard1 = new ShamanBoost(true);
        ShamanBoost shamanBoostCard2 = new ShamanBoost(false);
        assertEquals("s2b", shamanBoostCard1.getArt());
        assertEquals("s0b", shamanBoostCard2.getArt());
    }
}