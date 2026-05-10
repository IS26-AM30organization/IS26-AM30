package mesos.am30.gameModel;

import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.CharacterCard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerTest {
    private Player p = new Player("Alice");
    @Mock
    private BuildingCard mockBuild;
    @Mock
    private CharacterCard mockCh;
    @Mock
    private PrintStream streamMock;
    CharacterCard card1 = new CharacterCard(1, Parameter.INVENTOR,10,11,200);

    @Test
    void addBuilding() {
        p.getParameters().put(Parameter.FOOD, 10);
        p.getParameters().put(Parameter.BUILDER, 3);

        when(mockBuild.getFoodCost()).thenReturn(5);

        p.addBuilding(mockBuild);

        int remainingFood = p.getParameters().get(Parameter.FOOD);
        assertEquals(8, remainingFood);
        assertTrue(p.getBuildings().contains(mockBuild));
    }

    @Test
    void addCharacter() {
        when(mockCh.getRole()).thenReturn(Parameter.INVENTOR);

        p.addCharacter(mockCh);
        assertTrue(p.getTribe().get(Parameter.INVENTOR).contains(mockCh));
        assertEquals(p.getTribe().get(Parameter.INVENTOR).size(),1);

        p.addCharacter(mockCh);
        assertTrue(p.getTribe().get(Parameter.INVENTOR).contains(mockCh));
        assertEquals(p.getTribe().get(Parameter.INVENTOR).size(),2);
    }

    @Test
    void updateStats_OnlyUniqueInventions() {
        p.updateStats(Parameter.INVENTOR, 2);
        assertEquals(1, p.getParameters().get(Parameter.INVENTOR));

        p.updateStats(Parameter.INVENTOR, 3);
        assertEquals(2, p.getParameters().get(Parameter.INVENTOR));
        //OnOn second invention of the same type, parameter should not increase.
        p.updateStats(Parameter.INVENTOR, 3);
        assertEquals(2, p.getParameters().get(Parameter.INVENTOR));
    }

    @Test
    void updateStats_HunterFood() {
        p.getParameters().put(Parameter.FOOD, 5);
        p.getParameters().put(Parameter.HUNTER, 0);

        p.updateStats(Parameter.HUNTER, 1);

        assertEquals(1, p.getParameters().get(Parameter.HUNTER));
        assertEquals(6, p.getParameters().get(Parameter.FOOD));
    }

    @Test
    void lastRoundPoints() {
        //TBD
    }

    @Test
    void testUpdateStats() {
        //TBD
    }

    @Test
    void removeBuff() {
        p.updateStats(SpecialBuff.ADDITIONAL_FOOD_TILE);
        p.updateStats(SpecialBuff.ADDITIONAL_UP_TILE);
        assertEquals(p.getSpecialBuffs().size(), 2);

        p.removeBuff(SpecialBuff.ADDITIONAL_UP_TILE);
        p.removeBuff(SpecialBuff.ADDITIONAL_FOOD_TILE);
        assertEquals(p.getSpecialBuffs().size(), 0);
    }

    @Test
    void movesChecks() {
        p.setMoves(1,1);
        assertTrue(p.hasEnoughUpMoves());
        assertTrue(p.hasEnoughDownMoves());

        p.decreaseRemainingUpMoves();
        p.decreaseRemainingDownMoves();
        assertFalse(p.hasEnoughUpMoves());
        assertFalse(p.hasEnoughDownMoves());
        assertTrue(p.hasNoMoves());
    }

    @Test
    void displayTribe() {
        p.addCharacter(card1);
        System.setOut(streamMock);
        p.displayTribe();

        verify(streamMock, times(3)).println(Optional.ofNullable(any()));

        p.addCharacter(card1);
        p.addCharacter(card1);
        p.addCharacter(card1);
        p.addCharacter(card1);
        p.addCharacter(card1);
        p.addCharacter(card1);
        p.addCharacter(card1);
        p.displayTribe();

        verify(streamMock, times(9)).println(Optional.ofNullable(any()));
    }

    @Test
    void displayStats() {
        p.updateStats(Parameter.FOOD,3);
        p.updateStats(Parameter.PRESTIGE_POINTS,4);
        p.addCharacter(card1);


        System.setOut(streamMock);
        p.displayStats();

        assertEquals(p.getInventions().size(),1);
        verify(streamMock).printf(anyString(),any(),any());
    }
}