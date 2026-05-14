package mesos.am30.gameModel.card;

import mesos.am30.gameModel.board.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CardTest {
    private Card card;

    @BeforeEach
    void setUp() {
        card = new Card(1, 3) {
            @Override
            public void drawUp(Board board) {}

            @Override
            public void drawDown(Board board) {}

            @Override
            public void discard(Board board) {}

            @Override
            public void createRow(StringBuilder ln1, StringBuilder ln2, StringBuilder ln3) {}

            @Override
            public String getCardInfo(StringBuilder info) {return "";}

            @Override
            public String getArt() {return "";}
        };
    }

    @Test
    void getId() {
        assertEquals(3, card.getId());
    }

    @Test
    void getEra() {
        assertEquals(1, card.getEra());
    }

    @Test
    void isPickable() {
        assertFalse(card.isPickable());
    }

    @Test
    void getFrame() {
        assertEquals("", card.getFrame());
    }
}