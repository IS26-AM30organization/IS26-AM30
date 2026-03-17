package mesos.am30.GameModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class HuntTest {
    private Hunt huntCard;

    @Mock
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        huntCard = new Hunt(3);
    }

    @Test
    void handleEvent() {
        // set the Mock Player
        List<CharacterCard> hunters = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            hunters.add(mock(CharacterCard.class));
        }
        when(mockPlayer.getTribe()).thenReturn(Map.of(Parameter.HUNTER, hunters));

        // Act
        huntCard.handleEvent(mockPlayer);

        // Assert
        verify(mockPlayer).updateStats(Parameter.FOOD, 1);
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, huntCard.getPrestigePoints() * hunters.size());
    }
}