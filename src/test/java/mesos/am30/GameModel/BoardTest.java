package mesos.am30.GameModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BoardTest {
    private Board boardOf1;
    private Board boardOf2;
    private Board boardOf3;
    private Board boardOf4;
    private Board boardOf5;
    private Board boardOf6;

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
    FullSet fullSet;
    @Mock
    private Sustenance sustenance;

    @BeforeEach
    void setUp() {
        boardOf2 = new Board(List.of(p1, p2));
        boardOf3 = new Board(List.of(p1, p2, p3));
        boardOf4 = new Board(List.of(p1, p2, p3, p4));
        boardOf5 = new Board(List.of(p1, p2, p3, p4, p5));
    }

    //eq("String") is needed as Mockito requires either none or all matchers in method call
    @Test
    void deckTest(){
        try (MockedStatic<Utility> utility = mockStatic(Utility.class)) {
            utility.when(()->Utility.cardLoader(eq("characters.json"),anyInt(),any())).thenReturn(new ArrayList<>(
                            List.of(
                                    new CharacterCard(1, Parameter.ARTIST, 1, 0),
                                    new CharacterCard(1, Parameter.HUNTER, 5, 0),
                                    new CharacterCard(1, Parameter.ARTIST, 2, 3),
                                    new CharacterCard(1, Parameter.ARTIST, 1, 0),
                                    new CharacterCard(1, Parameter.HUNTER, 5, 0),
                                    new CharacterCard(1, Parameter.ARTIST, 2, 3),
                                    new CharacterCard(1, Parameter.ARTIST, 1, 0),
                                    new CharacterCard(1, Parameter.HUNTER, 5, 0),
                                    new CharacterCard(1, Parameter.ARTIST, 2, 3),
                                    new CharacterCard(1, Parameter.ARTIST, 1, 0),
                                    new CharacterCard(1, Parameter.ARTIST, 1, 0),
                                    new CharacterCard(1, Parameter.HUNTER, 5, 0),
                                    new CharacterCard(1, Parameter.ARTIST, 2, 3),
                                    new CharacterCard(2, Parameter.HUNTER, 2, 0)
                            )
                    )
            );

            utility.when(()->Utility.cardLoader(eq("buildings.json"),anyInt(),any())).thenReturn(new ArrayList<>(
                            List.of(
                                    new BuildingCard(1, fullSet, EventType.ROUND, 2, 2),
                                    new BuildingCard(2, fullSet, EventType.ROUND, 2, 2)
                            )
                    )
            );

            utility.when(()->Utility.cardLoader(eq("events.json"),anyInt(),any())).thenReturn(new ArrayList<>(
                            List.of(
                                    new EventCard(1, sustenance),
                                    new EventCard(2, sustenance)
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
        assertEquals(4,boardOf2.getLowerRow().size());
        assertEquals(5,boardOf3.getLowerRow().size());
        assertEquals(6,boardOf4.getLowerRow().size());
        assertEquals(7,boardOf5.getLowerRow().size());
    }


    //checks nextRound, nextEra and end
    @Test
    void nextRound() {
        boardOf3.getLowerRow().add(
                new CharacterCard(1, Parameter.GATHERER, 1, 0));
        boardOf3.getUpperRow().addAll(List.of(
                new CharacterCard(1, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, Parameter.GATHERER, 1, 0)
        ));
        boardOf3.getDecks().add(new ArrayList<>(List.of(
                new CharacterCard(1, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, Parameter.GATHERER, 1, 0),
                new CharacterCard(1, Parameter.GATHERER, 1, 0)
        )));
        boardOf3.getDecks().add(new ArrayList<>(List.of(
                new CharacterCard(2, Parameter.GATHERER, 1, 0),
                new CharacterCard(2, Parameter.GATHERER, 1, 0),
                new CharacterCard(2, Parameter.GATHERER, 1, 0)
        )));

        Map<Parameter, List<CharacterCard>> mockTribe = mock(Map.class);
        List.of(p1, p2, p3).forEach(p -> when(p.getTribe()).thenReturn(mockTribe));
        List.of(p1, p2, p3).forEach(p -> when(p.getTribe().get(Parameter.ARTIST)).thenReturn(new ArrayList<CharacterCard>()));
        List.of(p1, p2, p3).forEach(p -> when(p.getTribe().get(Parameter.BUILDER)).thenReturn(new ArrayList<CharacterCard>()));
        List.of(p1, p2, p3).forEach(p -> when(p.getTribe().get(Parameter.INVENTOR)).thenReturn(new ArrayList<CharacterCard>()));

        Map<Parameter, Integer> mockParameters = mock(Map.class);
        List.of(p1, p2, p3).forEach(p -> when(p.getParameters()).thenReturn(mockParameters));
        List.of(p1, p2, p3).forEach(p -> when(p.getParameters().get(Parameter.INVENTOR)).thenReturn(2));

        boardOf3.nextRound();
        assertEquals(7,boardOf3.getUpperRow().size());
        assertEquals(2, boardOf3.getLowerRow().size());
        boardOf3.nextRound();
        assertEquals(7,boardOf3.getUpperRow().size());
        assertEquals(7, boardOf3.getLowerRow().size());
        boardOf3.nextRound();
        assertEquals(0,boardOf3.getUpperRow().size());
        assertEquals(0, boardOf3.getLowerRow().size());
    }
}