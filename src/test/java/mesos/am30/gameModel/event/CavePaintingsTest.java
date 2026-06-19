package mesos.am30.gameModel.event;

import mesos.am30.gameModel.EventType;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.CharacterCard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class CavePaintingsTest {
    private CavePaintings cavePaintingsCard;

    @Mock
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        cavePaintingsCard = new CavePaintings(3,-2,4);
    }

    @Mock
    private BuildingCard mockBuilding;

    @Mock
    private StatsBoost mockStatsBoost;

    @Test
    void handleEvent_Positive() {
        // set the Mock Player
        List<CharacterCard> artists = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            artists.add(mock(CharacterCard.class));
        }
        when(mockPlayer.getTribe()).thenReturn(Map.of(Parameter.ARTIST, artists));

        // Act
        cavePaintingsCard.handleEvent(mockPlayer);

        // Assert
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, cavePaintingsCard.getGainedPrestigePoints() * 3);
    }

    @Test
    void handleEvent_Negative() {
        // set the Mock Player
        List<CharacterCard> artists = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            artists.add(mock(CharacterCard.class));
        }
        when(mockPlayer.getTribe()).thenReturn(Map.of(Parameter.ARTIST, artists));

        // Act
        cavePaintingsCard.handleEvent(mockPlayer);

        // Assert
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, cavePaintingsCard.getLostPrestigePoints());
    }

    @Test
    void handleEvent_BuildingsCorrect() {
        // set the Mock Building
        when(mockBuilding.getEventType()).thenReturn(EventType.CAVE_PAINTINGS);
        when(mockBuilding.getEvent()).thenReturn(mockStatsBoost);

        // set the Mock Player
        List<CharacterCard> artists = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            artists.add(mock(CharacterCard.class));
        }
        when(mockPlayer.getTribe()).thenReturn(Map.of(Parameter.ARTIST, artists));
        when(mockPlayer.getBuildings()).thenReturn(List.of(mockBuilding));

        // Act
        cavePaintingsCard.handleEvent(mockPlayer);

        // Assert
        verify(mockStatsBoost).handleEvent(mockPlayer);
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, cavePaintingsCard.getLostPrestigePoints());
    }

    @Test
    void handleEvent_BuildingsWrong() {
        // set the Mock Building
        when(mockBuilding.getEventType()).thenReturn(EventType.HUNT);

        // set the Mock Player
        List<CharacterCard> artists = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            artists.add(mock(CharacterCard.class));
        }
        when(mockPlayer.getTribe()).thenReturn(Map.of(Parameter.ARTIST, artists));
        when(mockPlayer.getBuildings()).thenReturn(List.of(mockBuilding));

        // Act
        cavePaintingsCard.handleEvent(mockPlayer);

        // Assert
        verifyNoInteractions(mockStatsBoost);
        verify(mockPlayer).updateStats(Parameter.PRESTIGE_POINTS, cavePaintingsCard.getLostPrestigePoints());
    }

    @Test
    void getAttributes() {
        // set up the StingBuilders
        StringBuilder ln1 = new StringBuilder();
        StringBuilder ln2 = new StringBuilder();
        StringBuilder ln3 = new StringBuilder();

        // Act
        cavePaintingsCard.getAttributes(ln1, ln2, ln3);

        // assert
        assertFalse(ln1.toString().isEmpty());
        assertFalse(ln2.toString().isEmpty());
        assertFalse(ln3.toString().isEmpty());
    }

    @Test
    void getInfo() {
        assertEquals(
                "This is a Painting Event Card: when resolved, if the player has at least " +
                        cavePaintingsCard.getArtistMinimum() + " he gains " +
                        cavePaintingsCard.getGainedPrestigePoints() + " pP, otherwise player loses " +
                        cavePaintingsCard.getLostPrestigePoints() + " pP.",
                cavePaintingsCard.getInfo(new StringBuilder())
        );
    }

    @Test
    void getArt() {
        assertEquals(
                cavePaintingsCard.getGainedPrestigePoints() +
                        "c" + (cavePaintingsCard.getLostPrestigePoints() * -1) +
                        "c" + cavePaintingsCard.getArtistMinimum(),
                cavePaintingsCard.getArt()
        );
    }
}