package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.CharacterCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FullSetFinalTest {
    private FullSetFinal testingCard;
    @Mock
    private Player mockPlayer;
    @Mock
    private CharacterCard mockCard;
    @BeforeEach
    void setUp() {
        testingCard=new FullSetFinal(6);
    }
    @Test
    void zeroFull() {
        Map<Parameter, List<CharacterCard>> tribe = Map.of(
                Parameter.INVENTOR, List.of(mockCard),
                Parameter.SHAMAN, List.of(mockCard, mockCard),
                Parameter.HUNTER, List.of(mockCard, mockCard)
        );
        when(mockPlayer.getTribe()).thenReturn(tribe);
        testingCard.handleEvent(mockPlayer);
        verify(mockPlayer, never()).updateStats(any(), anyInt());
    }

    @Test
    void oneFullSet_withExcesses() {
        Map<Parameter, List<CharacterCard>> tribe = Map.of(
                Parameter.INVENTOR, List.of(mockCard),
                Parameter.BUILDER, List.of(mockCard, mockCard),
                Parameter.GATHERER, List.of(mockCard, mockCard),
                Parameter.ARTIST, List.of(mockCard, mockCard, mockCard),
                Parameter.SHAMAN, List.of(mockCard, mockCard),
                Parameter.HUNTER, List.of(mockCard, mockCard)
        );
        when(mockPlayer.getTribe()).thenReturn(tribe);
        testingCard.handleEvent(mockPlayer);
        verify(mockPlayer, times(1)).updateStats(Parameter.PRESTIGE_POINTS, 6);
    }

    @Test
    void twoFullSet(){
        Map<Parameter, List<CharacterCard>> tribe = Map.of(
                Parameter.INVENTOR, List.of(mockCard,mockCard),
                Parameter.BUILDER, List.of(mockCard, mockCard),
                Parameter.GATHERER, List.of(mockCard, mockCard),
                Parameter.ARTIST, List.of(mockCard, mockCard, mockCard),
                Parameter.SHAMAN, List.of(mockCard, mockCard),
                Parameter.HUNTER, List.of(mockCard, mockCard)
        );
        when(mockPlayer.getTribe()).thenReturn(tribe);
        testingCard.handleEvent(mockPlayer);
        verify(mockPlayer, times(1)).updateStats(Parameter.PRESTIGE_POINTS, 12);

    }
    @Test
    void fullSet_withResourcesToFilter() {
        Map<Parameter, List<CharacterCard>> tribe = Map.of(
                Parameter.INVENTOR, List.of(mockCard),
                Parameter.BUILDER, List.of(mockCard),
                Parameter.GATHERER, List.of(mockCard),
                Parameter.ARTIST, List.of(mockCard),
                Parameter.SHAMAN, List.of(mockCard),
                Parameter.HUNTER, List.of(mockCard),
                Parameter.FOOD, List.of(mockCard, mockCard, mockCard),
                Parameter.PRESTIGE_POINTS, List.of(mockCard)
        );
        when(mockPlayer.getTribe()).thenReturn(tribe);

        testingCard.handleEvent(mockPlayer);

        verify(mockPlayer, times(1)).updateStats(Parameter.PRESTIGE_POINTS, 6);
    }

    @Test
    void getAttributes() {
        // set up the StingBuilders
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        // Act
        testingCard.getAttributes(ln1, ln2, ln3);

        // assert
        assertFalse(ln1.toString().isEmpty());
        assertTrue(ln2.toString().isEmpty());
        assertFalse(ln3.toString().isEmpty());
    }

    @Test
    void getInfo() {
        assertEquals("This Building gives " + testingCard.getPpGain() +
                        " pP to its owner, for each set of 6 unique Characters he has collected during the game.",
                testingCard.getInfo(new StringBuilder())
        );
    }

    @Test
    void getArt() {
        assertEquals("sf", testingCard.getArt());
    }
}