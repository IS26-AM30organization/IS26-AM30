package mesos.am30.gameModel.card;

import mesos.am30.gameModel.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TileTest {
    Tile tile;

    @Mock Player player;

    @BeforeEach
    void setUp() {
        tile = new Tile(1,2,0);
    }

    @Test
    void getCurrentPlayer_setCurrentPlayer() {
        tile.setCurrentPlayer(player);
        assertEquals(Optional.ofNullable(player), tile.getCurrentPlayer());
    }

    @Test
    void clearCurrentPlayer() {
        tile.setCurrentPlayer(player);
        tile.clearCurrentPlayer();
        assertEquals(Optional.empty(), tile.getCurrentPlayer());
    }

    @Test
    void getUpArrows_NotNull() {
        assertEquals(1, tile.getUpArrows());
    }

    @Test
    void getUpArrows_Null() {
        Tile tileNull = new Tile(null,2,1);
        assertEquals(0, tileNull.getUpArrows());
    }

    @Test
    void getDownArrows_NotNull() {
        assertEquals(2, tile.getDownArrows());
    }

    @Test
    void getDownArrows_Null() {
        Tile tileNull = new Tile(3,null,1);
        assertEquals(0, tileNull.getDownArrows());
    }

    @Test
    void getFood() {
        assertEquals(0, tile.getFood());
    }

    @Test
    void createRow() {
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        tile.setCurrentPlayer(player);
        tile.createRow(ln1,ln2,ln3);

        assertFalse(ln1.toString().isEmpty());
        assertFalse(ln2.toString().isEmpty());
        assertFalse(ln3.toString().isEmpty());
    }

    @Test
    void createRow_AllNull() {
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        Tile tileNull = new Tile(null, null, null);
        tileNull.clearCurrentPlayer();
        tileNull.createRow(ln1,ln2,ln3);

        assertFalse(ln1.toString().isEmpty());
        assertFalse(ln2.toString().isEmpty());
        assertFalse(ln3.toString().isEmpty());
    }

    @Test
    void equals_Null() {
        boolean equals = tile.equals(null);
        assertFalse(equals);
    }

    @Test
    void equals_DifferentClasses() {
        boolean equals = tile.equals(player);
        assertFalse(equals);
    }

    @Test
    void equals_True() {
        Tile tile1 = new Tile(1,2,0);
        Tile tile2 = new Tile(1,2,0);

        // Act
        boolean equals = tile1.equals(tile2);

        // Assert
        assertTrue(equals);
    }

    @Test
    void equals_False_UpArrows() {
        Tile tile1 = new Tile(1,2,0);
        Tile tile2 = new Tile(2,2,0);

        // Act
        boolean equals = tile1.equals(tile2);

        // Assert
        assertFalse(equals);
    }

    @Test
    void equals_False_DownArrows() {
        Tile tile1 = new Tile(1,2,0);
        Tile tile2 = new Tile(1,1,0);

        // Act
        boolean equals = tile1.equals(tile2);

        // Assert
        assertFalse(equals);
    }

    @Test
    void equals_False_Food() {
        Tile tile1 = new Tile(1,2,0);
        Tile tile2 = new Tile(1,2,1);

        // Act
        boolean equals = tile1.equals(tile2);

        // Assert
        assertFalse(equals);
    }

    @Test
    void equals_False_CurrentPlayer() {
        Tile tile1 = new Tile(1,2,0);
        Tile tile2 = new Tile(1,2,0);
        tile1.setCurrentPlayer(player);

        // Act
        boolean equals = tile1.equals(tile2);

        // Assert
        assertFalse(equals);
    }
}