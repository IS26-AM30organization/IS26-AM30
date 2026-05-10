package mesos.am30.gameModel.card;

import mesos.am30.gameModel.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class TileTest {
    @Mock Player player;
    Tile tile1 = new Tile(1,2,0);

    @Test
    void clearCurrentPlayer() {
        tile1.setCurrentPlayer(player);
        tile1.clearCurrentPlayer();
        assertEquals(tile1.getCurrentPlayer(), Optional.empty());
    }

    @Test
    void createRow() {
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        tile1.createRow(ln1,ln2,ln3);

        assertFalse(ln1.toString().isEmpty());
        assertFalse(ln2.toString().isEmpty());
    }
}