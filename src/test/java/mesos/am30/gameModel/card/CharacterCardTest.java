package mesos.am30.gameModel.card;

import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.board.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CharacterCardTest {
    @Mock private Board board;
    CharacterCard card1 = new CharacterCard(1, Parameter.ARTIST,10,11,200);
    CharacterCard card2 = new CharacterCard(1, Parameter.SHAMAN,10,11,100);
    CharacterCard card3 = new CharacterCard(1, Parameter.GATHERER,10,11,100);
    CharacterCard card4 = new CharacterCard(1, Parameter.HUNTER,10,11,100);
    CharacterCard card5 = new CharacterCard(1, Parameter.INVENTOR,10,11,100);
    CharacterCard card6 = new CharacterCard(1, Parameter.BUILDER,10,11,100);

    @BeforeEach
    void setUp() {
    }

    @Test
    void drawUp() {
        card1.drawUp(board);
        verify(board).drawUp(card1);
    }

    @Test
    void drawDown() {
        card1.drawDown(board);
        verify(board).drawDown(card1);
    }

    @Test
    void discard() {
        card1.discard(board);
        verify(board).discard(card1);
    }

    @Test
    void createRow() {
        StringBuilder roles = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder pps = new StringBuilder();

        card1.createRow(roles, ln2, pps);
        String role = roles.toString();
        String pp = pps.toString();
        assertTrue(role.contains("" + card1.getRole()));
        assertTrue(pp.contains("PP:11"));
        role = "";
        pp = "";

        card2.createRow(roles, ln2, pps);
        role = roles.toString();
        pp = pps.toString();
        assertTrue(role.contains("" + card2.getRole()));
        assertTrue(pp.contains("PP:11"));
        role = "";
        pp = "";

        card2.createRow(roles, ln2, pps);
        role = roles.toString();
        pp = pps.toString();
        assertTrue(role.contains("" + card2.getRole()));
        assertTrue(pp.contains("PP:11"));
        role = "";
        pp = "";

        card3.createRow(roles, ln2, pps);
        role = roles.toString();
        pp = pps.toString();
        assertTrue(role.contains("" + card3.getRole()));
        assertTrue(pp.contains("PP:11"));
        role = "";
        pp = "";

        card4.createRow(roles, ln2, pps);
        role = roles.toString();
        pp = pps.toString();
        assertTrue(role.contains("" + card4.getRole()));
        assertTrue(pp.contains("PP:11"));
        role = "";
        pp = "";

        card5.createRow(roles, ln2, pps);
        role = roles.toString();
        pp = pps.toString();
        assertTrue(role.contains("" + card5.getRole()));
        assertTrue(pp.contains("PP:11"));
        role = "";
        pp = "";

        card6.createRow(roles, ln2, pps);
        role = roles.toString();
        pp = pps.toString();
        assertTrue(role.contains("" + card6.getRole()));
        assertTrue(pp.contains("PP:11"));
        role = "";
        pp = "";
    }
}