package mesos.am30.gameModel;

import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.CharacterCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.io.PrintStream;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerTest {
    private Player player;
    @Mock
    private BuildingCard mockBuild;
    @Mock
    private CharacterCard mockCh;
    @Mock
    private PrintStream streamMock;

    @BeforeEach
    void setUp() {
        player = new Player("Alice");
    }

    @Test
    void getNickname() {
        assertEquals("Alice", player.getNickname());
    }

    @Test
    void getParameters() {
        assertEquals(Map.of(
                Parameter.ARTIST,           0,
                Parameter.SHAMAN,           0,
                Parameter.INVENTOR,         0,
                Parameter.BUILDER,          0,
                Parameter.GATHERER,         0,
                Parameter.HUNTER,           0,
                Parameter.FOOD,             0,
                Parameter.PRESTIGE_POINTS,  0
        ), player.getParameters());
    }

    @Test
    void getTribe() {
        assertEquals(Map.of(
                Parameter.ARTIST,   List.of(),
                Parameter.SHAMAN,   List.of(),
                Parameter.INVENTOR, List.of(),
                Parameter.BUILDER,  List.of(),
                Parameter.GATHERER, List.of(),
                Parameter.HUNTER,   List.of()
        ), player.getTribe());
    }

    @Test
    void getInventions() {
        assertEquals(new HashSet<>(10), player.getInventions());
    }

    @Test
    void getBuildings() {
        assertEquals(new ArrayList<>(), player.getBuildings());
    }

    @Test
    void getSpecialBuffs() {
        assertEquals(new HashSet<>(), player.getSpecialBuffs());
    }

    @Test
    void setUpMoves() {
        player.setUpMoves(1);
        assertEquals(1, player.getRemainingUpMoves());
    }

    @Test
    void setDownMoves() {
        player.setDownMoves(1);
        assertEquals(1, player.getRemainingDownMoves());
    }

    @Test
    void setMoves() {
        player.setMoves(1, 2);
        assertEquals(1, player.getRemainingUpMoves());
        assertEquals(2, player.getRemainingDownMoves());
    }

    @Test
    void decreaseRemainingUpMoves() {
        player.setUpMoves(2);
        player.decreaseRemainingUpMoves();
        assertEquals(1, player.getRemainingUpMoves());
    }

    @Test
    void decreaseRemainingDownMoves() {
        player.setDownMoves(2);
        player.decreaseRemainingDownMoves();
        assertEquals(1, player.getRemainingDownMoves());
    }

    @Test
    void hasEnoughUpMoves() {
        player.setUpMoves(1);
        assertTrue(player.hasEnoughUpMoves());

        player.decreaseRemainingUpMoves();
        assertFalse(player.hasEnoughUpMoves());
    }

    @Test
    void hasEnoughDownMoves() {
        player.setDownMoves(1);
        assertTrue(player.hasEnoughDownMoves());

        player.decreaseRemainingDownMoves();
        assertFalse(player.hasEnoughDownMoves());
    }

    @Test
    void hasNoMoves() {
        player.setMoves(1, 0);
        assertFalse(player.hasNoMoves());

        player.decreaseRemainingUpMoves();
        assertTrue(player.hasNoMoves());

        player.setDownMoves(1);
        assertFalse(player.hasNoMoves());
    }

    @Test
    void addCharacter() {
        when(mockCh.getRole()).thenReturn(Parameter.ARTIST);
        when(mockCh.getValue()).thenReturn(10);
        when(mockCh.getPrestigePoints()).thenReturn(3);

        player.addCharacter(mockCh);
        assertTrue(player.getTribe().get(Parameter.ARTIST).contains(mockCh));
        assertEquals(1, player.getTribe().get(Parameter.ARTIST).size());
        assertEquals(3, player.getParameters().get(Parameter.PRESTIGE_POINTS));

        player.addCharacter(mockCh);
        assertTrue(player.getTribe().get(Parameter.ARTIST).contains(mockCh));
        assertEquals(2, player.getTribe().get(Parameter.ARTIST).size());
        assertEquals(6, player.getParameters().get(Parameter.PRESTIGE_POINTS));
    }

    @Test
    void getCharacterType() {
        // set up the Mock Character
        when(mockCh.getRole()).thenReturn(Parameter.ARTIST);
        when(mockCh.getValue()).thenReturn(10);
        when(mockCh.getPrestigePoints()).thenReturn(3);

        // no Characters
        assertEquals(List.of(), player.getCharacterType(Parameter.ARTIST));

        // add a Character
        player.addCharacter(mockCh);
        assertEquals(List.of(mockCh), player.getCharacterType(Parameter.ARTIST));
    }

    @Test
    void addBuilding() {
        player.getParameters().put(Parameter.FOOD, 10);
        player.getParameters().put(Parameter.BUILDER, -3);

        when(mockBuild.getFoodCost()).thenReturn(5);

        player.addBuilding(mockBuild);

        int remainingFood = player.getParameters().get(Parameter.FOOD);
        assertEquals(8, remainingFood);
        assertTrue(player.getBuildings().contains(mockBuild));
    }

    @Test
    void addBuilding_FullDiscount() {
        player.getParameters().put(Parameter.FOOD, 10);
        player.getParameters().put(Parameter.BUILDER, -6);

        when(mockBuild.getFoodCost()).thenReturn(5);

        player.addBuilding(mockBuild);

        int remainingFood = player.getParameters().get(Parameter.FOOD);
        assertEquals(10, remainingFood);
        assertTrue(player.getBuildings().contains(mockBuild));
    }

    @Test
    void updateStats_OnlyUniqueInventions() {
        player.updateStats(Parameter.INVENTOR, 2);
        assertEquals(1, player.getParameters().get(Parameter.INVENTOR));

        player.updateStats(Parameter.INVENTOR, 3);
        assertEquals(2, player.getParameters().get(Parameter.INVENTOR));
        //OnOn second invention of the same type, parameter should not increase.
        player.updateStats(Parameter.INVENTOR, 3);
        assertEquals(2, player.getParameters().get(Parameter.INVENTOR));
    }

    @Test
    void updateStats_HunterFood() {
        player.getParameters().put(Parameter.FOOD, 5);
        player.getParameters().put(Parameter.HUNTER, 0);

        player.updateStats(Parameter.HUNTER, 1);

        assertEquals(1, player.getParameters().get(Parameter.HUNTER));
        assertEquals(6, player.getParameters().get(Parameter.FOOD));
    }

    @Test
    void updateStats_NegativeFood() {
        player.getParameters().put(Parameter.FOOD, 5);
        player.getParameters().put(Parameter.PRESTIGE_POINTS, 30);

        player.updateStats(Parameter.FOOD, -10);
        assertEquals(0, player.getParameters().get(Parameter.FOOD));
        assertEquals(20, player.getParameters().get(Parameter.PRESTIGE_POINTS));
    }

    @Test
    void updateStats_NegativePrestigePoints() {
        player.getParameters().put(Parameter.PRESTIGE_POINTS, 10);

        player.updateStats(Parameter.PRESTIGE_POINTS, -20);
        assertEquals(-10, player.getParameters().get(Parameter.PRESTIGE_POINTS));
    }

    @Test
    void removeBuff() {
        player.updateStats(SpecialBuff.ADDITIONAL_FOOD_TILE);
        player.updateStats(SpecialBuff.ADDITIONAL_UP_TILE);
        assertEquals(2, player.getSpecialBuffs().size());

        player.removeBuff(SpecialBuff.ADDITIONAL_UP_TILE);
        player.removeBuff(SpecialBuff.ADDITIONAL_FOOD_TILE);
        assertEquals(0, player.getSpecialBuffs().size());
    }

    @Test
    void lastRoundPoints() {
        // set up Mock Stats
        when(mockCh.getPrestigePoints()).thenReturn(10);
        when(mockBuild.getPpGain()).thenReturn(10);

        // Mock Tribe (40 BUILDER + 6 INVENTOR + 10 ARTIST)
        player.getTribe().get(Parameter.ARTIST).add(mockCh);
        player.getTribe().get(Parameter.ARTIST).add(mockCh);
        player.getTribe().get(Parameter.ARTIST).add(mockCh);
        player.getTribe().get(Parameter.BUILDER).add(mockCh);
        player.getTribe().get(Parameter.BUILDER).add(mockCh);
        player.getTribe().get(Parameter.BUILDER).add(mockCh);
        player.getTribe().get(Parameter.BUILDER).add(mockCh);
        player.getTribe().get(Parameter.INVENTOR).add(mockCh);
        player.getTribe().get(Parameter.INVENTOR).add(mockCh);
        player.getTribe().get(Parameter.INVENTOR).add(mockCh);

        // mock Buildings (20 BUILDINGS)
        player.getBuildings().add(mockBuild);
        player.getBuildings().add(mockBuild);

        // mock parameters
        player.getParameters().put(Parameter.INVENTOR, 2);
        player.updateStats(Parameter.PRESTIGE_POINTS, 30);

        // Act
        player.lastRoundPoints();

        // Assert
        assertEquals(106, player.getParameters().get(Parameter.PRESTIGE_POINTS));
    }

    @Test
    void displayTribe() {
        // set the Mock Character
        when(mockCh.getRole()).thenReturn(Parameter.INVENTOR);
        when(mockCh.getValue()).thenReturn(10);
        when(mockCh.getPrestigePoints()).thenReturn(11);

        // first call
        player.addCharacter(mockCh);
        System.setOut(streamMock);
        player.displayTribe();
        verify(streamMock, times(3)).println(Optional.ofNullable(any()));

        // second call
        player.addCharacter(mockCh);
        player.addCharacter(mockCh);
        player.addCharacter(mockCh);
        player.addCharacter(mockCh);
        player.addCharacter(mockCh);
        player.addCharacter(mockCh);
        player.addCharacter(mockCh);
        player.displayTribe();
        verify(streamMock, times(6)).println(Optional.ofNullable(any()));
    }

    @Test
    void displayTribe_Empty() {
        System.setOut(streamMock);
        player.displayTribe();
        player.displayTribe();
        verify(streamMock, never()).println(Optional.ofNullable(any()));
    }

    @Test
    void displayStats() {
        // set up Mock Stats
        when(mockCh.getRole()).thenReturn(Parameter.INVENTOR);
        when(mockCh.getValue()).thenReturn(10);
        when(mockCh.getPrestigePoints()).thenReturn(11);
        player.updateStats(Parameter.FOOD,3);
        player.updateStats(Parameter.PRESTIGE_POINTS,4);

        // Act
        player.addCharacter(mockCh);
        System.setOut(streamMock);
        player.displayStats();

        // Assert
        assertEquals(1, player.getInventions().size());
        verify(streamMock).printf(anyString(),any(),any());
    }

    @Test
    void displayStats_NullInventions() {
        player.setNullInventions();
        System.setOut(streamMock);
        player.displayStats();
        assertNull(player.getInventions());
        verify(streamMock).printf(anyString(),any(),any());
    }

    @Test
    void equals_Null() {
        boolean equals = player.equals(null);
        assertFalse(equals);
    }

    @Test
    void equals_DifferentClasses() {
        boolean equals = player.equals(mockCh);
        assertFalse(equals);
    }

    @Test
    void equals_True() {
        // set two Players
        Player player1 = new Player("p1");
        Player player2 = new Player("p1");

        // Act
        boolean equals = player1.equals(player2);

        // Assert
        assertTrue(equals);
    }

    @Test
    void equals_False() {
        // set two Players
        Player player1 = new Player("p1");
        Player player2 = new Player("p2");

        // Act
        boolean equals = player1.equals(player2);

        // Assert
        assertFalse(equals);
    }
}