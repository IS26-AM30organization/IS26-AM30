package mesos.am30.GameModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

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
}