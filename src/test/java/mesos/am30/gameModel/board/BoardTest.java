package mesos.am30.gameModel.board;

import mesos.am30.gameModel.EventType;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Card;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.Tile;
import mesos.am30.gameModel.card.EventCard;
import mesos.am30.gameModel.eventIF.FullSet;
import mesos.am30.gameModel.eventIF.Sustenance;
import mesos.am30.client.IF_GameView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BoardTest {
    private Board boardOf2;
    private Board boardOf3;
    private Board boardOf4;
    private Board boardOf5;

    @Mock
    private Player p1;
    @Mock
    private Player p2;
    @Mock
    private Player p3;
    @Mock
    private Player p4;
    @Mock
    private Player p5;
    @Mock
    private IF_GameView v1;
    @Mock
    private IF_GameView v2;
    @Mock
    private IF_GameView v3;
    @Mock
    private IF_GameView v4;
    @Mock
    FullSet fullSet;
    @Mock
    private Sustenance sustenance;
    @Mock
    private CharacterCard b;
    @Mock
    private CharacterCard h;
    @Mock
    private CharacterCard i1;
    @Mock
    private CharacterCard i2;
    @Mock
    private BuildingCard b1;
    @Mock
    private BuildingCard b2;
    @Mock
    private EventCard diNap;
    @Mock
    private GameManager game;

    @BeforeEach
    void setUp() {
        boardOf2 = new Board(List.of(p1, p2),List.of(v1,v2));
        boardOf3 = new Board(List.of(p1, p2, p3),List.of(v1,v2,v3));
        boardOf4 = new Board(List.of(p1, p2, p3, p4),List.of(v1,v2,v3,v4));
        boardOf5 = new Board(List.of(p1, p2, p3, p4, p5),List.of(v1,v2,v3,v4));
    }

    //eq("String") is needed as Mockito requires either none or all matchers in method call
    @Test
    void deckTest(){
        try (MockedStatic<Utility> utility = mockStatic(Utility.class)) {
            utility.when(()->Utility.cardLoader(eq("characters.json"),anyInt(),any())).thenReturn(new ArrayList<>(
                            List.of(
                                    new CharacterCard(1, Parameter.ARTIST, 1, 0, 1),
                                    new CharacterCard(1, Parameter.HUNTER, 5, 0,2),
                                    new CharacterCard(1, Parameter.ARTIST, 2, 3,3 ),
                                    new CharacterCard(1, Parameter.ARTIST, 1, 0,4),
                                    new CharacterCard(1, Parameter.HUNTER, 5, 0,5),
                                    new CharacterCard(1, Parameter.ARTIST, 2, 3,6),
                                    new CharacterCard(1, Parameter.ARTIST, 1, 0,7),
                                    new CharacterCard(1, Parameter.HUNTER, 5, 0,8),
                                    new CharacterCard(1, Parameter.ARTIST, 2, 3,9),
                                    new CharacterCard(1, Parameter.ARTIST, 1, 0,10),
                                    new CharacterCard(1, Parameter.ARTIST, 1, 0,11),
                                    new CharacterCard(1, Parameter.HUNTER, 5, 0,12),
                                    new CharacterCard(2, Parameter.HUNTER, 2, 0,13)
                            )
                    )
            );

            utility.when(()->Utility.cardLoader(eq("buildings.json"),anyInt(),any())).thenReturn(new ArrayList<>(
                            List.of(
                                    new BuildingCard(1, fullSet, EventType.ROUND, 2, 2, 14),
                                    new BuildingCard(2, fullSet, EventType.ROUND, 2, 2, 15)
                            )
                    )
            );

            utility.when(()->Utility.cardLoader(eq("events.json"),anyInt(),any())).thenReturn(new ArrayList<>(
                            List.of(
                                    new EventCard(1, sustenance, 16),
                                    new EventCard(2, sustenance,17)
                            )
                    )
            );

            utility.when(()->Utility.cardLoader(eq("tiles.json"),anyInt(),any())).thenReturn(new ArrayList<>());

            utility.when(()->Utility.cardLoader(eq("finals.json"),anyInt(),any())).thenReturn(new ArrayList<>());

            boardOf2.prepare();
            boardOf3.prepare();
            boardOf4.prepare();
            boardOf5.prepare();

            boardOf2.start();
            boardOf3.start();
            boardOf4.start();
            boardOf5.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(6,boardOf2.getUpperRow().size());
        assertEquals(7,boardOf3.getUpperRow().size());
        assertEquals(8,boardOf4.getUpperRow().size());
        assertEquals(7,boardOf5.getUpperRow().size());
        assertEquals(3,boardOf2.getLowerRow().size());
        assertEquals(4,boardOf3.getLowerRow().size());
        assertEquals(5,boardOf4.getLowerRow().size());
        assertEquals(6,boardOf5.getLowerRow().size());
    }


    //checks nextRound, nextEra and end
    @Test
    void nextRound() throws IOException {
        boardOf3.getLowerRow().add(
                new CharacterCard(1, Parameter.GATHERER, 1, 0, 1));
        //when(diNap.getEvent()).thenReturn(sustenance);
        boardOf3.getUpperRow().addAll(List.of(
                new CharacterCard(1, Parameter.GATHERER, 1, 0, 2),
                new CharacterCard(1, Parameter.GATHERER, 1, 0,3),
                diNap
        ));
        boardOf3.getDecks().add(new ArrayList<>(List.of(
                new CharacterCard(1, Parameter.GATHERER, 1, 0, 4),
                new CharacterCard(1, Parameter.GATHERER, 1, 0, 5),
                new CharacterCard(1, Parameter.GATHERER, 1, 0, 6),
                new CharacterCard(1, Parameter.GATHERER, 1, 0, 7),
                new CharacterCard(1, Parameter.GATHERER, 1, 0, 8),
                new CharacterCard(1, Parameter.GATHERER, 1, 0, 9),
                new CharacterCard(1, Parameter.GATHERER, 1, 0, 10),
                new CharacterCard(1, Parameter.GATHERER, 1, 0, 11),
                new CharacterCard(1, Parameter.GATHERER, 1, 0, 12),
                new CharacterCard(1, Parameter.GATHERER, 1, 0, 13),
                new CharacterCard(1, Parameter.GATHERER, 1, 0, 14)
        )));
        boardOf3.getDecks().add(new ArrayList<>(List.of(
                new CharacterCard(2, Parameter.GATHERER, 1, 0, 15),
                new CharacterCard(2, Parameter.GATHERER, 1, 0, 16),
                new CharacterCard(2, Parameter.GATHERER, 1, 0, 17)
        )));

        List.of(p1,p2,p3).forEach(p -> when(p.getBuildings()).thenReturn(new HashSet<>()));

        boardOf3.testGame(game);

        boardOf3.nextRound();
        assertEquals(7,boardOf3.getUpperRow().size());
        assertEquals(3, boardOf3.getLowerRow().size());
        boardOf3.nextRound();
        assertEquals(7,boardOf3.getUpperRow().size());
        assertEquals(7, boardOf3.getLowerRow().size());
        boardOf3.nextRound();
        assertEquals(0,boardOf3.getUpperRow().size());
        assertEquals(0, boardOf3.getLowerRow().size());
    }

    @Test
    void nextRound_TwoPlayers_IncompleteDeck_EndsGame() throws IOException {
        boardOf2.getDecks().add(new ArrayList<>(List.of(
                new CharacterCard(1, Parameter.GATHERER, 1, 0, 101),
                new CharacterCard(1, Parameter.GATHERER, 1, 0, 102),
                new CharacterCard(1, Parameter.GATHERER, 1, 0, 103)
        )));
        when(p1.getBuildings()).thenReturn(new HashSet<>());
        when(p2.getBuildings()).thenReturn(new HashSet<>());
        boardOf2.testGame(game);

        boardOf2.nextRound();

        verify(game).sendClientEnd();
    }

    @Test
    void nextRound_TwoPlayers_FullLastEraDeck_DoesNotEndImmediately() throws IOException {
        List<Card> fullEraDeck = new ArrayList<>();
        for (int id = 101; id <= 106; id++)
            fullEraDeck.add(new CharacterCard(1, Parameter.GATHERER, 1, 0, id));
        boardOf2.getDecks().add(fullEraDeck);

        when(p1.getBuildings()).thenReturn(new HashSet<>());
        when(p2.getBuildings()).thenReturn(new HashSet<>());
        boardOf2.testGame(game);

        boardOf2.nextRound();

        verify(game, never()).sendClientEnd();
        assertEquals(6, boardOf2.getUpperRow().size());
        verify(game).iChangedTurn();
    }

    @Test
    void fullGame_TwoPlayers_TerminatesCorrectly() throws IOException {
        Player player1 = new Player("P1");
        Player player2 = new Player("P2");
        IF_GameView view1 = mock(IF_GameView.class);
        IF_GameView view2 = mock(IF_GameView.class);

        Board board = new Board(List.of(player1, player2), List.of(view1, view2));
        GameManager gm = new GameManager(board, List.of(player1, player2), List.of(view1, view2));
        board.testGame(gm);

        player1.updateStats(Parameter.FOOD, 5);
        player2.updateStats(Parameter.FOOD, 5);

        Tile tile1 = new Tile(1, 0, 0);
        Tile tile2 = new Tile(0, 0, 0);
        board.getTiles().addAll(List.of(tile1, tile2));

        CharacterCard c1 = new CharacterCard(1, Parameter.GATHERER, 1, 0, 1);
        CharacterCard c2 = new CharacterCard(1, Parameter.GATHERER, 1, 0, 2);
        CharacterCard c3 = new CharacterCard(1, Parameter.GATHERER, 1, 0, 3);
        board.getUpperRow().add(c1);
        board.getDecks().add(new ArrayList<>(List.of(c2, c3)));

        board.pickTile(player1, tile1);
        board.pickTile(player2, tile2);

        player1.decreaseRemainingUpMoves();
        boolean allDone = board.pickCard(player1, c1);

        assertTrue(allDone);
        board.nextRound();

        verify(view1).end();
        verify(view2).end();
    }

    @Test
    void pickCard() throws IOException {
        when(p2.getInventions()).thenReturn(new HashSet<>(10));
        p2.getInventions().add(3);

        boardOf5.testGame(game);

        boardOf5.getUpperRow().addAll(List.of(h,i2));
        boardOf5.getLowerRow().addAll(List.of(i1,b));
        boardOf5.getUpperBuildings().add(b1);
        boardOf5.getLowerBuildings().add(b2);

        assertEquals(2,boardOf5.getUpperRow().size());

        boardOf5.pickCard(p1,h);
        verify(p1).addCharacter(h);
        assertEquals(1,boardOf5.getUpperRow().size());

        boardOf5.pickCard(p2,h);
        assertEquals(1,boardOf5.getUpperRow().size());

        boardOf5.pickCard(p1,b);
        verify(p1).addCharacter(b);
        assertEquals(1,boardOf5.getUpperRow().size());
        assertEquals(1,boardOf5.getLowerRow().size());

        assertTrue(p2.getInventions().contains(3));
        assertFalse(p2.getInventions().contains(4));
        boardOf5.pickCard(p2,i1);
        verify(p2).addCharacter(i1);

        assertTrue(boardOf5.getUpperBuildings().contains(b1));
        boardOf5.pickCard(p1,b1);
        verify(p1).addBuilding(b1);
        assertFalse(boardOf5.getUpperBuildings().contains(b1));

        assertTrue(boardOf5.getLowerBuildings().contains(b2));
        boardOf5.pickCard(p1,b2);
        verify(p1).addBuilding(b2);
        assertFalse(boardOf5.getUpperBuildings().contains(b2));
    }
}