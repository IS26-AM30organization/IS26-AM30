package mesos.am30.GameModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TileBoostTest {
    private TileBoost tileBoost;

    @Mock
    private Player player;

    @BeforeEach
    void setUp() {
    }

    @Test
    void handleEvent_BasicTest() {
        tileBoost = new TileBoost(SpecialBuff.ADDITIONAL_UP_TILE);
        tileBoost.handleEvent(player);
        verify(player).updateStats(SpecialBuff.ADDITIONAL_UP_TILE);

        org.mockito.Mockito.clearInvocations(player);

        tileBoost = new TileBoost(SpecialBuff.ADDITIONAL_FOOD_TILE);
        tileBoost.handleEvent(player);
        verify(player).updateStats(SpecialBuff.ADDITIONAL_FOOD_TILE);
    }
}

