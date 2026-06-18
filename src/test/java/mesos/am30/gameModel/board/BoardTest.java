package mesos.am30.gameModel.board;

import mesos.am30.gameModel.EventType;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.SpecialBuff;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Card;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.Tile;
import mesos.am30.gameModel.card.EventCard;
import mesos.am30.gameModel.event.FullSet;
import mesos.am30.gameModel.event.Hunt;
import mesos.am30.gameModel.event.Sustenance;
import mesos.am30.common.interfaces.IF_GameView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BoardTest {
    private Board boardOf2;
    private Board boardOf3;
    private Board boardOf4;
    private Board boardOf5;

    @Mock
    private Player player1;
    @Mock
    private Player player2;
    @Mock
    private Player player3;
    @Mock
    private Player player4;
    @Mock
    private Player player5;
    @Mock
    FullSet fullSet;
    @Mock
    private Sustenance sustenanceEvent;
    @Mock
    private Hunt huntEvent;
    @Mock
    private CharacterCard builder;
    @Mock
    private CharacterCard hunter;
    @Mock
    private CharacterCard inventor1;
    @Mock
    private CharacterCard inventor2;
    @Mock
    private BuildingCard building1;
    @Mock
    private BuildingCard building2;
    @Mock
    private EventCard diNap;
    @Mock
    private GameManager game;

    @BeforeEach
    void setUp() {
        boardOf2 = new Board(List.of(player1, player2),List.of(
                mock(IF_GameView.class),
                mock(IF_GameView.class))
        );
        boardOf3 = new Board(List.of(player1, player2, player3),List.of(
                mock(IF_GameView.class),
                mock(IF_GameView.class),
                mock(IF_GameView.class)
        ));
        boardOf4 = new Board(List.of(player1, player2, player3, player4),List.of(
                mock(IF_GameView.class),
                mock(IF_GameView.class),
                mock(IF_GameView.class),
                mock(IF_GameView.class)
        ));
        boardOf5 = new Board(List.of(player1, player2, player3, player4, player5),List.of(
                mock(IF_GameView.class),
                mock(IF_GameView.class),
                mock(IF_GameView.class),
                mock(IF_GameView.class),
                mock(IF_GameView.class)
        ));
    }

    @Test
    void Board_WrongPlayersNumber() {
        Board boardOf1 = new Board(List.of(player1), List.of(mock(IF_GameView.class)));
        assertEquals(0, boardOf1.getTileBoost().length);
    }

    @Test
    void getTiles() {
        assertEquals(new ArrayList<>(), boardOf2.getTiles());
        assertEquals(new ArrayList<>(), boardOf3.getTiles());
        assertEquals(new ArrayList<>(), boardOf4.getTiles());
        assertEquals(new ArrayList<>(), boardOf5.getTiles());
    }

    @Test
    void getUpperRow() {
        assertEquals(new ArrayList<>(), boardOf2.getUpperRow());
        assertEquals(new ArrayList<>(), boardOf3.getUpperRow());
        assertEquals(new ArrayList<>(), boardOf4.getUpperRow());
        assertEquals(new ArrayList<>(), boardOf5.getUpperRow());
    }

    @Test
    void getUpperBuildings() {
        assertEquals(new ArrayList<>(), boardOf2.getUpperBuildings());
        assertEquals(new ArrayList<>(), boardOf3.getUpperBuildings());
        assertEquals(new ArrayList<>(), boardOf4.getUpperBuildings());
        assertEquals(new ArrayList<>(), boardOf5.getUpperBuildings());
    }

    @Test
    void getLowerRow() {
        assertEquals(new ArrayList<>(), boardOf2.getLowerRow());
        assertEquals(new ArrayList<>(), boardOf3.getLowerRow());
        assertEquals(new ArrayList<>(), boardOf4.getLowerRow());
        assertEquals(new ArrayList<>(), boardOf5.getLowerRow());
    }

    @Test
    void getLowerBuildings() {
        assertEquals(new ArrayList<>(), boardOf2.getLowerBuildings());
        assertEquals(new ArrayList<>(), boardOf3.getLowerBuildings());
        assertEquals(new ArrayList<>(), boardOf4.getLowerBuildings());
        assertEquals(new ArrayList<>(), boardOf5.getLowerBuildings());
    }

    @Test
    void getPlayersOrder() {
        assertEquals(List.of(player1, player2), boardOf2.getPlayersOrder());
        assertEquals(List.of(player1, player2, player3), boardOf3.getPlayersOrder());
        assertEquals(List.of(player1, player2, player3, player4), boardOf4.getPlayersOrder());
        assertEquals(List.of(player1, player2, player3, player4, player5), boardOf5.getPlayersOrder());
    }

    @Test
    void getCurrentPlayer() {
        assertEquals(boardOf2.getPlayersOrder().getFirst(), boardOf2.getCurrentPlayer());
        assertEquals(boardOf3.getPlayersOrder().getFirst(), boardOf3.getCurrentPlayer());
        assertEquals(boardOf4.getPlayersOrder().getFirst(), boardOf4.getCurrentPlayer());
        assertEquals(boardOf5.getPlayersOrder().getFirst(), boardOf5.getCurrentPlayer());
    }

    @Test
    void getCurrentMove() {
        // Act
        boardOf2.testGame(game);
        boardOf3.testGame(game);
        boardOf4.testGame(game);
        boardOf5.testGame(game);

        // Assert
        assertEquals(game.getCurrentMove(), boardOf2.getCurrentMove());
        assertEquals(game.getCurrentMove(), boardOf3.getCurrentMove());
        assertEquals(game.getCurrentMove(), boardOf4.getCurrentMove());
        assertEquals(game.getCurrentMove(), boardOf5.getCurrentMove());
    }

    @Test
    void deckTest(){
        try (MockedStatic<Utility> utility = mockStatic(Utility.class)) {
            utility.when(()->Utility.cardLoader(eq("characters.json"),anyInt(),any())).thenReturn(new ArrayList<>(
                            List.of(
                                    new CharacterCard(1, 1, Parameter.ARTIST, 1, 0),
                                    new CharacterCard(1, 2, Parameter.HUNTER, 5, 0),
                                    new CharacterCard(1, 3, Parameter.ARTIST, 2, 3),
                                    new CharacterCard(1, 4, Parameter.ARTIST, 1, 0),
                                    new CharacterCard(1, 5, Parameter.HUNTER, 5, 0),
                                    new CharacterCard(1, 6, Parameter.ARTIST, 2, 3),
                                    new CharacterCard(1, 7, Parameter.ARTIST, 1, 0),
                                    new CharacterCard(1, 8, Parameter.HUNTER, 5, 0),
                                    new CharacterCard(1, 9, Parameter.ARTIST, 2, 3),
                                    new CharacterCard(1, 10, Parameter.ARTIST, 1, 0),
                                    new CharacterCard(1, 11, Parameter.ARTIST, 1, 0),
                                    new CharacterCard(1, 12, Parameter.HUNTER, 5, 0),
                                    new CharacterCard(2, 13, Parameter.HUNTER, 2, 0)
                            )
                    )
            );

            utility.when(()->Utility.cardLoader(eq("buildings.json"),anyInt(),any())).thenReturn(new ArrayList<>(
                            List.of(
                                    new BuildingCard(1, 14, fullSet, EventType.ROUND, 2, 2),
                                    new BuildingCard(2, 15, fullSet, EventType.ROUND, 2, 2)
                            )
                    )
            );

            utility.when(()->Utility.cardLoader(eq("events.json"),anyInt(),any())).thenReturn(new ArrayList<>(
                            List.of(
                                    new EventCard(1, 16, sustenanceEvent),
                                    new EventCard(1, 17, huntEvent),
                                    new EventCard(2, 18, sustenanceEvent)
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
            fail();
        }

        assertEquals(6,boardOf2.getUpperRow().size());
        assertEquals(7,boardOf3.getUpperRow().size());
        assertEquals(8,boardOf4.getUpperRow().size());
        assertEquals(8,boardOf5.getUpperRow().size());
        assertEquals(3,boardOf2.getLowerRow().size());
        assertEquals(4,boardOf3.getLowerRow().size());
        assertEquals(5,boardOf4.getLowerRow().size());
        assertEquals(6,boardOf5.getLowerRow().size());
    }

    @Test
    void start_EdgeCases() {
        try (MockedStatic<Utility> utility = mockStatic(Utility.class)) {
            utility.when(()->Utility.cardLoader(eq("characters.json"),anyInt(),any()))
                    .thenReturn(new ArrayList<>(List.of(
                            new CharacterCard(1, 1, Parameter.ARTIST, 1, 0),
                            new CharacterCard(1, 2, Parameter.HUNTER, 5, 0),
                            new CharacterCard(1, 3, Parameter.ARTIST, 2, 3),
                            new CharacterCard(1, 4, Parameter.ARTIST, 1, 0),
                            new CharacterCard(1, 5, Parameter.HUNTER, 5, 0)
                    ))
            );

            utility.when(()->Utility.cardLoader(eq("buildings.json"),anyInt(),any()))
                    .thenReturn(new ArrayList<>(List.of(
                            new BuildingCard(1, 14, fullSet, EventType.ROUND, 2, 2),
                            new BuildingCard(2, 15, fullSet, EventType.ROUND, 2, 2)
                    ))
            );

            utility.when(()->Utility.cardLoader(eq("events.json"),anyInt(),any()))
                    .thenReturn(new ArrayList<>(List.of(
                            new EventCard(1, 16, huntEvent),
                            new EventCard(2, 17, sustenanceEvent)
                    ))
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
            fail();
        }

        assertEquals(3,boardOf2.getUpperRow().size());
        assertEquals(2,boardOf3.getUpperRow().size());
        assertEquals(1,boardOf4.getUpperRow().size());
        assertEquals(1,boardOf5.getUpperRow().size());
        assertEquals(3,boardOf2.getLowerRow().size());
        assertEquals(4,boardOf3.getLowerRow().size());
        assertEquals(5,boardOf4.getLowerRow().size());
        assertEquals(5,boardOf5.getLowerRow().size());
    }

    @Test
    void discard() {
        // set up Mock Rows
        List<Card> upperRow = boardOf2.getUpperRow();
        for (int i = 0; i < 4; i++) upperRow.add(mock(CharacterCard.class));
        for (int i = 0; i < 2; i++) upperRow.add(diNap);
        List<Card> lowerRow = boardOf2.getLowerRow();
        for (int i = 0; i < 2; i++) lowerRow.add(mock(CharacterCard.class));
        lowerRow.add(diNap);

        // Act - upperRow
        while(!upperRow.isEmpty()) boardOf2.discard(upperRow.getFirst());
        assertEquals(0, boardOf2.getUpperRow().size());
        assertEquals(3, boardOf2.getLowerRow().size());

        // Act - lowerRow
        while (!lowerRow.isEmpty()) boardOf2.discard(lowerRow.getFirst());
        assertEquals(0, boardOf3.getUpperRow().size());
        assertEquals(0, boardOf4.getLowerRow().size());
    }

    @Test
    void discard_Building() {
        // set up Mock Rows
        List<BuildingCard> upperBuildings = boardOf2.getUpperBuildings();
        for (int i = 0; i < 4; i++) upperBuildings.add(mock(BuildingCard.class));
        List<BuildingCard> lowerBuildings = boardOf2.getLowerBuildings();
        for (int i = 0; i < 2; i++) lowerBuildings.add(mock(BuildingCard.class));

        // Act - upperBuildings
        while(!upperBuildings.isEmpty()) boardOf2.discard(upperBuildings.getFirst());
        assertEquals(0, boardOf2.getUpperBuildings().size());
        assertEquals(2, boardOf2.getLowerBuildings().size());

        // Act - lowerBuildings
        while (!lowerBuildings.isEmpty()) boardOf2.discard(lowerBuildings.getFirst());
        assertEquals(0, boardOf3.getUpperBuildings().size());
        assertEquals(0, boardOf4.getLowerBuildings().size());
    }

    //checks nextRound, nextEra and end
    @Test
    void nextRound() throws IOException {
        // set up Mock Scores
        boardOf3.testGame(game);
        when(player1.getParameters()).thenReturn(Map.of(
                Parameter.PRESTIGE_POINTS, 10
        ));
        when(player2.getParameters()).thenReturn(Map.of(
                Parameter.PRESTIGE_POINTS, 5
        ));
        when(player3.getParameters()).thenReturn(Map.of(
                Parameter.PRESTIGE_POINTS, 30
        ));

        // set up Rows
        boardOf3.getLowerRow().add(
                new CharacterCard(1, 1, Parameter.GATHERER, 1, 0));
        boardOf3.getUpperRow().addAll(List.of(
                new CharacterCard(1, 2, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, 3, Parameter.GATHERER, 1, 0),
                diNap
        ));

        // set up Decks
        boardOf3.getDecks().add(new ArrayList<>(List.of(
                new CharacterCard(1, 4, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, 5, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, 6, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, 7, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, 8, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, 9, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, 10, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, 11, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, 12, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, 13, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, 14, Parameter.GATHERER, 1, 0)
        )));
        boardOf3.getDecks().add(new ArrayList<>(List.of(
                new CharacterCard(2, 15, Parameter.GATHERER, 1, 0),
                new CharacterCard(2, 16, Parameter.GATHERER, 1, 0),
                new CharacterCard(2, 17, Parameter.GATHERER, 1, 0)
        )));
        boardOf3.getBuildingDecks().add(List.of(
                building1,
                building2
        ));

        // set up Mock Buildings
        when(building1.getEventType()).thenReturn(EventType.ONETIME);
        when(building1.getEvent()).thenReturn(huntEvent);
        when(building2.getEventType()).thenReturn(EventType.FINAL);
        when(building2.getEvent()).thenReturn(sustenanceEvent);
        when(player1.getBuildings()).thenReturn(List.of(building1, building2));
        List.of(player2,player3).forEach(p -> when(p.getBuildings()).thenReturn(new ArrayList<>()));

        // Act - nextRound Era 1
        boardOf3.nextRound();
        assertEquals(7,boardOf3.getUpperRow().size());
        assertEquals(3, boardOf3.getLowerRow().size());
        verify(building1, times(1)).getEventType();
        verify(huntEvent, times(1)).handleEvent(player1);
        verify(building2, times(1)).getEventType();
        verifyNoInteractions(sustenanceEvent);
        verify(player1, times(1)).getSpecialBuffs();
        verify(player1, never()).updateStats(Parameter.FOOD, 1);
        verify(player1, never()).removeBuff(SpecialBuff.ADDITIONAL_FOOD_TILE);
        verify(player2, never()).getSpecialBuffs();
        verify(player3, never()).getSpecialBuffs();

        // Act - nextRound Era 2
        when(player1.getSpecialBuffs()).thenReturn(Set.of(SpecialBuff.ADDITIONAL_FOOD_TILE));
        boardOf3.nextRound();
        assertEquals(7,boardOf3.getUpperRow().size());
        assertEquals(7, boardOf3.getLowerRow().size());
        verify(building1, times(2)).getEventType();
        verify(huntEvent, times(2)).handleEvent(player1);
        verify(building2, times(2)).getEventType();
        verifyNoInteractions(sustenanceEvent);
        verify(player1, times(2)).getSpecialBuffs();
        verify(player1, times(1)).updateStats(Parameter.FOOD, 1);
        verify(player1, times(1)).removeBuff(SpecialBuff.ADDITIONAL_FOOD_TILE);
        verify(player2, never()).getSpecialBuffs();
        verify(player3, never()).getSpecialBuffs();

        // Act - nextRound end
        boardOf3.nextRound();
        assertEquals(0,boardOf3.getUpperRow().size());
        assertEquals(0, boardOf3.getLowerRow().size());
        verify(building1, times(4)).getEventType();
        verify(huntEvent, times(3)).handleEvent(player1);
        verify(building2, times(4)).getEventType();
        verify(sustenanceEvent, times(1)).handleEvent(player1);
        verify(player1, times(2)).getSpecialBuffs();
        verify(player1, times(1)).updateStats(Parameter.FOOD, 1);
        verify(player1, times(1)).removeBuff(SpecialBuff.ADDITIONAL_FOOD_TILE);
        verify(player2, never()).getSpecialBuffs();
        verify(player3, never()).getSpecialBuffs();
    }

    @Test
    void nextRound_endGame() throws IOException {
        // set up Mock Scores
        when(player1.getParameters()).thenReturn(Map.of(
                Parameter.PRESTIGE_POINTS, 10
        ));
        when(player2.getParameters()).thenReturn(Map.of(
                Parameter.PRESTIGE_POINTS, 5
        ));

        // set up Board
        boardOf2.testGame(game);
        boardOf2.getPlayersOrder().clear();
        boardOf2.getDecks().add(new ArrayList<>(List.of()));

        // Act
        boardOf2.nextRound();

        // Assert
        verify(game).sendClientEnd();
    }

    @Test
    void nextRound_TwoPlayers_IncompleteDeck_EndsGame() throws IOException {
        // set up Mock Scores
        when(player1.getParameters()).thenReturn(Map.of(
                Parameter.PRESTIGE_POINTS, 10
        ));
        when(player2.getParameters()).thenReturn(Map.of(
                Parameter.PRESTIGE_POINTS, 5
        ));

        // set up Board
        boardOf2.getDecks().add(new ArrayList<>(List.of(
                new CharacterCard(1, 101, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, 102, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, 103, Parameter.GATHERER, 1, 0)
        )));
        when(player1.getBuildings()).thenReturn(new ArrayList<>());
        when(player2.getBuildings()).thenReturn(new ArrayList<>());
        boardOf2.testGame(game);

        // Act
        boardOf2.nextRound();

        // Assert
        verify(game).sendClientEnd();
    }

    @Test
    void nextRound_TwoPlayers_FullLastEraDeck_DoesNotEndImmediately() throws IOException {
        List<Card> fullEraDeck = new ArrayList<>();
        for (int id = 101; id <= 106; id++)
            fullEraDeck.add(new CharacterCard(1, id, Parameter.GATHERER, 1, 0));
        boardOf2.getDecks().add(fullEraDeck);

        when(player1.getBuildings()).thenReturn(new ArrayList<>());
        when(player2.getBuildings()).thenReturn(new ArrayList<>());
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

        CharacterCard c1 = new CharacterCard(1, 1, Parameter.GATHERER, 1, 0);
        CharacterCard c2 = new CharacterCard(1, 2, Parameter.GATHERER, 1, 0);
        CharacterCard c3 = new CharacterCard(1, 3, Parameter.GATHERER, 1, 0);
        board.getUpperRow().add(c1);
        board.getDecks().add(new ArrayList<>(List.of(c2, c3)));

        board.pickTile(player1, tile1);
        board.pickTile(player2, tile2);

        player1.decreaseRemainingUpMoves();
        boolean allDone = board.pickCard(player1, c1);

        assertTrue(allDone);
        board.nextRound();

        verify(view1).askShowRankings();
        verify(view2).askShowRankings();
    }

    @Test
    void pickCard() throws IOException {
        // set up the Board
        boardOf5.testGame(game);
        boardOf5.getUpperRow().addAll(List.of(hunter,inventor2));
        boardOf5.getLowerRow().addAll(List.of(inventor1,builder));
        boardOf5.getUpperBuildings().add(building1);
        boardOf5.getLowerBuildings().add(building2);

        // Act - upper Row
        boardOf5.pickCard(player1,hunter);
        verify(player1).addCharacter(hunter);
        assertFalse(boardOf5.getUpperRow().contains(hunter));
        assertEquals(1,boardOf5.getUpperRow().size());
        assertEquals(2,boardOf5.getLowerRow().size());
        assertEquals(1,boardOf5.getUpperBuildings().size());
        assertEquals(1,boardOf5.getLowerBuildings().size());

        // Act - not valid
        boardOf5.pickCard(player2,hunter);
        verify(player2, never()).addCharacter(hunter);
        assertEquals(1,boardOf5.getUpperRow().size());
        assertEquals(2,boardOf5.getLowerRow().size());
        assertEquals(1,boardOf5.getUpperBuildings().size());
        assertEquals(1,boardOf5.getLowerBuildings().size());

        // Act - lower row
        boardOf5.pickCard(player1,builder);
        verify(player1).addCharacter(builder);
        assertFalse(boardOf5.getLowerRow().contains(builder));
        assertEquals(1,boardOf5.getUpperRow().size());
        assertEquals(1,boardOf5.getLowerRow().size());
        assertEquals(1,boardOf5.getUpperBuildings().size());
        assertEquals(1,boardOf5.getLowerBuildings().size());

        // Act - add invention
        boardOf5.pickCard(player2,inventor1);
        verify(player2).addCharacter(inventor1);
        assertFalse(boardOf5.getLowerRow().contains(inventor1));
        assertEquals(1,boardOf5.getUpperRow().size());
        assertEquals(0,boardOf5.getLowerRow().size());
        assertEquals(1,boardOf5.getUpperBuildings().size());
        assertEquals(1,boardOf5.getLowerBuildings().size());

        // Act - upper Building
        boardOf5.pickCard(player1,building1);
        verify(player1).addBuilding(building1);
        assertFalse(boardOf5.getUpperBuildings().contains(building1));
        assertEquals(1,boardOf5.getUpperRow().size());
        assertEquals(0,boardOf5.getLowerRow().size());
        assertEquals(0,boardOf5.getUpperBuildings().size());
        assertEquals(1,boardOf5.getLowerBuildings().size());

        // Act - not valid
        boardOf5.pickCard(player2,building1);
        verify(player2, never()).addBuilding(building1);
        assertEquals(1,boardOf5.getUpperRow().size());
        assertEquals(0,boardOf5.getLowerRow().size());
        assertEquals(0,boardOf5.getUpperBuildings().size());
        assertEquals(1,boardOf5.getLowerBuildings().size());

        // Act - lower Building
        assertTrue(boardOf5.getLowerBuildings().contains(building2));
        boardOf5.pickCard(player1,building2);
        verify(player1).addBuilding(building2);
        assertFalse(boardOf5.getUpperBuildings().contains(building2));
        assertEquals(1,boardOf5.getUpperRow().size());
        assertEquals(0,boardOf5.getLowerRow().size());
        assertEquals(0,boardOf5.getUpperBuildings().size());
        assertEquals(0,boardOf5.getLowerBuildings().size());
    }
}