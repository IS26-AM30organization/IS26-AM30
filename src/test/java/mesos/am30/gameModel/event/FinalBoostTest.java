package mesos.am30.gameModel.event;

import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.eventIF.FinalBoost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@ExtendWith(MockitoExtension.class)
class FinalBoostTest {
    private FinalBoost testingCard;
    @Mock
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        testingCard=new FinalBoost(Parameter.GATHERER,2);
    }

    @Test
    void normalCase() {
        List<CharacterCard> gatherers=new ArrayList<>();
        for (int i=0; i<4; i++)
            gatherers.add(mock(CharacterCard.class));
        when(mockPlayer.getTribe()).thenReturn(Map.of(
                Parameter.GATHERER, gatherers
        ));
        testingCard.handleEvent(mockPlayer);

        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, 8);
    }
    @Test
    void nullCase() {

        when(mockPlayer.getTribe()).thenReturn(Map.of(
                Parameter.GATHERER, new ArrayList<>()
        ));
        testingCard.handleEvent(mockPlayer);

        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, 0);
    }
}