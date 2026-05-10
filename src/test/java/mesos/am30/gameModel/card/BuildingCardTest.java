package mesos.am30.gameModel.card;

import mesos.am30.gameModel.EventType;
import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.board.Board;
import mesos.am30.gameModel.eventIF.FullSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuildingCardTest {
    @Mock private Board board;
    @Mock private Player player;

    private IF_Event event = new FullSet(3)  ;
    BuildingCard card1 = new BuildingCard(1, event, EventType.ROUND, 3, 1, 100);
    BuildingCard card2 = new BuildingCard(1, event, EventType.ROUND, 10, 1, 100);

    @Test
    void canBeBought_True() {
        Map<Parameter, Integer> parameters = new HashMap<>();
        parameters.put(Parameter.FOOD, 6);
        parameters.put(Parameter.BUILDER, 5);

        when(player.getParameters()).thenReturn(parameters);

        assertTrue(card2.canBeBought(player));
    }

    @Test
    void canBeBought_False() {
        Map<Parameter, Integer> params = new HashMap<>();
        params.put(Parameter.FOOD, 4);
        params.put(Parameter.BUILDER, 3);

        when(player.getParameters()).thenReturn(params);

        assertFalse(card2.canBeBought(player));
    }

    @Test
    void discard() {
        List<BuildingCard> upperRow = new ArrayList<>();
        List<BuildingCard> lowerRow = new ArrayList<>();
        upperRow.add(card1);
        lowerRow.add(card2);
        when(board.getUpperBuildings()).thenReturn(upperRow);
        when(board.getLowerBuildings()).thenReturn(lowerRow);

        card1.discard(board);
        card2.discard(board);

        assertTrue(upperRow.isEmpty());
        assertTrue(lowerRow.isEmpty());
    }

    @Test
    void createRow() {
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        card1.createRow(ln1,ln2,ln3);

        assertFalse(ln1.toString().isEmpty());
        assertFalse(ln2.toString().isEmpty());
    }
}