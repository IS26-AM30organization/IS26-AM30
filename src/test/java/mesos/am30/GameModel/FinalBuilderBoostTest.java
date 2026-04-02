package mesos.am30.GameModel;

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
class FinalBuilderBoostTest {
    private FinalBuilderBoost testingCard;

    @Mock
    Player mockPlayer;
    @Mock
    CharacterCard mockCard1;
    @Mock
    CharacterCard mockCard2;

    @BeforeEach
    void setUp() {
        testingCard= new FinalBuilderBoost(2);
    }

    @Test
    void normalCase() {
        List<CharacterCard> builders=new ArrayList<>();
        builders.add(mockCard1);
        builders.add(mockCard2);
        when(mockCard1.getPrestigePoints()).thenReturn(3);
        when(mockCard2.getPrestigePoints()).thenReturn(2);
        when(mockPlayer.getTribe()).thenReturn(Map.of(
                Parameter.BUILDER, builders
        ));
        testingCard.handleEvent(mockPlayer);
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS,5);
    }
    @Test
    void nullCase() {

        when(mockPlayer.getTribe()).thenReturn(Map.of(
                Parameter.BUILDER, new ArrayList<>()
        ));
        testingCard.handleEvent(mockPlayer);

        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, 0);
    }
}