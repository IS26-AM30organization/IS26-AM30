package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.SpecialBuff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TileBoostTest {

    @Mock
    private Player player;

    @Test
    void handleEvent_BasicTest() {
        TileBoost tileBoostMove = new TileBoost(SpecialBuff.ADDITIONAL_UP_TILE);
        tileBoostMove.handleEvent(player);
        verify(player).updateStats(SpecialBuff.ADDITIONAL_UP_TILE);

        org.mockito.Mockito.clearInvocations(player);

        TileBoost tileBoostFood = new TileBoost(SpecialBuff.ADDITIONAL_FOOD_TILE);
        tileBoostFood.handleEvent(player);
        verify(player).updateStats(SpecialBuff.ADDITIONAL_FOOD_TILE);
    }

    @Test
    void getAttributes_ADDITIONAL_UP_TILE() {
        TileBoost tileBoost = new TileBoost(SpecialBuff.ADDITIONAL_UP_TILE);

        // set up the StingBuilders
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        // Act
        tileBoost.getAttributes(ln1, ln2, ln3);

        // assert
        assertFalse(ln1.toString().isEmpty());
        assertTrue(ln2.toString().isEmpty());
        assertFalse(ln3.toString().isEmpty());
    }

    @Test
    void getAttributes_ADDITIONAL_FOOD_TILE() {
        TileBoost tileBoost = new TileBoost(SpecialBuff.ADDITIONAL_FOOD_TILE);

        // set up the StingBuilders
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        // Act
        tileBoost.getAttributes(ln1, ln2, ln3);

        // assert
        assertFalse(ln1.toString().isEmpty());
        assertTrue(ln2.toString().isEmpty());
        assertFalse(ln3.toString().isEmpty());
    }

    @Test
    void getInfo() {
        TileBoost foodBoost = new TileBoost(SpecialBuff.ADDITIONAL_FOOD_TILE);
        TileBoost moveUpBoost = new TileBoost(SpecialBuff.ADDITIONAL_UP_TILE);
        assertEquals("This Building grants its owner 1 more food if his totem is then placed on a food tile.", foodBoost.getInfo(new StringBuilder()));
        assertEquals("This Building grants its owner 1 more UpMove at the end of the round.", moveUpBoost.getInfo(new StringBuilder()));
    }

    @Test
    void getArt() {
        assertEquals("tb", new TileBoost(SpecialBuff.ADDITIONAL_FOOD_TILE).getArt());
    }
}

