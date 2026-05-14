package mesos.am30.gameModel.card;

import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.board.Board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
class CharacterCardTest {
    @Mock private Board board;

    private List<CharacterCard> cards;

    @BeforeEach
    void setUp() {
        cards = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            cards.add(new CharacterCard(1, 100 + i, Parameter.values()[i], 10, 11));
        }
    }

    @Test
    void getRole() {
        for (int i = 0; i < 6; i++) {
            assertEquals(Parameter.values()[i], cards.get(i).getRole());
        }
    }

    @Test
    void getValue() {
        for (CharacterCard card : cards) {
            assertEquals(10, card.getValue());
        }
    }

    @Test
    void getPrestigePoints() {
        for (CharacterCard card : cards) {
            assertEquals(11, card.getPrestigePoints());
        }
    }

    @Test
    void isPickable() {
        for (CharacterCard card : cards) {
            assertTrue(card.isPickable());
        }
    }

    @Test
    void drawUp() {
        for (CharacterCard card : cards) {
            card.drawUp(board);
            verify(board).drawUp(card);
        }
    }

    @Test
    void drawDown() {
        for (CharacterCard card : cards) {
            card.drawDown(board);
            verify(board).drawDown(card);
        }
    }

    @Test
    void discard() {
        for (CharacterCard card : cards) {
            card.discard(board);
            verify(board).discard(card);
        }
    }

    @Test
    void createRow_Normal() {
        StringBuilder roles = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder pps = new StringBuilder();

        for (CharacterCard card : cards) {
            card.createRow(roles, ln2, pps);
            String role = roles.toString();
            String pp = pps.toString();
            assertTrue(role.contains("" + card.getRole()));
            assertTrue(pp.contains("PP:11"));
        }
    }

    @Test
    void createRow_EmptyCharacterCard() {
        // set up the StingBuilders
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        // Act
        CharacterCard characterCard = new CharacterCard(1, 100, Parameter.ARTIST, 0, 0);
        characterCard.createRow(ln1,ln2,ln3);

        // assert
        assertFalse(ln1.toString().isEmpty());
        assertFalse(ln2.toString().isEmpty());
        assertFalse(ln3.toString().isEmpty());
    }

    @Test
    void createRow_Maximum_rowPP() {
        // set up the StingBuilders
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        // Act
        CharacterCard characterCard = new CharacterCard(1, 100, Parameter.ARTIST, 0, 123);
        characterCard.createRow(ln1,ln2,ln3);

        // assert
        assertFalse(ln1.toString().isEmpty());
        assertFalse(ln2.toString().isEmpty());
        assertFalse(ln3.toString().isEmpty());
    }

    @Test
    void getCardInfo() {
        StringBuilder info = new StringBuilder();

        // Act
        CharacterCard characterCard = new CharacterCard(1, 100, Parameter.ARTIST, 0, 123);
        characterCard.getCardInfo(info);

        // Assert
        assertEquals("Character", info.toString());
    }

    @Test
    void getArt() {
        for (CharacterCard card : cards) {
            String art = card.getRole().name().toLowerCase().charAt(0) + (card.getId() % 2 == 0 ? "f" : "m");
            assertEquals(art, card.getArt());
        }
    }

    @Test
    void getFrame() {
        cards.add(new CharacterCard(1, 1, Parameter.values()[1], -1, 11));
        for (CharacterCard card : cards) {
            String frame = card.getPrestigePoints() + "" + card.getRole().name().toLowerCase().charAt(0) + (card.getValue() < 0 ? card.getValue() * (-1) : card.getValue());
            assertEquals(frame, card.getFrame());
        }
    }
}