package mesos.am30.gameModel.board;

import mesos.am30.client.IF_GameView;
import mesos.am30.common.Move;
import mesos.am30.common.ViewParameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.SpecialBuff;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Card;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameManagerTest {

    @Mock private Board mockBoard;
    @Mock private IF_GameView mockView1;
    @Mock private IF_GameView mockView2;
    @Mock private Card mockCard;
    @Mock private BuildingCard mockBuildingCard;
    @Mock private Player playerA;
    @Mock private Player playerB;

    private List<Player> playersOrder;
    private List<Player> players;
    private GameManager gameManager;

    @BeforeEach
    void setUp() {
        lenient().when(playerA.getNickname()).thenReturn("A");
        lenient().when(playerB.getNickname()).thenReturn("B");
        playersOrder = new ArrayList<>(List.of(playerA, playerB));
        players = new ArrayList<>(List.of(playerA, playerB));

        when(mockBoard.getPlayersOrder()).thenReturn(playersOrder);
        gameManager = new GameManager(mockBoard, players, List.of(mockView1, mockView2));
    }

    // anyCharacterLeft

    @Test
    void anyCharacterLeft_WithPickableCard_ReturnsTrue() {
        when(mockCard.isPickable()).thenReturn(true);
        assertTrue(gameManager.anyCharacterLeft(List.of(mockCard)));
    }

    @Test
    void anyCharacterLeft_AllNonPickable_ReturnsFalse() {
        when(mockCard.isPickable()).thenReturn(false);
        assertFalse(gameManager.anyCharacterLeft(List.of(mockCard)));
    }

    @Test
    void anyCharacterLeft_EmptyList_ReturnsFalse() {
        assertFalse(gameManager.anyCharacterLeft(List.of()));
    }

    // anyChoosableCard

    @Test
    void anyChoosableCard_UpMovesAndPickableUpperRow_ReturnsTrueWithoutZeroingMoves() {
        when(playerA.hasEnoughUpMoves()).thenReturn(true);
        when(mockCard.isPickable()).thenReturn(true);
        when(mockBoard.getUpperRow()).thenReturn(List.of(mockCard));

        assertTrue(gameManager.anyChoosableCard(playerA));
        verify(playerA, never()).setMoves(0, 0);
    }

    @Test
    void anyChoosableCard_DownMovesAndPickableLowerRow_ReturnsTrue() {
        when(playerA.hasEnoughUpMoves()).thenReturn(false);
        when(playerA.hasEnoughDownMoves()).thenReturn(true);
        when(mockCard.isPickable()).thenReturn(true);
        when(mockBoard.getLowerRow()).thenReturn(List.of(mockCard));

        assertTrue(gameManager.anyChoosableCard(playerA));
        verify(playerA, never()).setMoves(0, 0);
    }

    @Test
    void anyChoosableCard_NoPickableCards_ReturnsFalseAndZeroesMoves() {
        when(playerA.hasEnoughUpMoves()).thenReturn(true);
        when(playerA.hasEnoughDownMoves()).thenReturn(true);
        when(mockCard.isPickable()).thenReturn(false);
        when(mockBoard.getUpperRow()).thenReturn(List.of(mockCard));
        when(mockBoard.getLowerRow()).thenReturn(List.of(mockCard));
        when(mockBoard.getUpperBuildings()).thenReturn(List.of());
        when(mockBoard.getLowerBuildings()).thenReturn(List.of());

        assertFalse(gameManager.anyChoosableCard(playerA));
        verify(playerA).setMoves(0, 0);
    }

    @Test
    void anyChoosableCard_UpMovesAndOnlyBuildingsAvailable_ReturnsTrue() {
        when(playerA.hasEnoughUpMoves()).thenReturn(true);
        when(mockCard.isPickable()).thenReturn(false);
        when(mockBoard.getUpperRow()).thenReturn(List.of(mockCard));
        when(mockBoard.getUpperBuildings()).thenReturn(List.of(mockBuildingCard));
        when(mockBuildingCard.canBeBought(playerA)).thenReturn(true);

        assertTrue(gameManager.anyChoosableCard(playerA));
        verify(playerA, never()).setMoves(0, 0);
    }

    @Test
    void anyChoosableCard_DownMovesAndOnlyBuildingsAvailable_ReturnsTrue() {
        when(playerA.hasEnoughUpMoves()).thenReturn(false);
        when(playerA.hasEnoughDownMoves()).thenReturn(true);
        when(mockCard.isPickable()).thenReturn(false);
        when(mockBoard.getLowerRow()).thenReturn(List.of(mockCard));
        when(mockBoard.getLowerBuildings()).thenReturn(List.of(mockBuildingCard));
        when(mockBuildingCard.canBeBought(playerA)).thenReturn(true);

        assertTrue(gameManager.anyChoosableCard(playerA));
        verify(playerA, never()).setMoves(0, 0);
    }

    // whereDoIPickCards

    @Test
    void whereDoIPickCards_NoMoves_ReturnsPickTile() {
        when(playerA.hasNoMoves()).thenReturn(true);
        assertEquals(Move.PICK_TILE, gameManager.whereDoIPickCards());
    }

    @Test
    void whereDoIPickCards_BothMoves_ReturnsPickAnyCard() {
        when(playerA.hasNoMoves()).thenReturn(false);
        when(playerA.hasEnoughUpMoves()).thenReturn(true);
        when(playerA.hasEnoughDownMoves()).thenReturn(true);
        assertEquals(Move.PICK_ANY_CARD, gameManager.whereDoIPickCards());
    }

    @Test
    void whereDoIPickCards_OnlyUpMoves_ReturnsPickFromUp() {
        when(playerA.hasNoMoves()).thenReturn(false);
        when(playerA.hasEnoughUpMoves()).thenReturn(true);
        when(playerA.hasEnoughDownMoves()).thenReturn(false);
        assertEquals(Move.PICK_FROM_UP, gameManager.whereDoIPickCards());
    }

    @Test
    void whereDoIPickCards_OnlyDownMoves_ReturnsPickFromDown() {
        when(playerA.hasNoMoves()).thenReturn(false);
        when(playerA.hasEnoughUpMoves()).thenReturn(false);
        assertEquals(Move.PICK_FROM_DOWN, gameManager.whereDoIPickCards());
    }

    // notifyEveryone

    @Test
    void notifyEveryone_NotifiesAllViewsAndSetsCurrentMove() throws IOException {
        gameManager.notifyEveryone(playerA, Move.PICK_TILE);
        verify(mockView1).notifyTurn("A", Move.PICK_TILE);
        verify(mockView2).notifyTurn("A", Move.PICK_TILE);
        assertEquals(Move.PICK_TILE, gameManager.getCurrentMove());
    }

    // updateEveryone

    @Test
    void updateEveryone_CallsUpdateOnAllViews() throws IOException {
        gameManager.updateEveryone(ViewParameter.PLAYERS, players);
        verify(mockView1).update(eq(ViewParameter.PLAYERS), anyList());
        verify(mockView2).update(eq(ViewParameter.PLAYERS), anyList());
    }

    // iPickedTile

    @Test
    void iPickedTile_OtherPlayersRemain_NotifiesNextWithPickTile() throws IOException {
        when(mockBoard.getTiles()).thenReturn(List.of());
        gameManager.iPickedTile(playerA);
        assertFalse(playersOrder.contains(playerA));
        assertTrue(playersOrder.contains(playerB));
        verify(mockView1).notifyTurn("B", Move.PICK_TILE);
        verify(mockView2).notifyTurn("B", Move.PICK_TILE);
    }

    @Test
    void iPickedTile_LastPlayer_CallsScanTilesAndNotifiesForCards() throws IOException {
        playersOrder.clear();
        playersOrder.add(playerA);
        when(mockBoard.getTiles()).thenReturn(List.of());
        doAnswer(_ -> { playersOrder.add(playerA); return null; }).when(mockBoard).scanTiles();

        gameManager.iPickedTile(playerA);

        verify(mockBoard).scanTiles();
        verify(mockView1).notifyTurn(eq("A"), any(Move.class));
    }

    // iPickedCard

    @Test
    void iPickedCard_AllPlayersNoMoves_ReturnsTrue() throws IOException {
        when(playerA.hasNoMoves()).thenReturn(true);
        when(playerA.getSpecialBuffs()).thenReturn(Set.of());
        when(playerB.hasNoMoves()).thenReturn(true);
        assertTrue(gameManager.iPickedCard(playerA));
    }

    @Test
    void iPickedCard_CurrentPlayerUsedLastMove_NotifiesNext() throws IOException {
        when(playerA.hasNoMoves()).thenReturn(true);
        when(playerA.getSpecialBuffs()).thenReturn(Set.of());
        when(playerB.hasNoMoves()).thenReturn(false);
        when(playerB.hasEnoughUpMoves()).thenReturn(true);
        when(playerB.hasEnoughDownMoves()).thenReturn(false);
        when(mockCard.isPickable()).thenReturn(true);
        when(mockBoard.getUpperRow()).thenReturn(List.of(mockCard));

        assertFalse(gameManager.iPickedCard(playerA));
        verify(mockView1).notifyTurn("B", Move.PICK_FROM_UP);
        verify(mockView2).notifyTurn("B", Move.PICK_FROM_UP);
    }

    @Test
    void iPickedCard_PlayerStillHasMoves_SamePlayerNotifiedAgain() throws IOException {
        when(playerA.hasNoMoves()).thenReturn(false);
        when(playerA.hasEnoughUpMoves()).thenReturn(true);
        when(playerA.hasEnoughDownMoves()).thenReturn(false);
        when(mockCard.isPickable()).thenReturn(true);
        when(mockBoard.getUpperRow()).thenReturn(List.of(mockCard));

        assertFalse(gameManager.iPickedCard(playerA));
        verify(mockView1).notifyTurn("A", Move.PICK_FROM_UP);
        verify(mockView2).notifyTurn("A", Move.PICK_FROM_UP);
    }

    @Test
    void iPickedCard_SkipsPlayerWithNoChoosableCards_NotifiesNext() throws IOException {
        when(playerA.hasNoMoves()).thenReturn(false);
        when(playerA.hasEnoughUpMoves()).thenReturn(true);
        when(playerA.hasEnoughDownMoves()).thenReturn(false);
        when(playerB.hasNoMoves()).thenReturn(false);
        when(playerB.hasEnoughUpMoves()).thenReturn(false);
        when(playerB.hasEnoughDownMoves()).thenReturn(true);
        when(mockCard.isPickable()).thenReturn(true);
        when(mockBoard.getUpperRow()).thenReturn(List.of());
        when(mockBoard.getLowerRow()).thenReturn(List.of(mockCard));
        when(mockBoard.getUpperBuildings()).thenReturn(List.of());

        assertFalse(gameManager.iPickedCard(playerA));
        verify(mockView1).notifyTurn("B", Move.PICK_FROM_DOWN);
        verify(mockView2).notifyTurn("B", Move.PICK_FROM_DOWN);
    }

    @Test
    void iPickedCard_PlayerWithOnlyBuildingsAvailable_NotSkipped() throws IOException {
        when(playerA.hasNoMoves()).thenReturn(false);
        when(playerA.hasEnoughUpMoves()).thenReturn(true);
        when(playerA.hasEnoughDownMoves()).thenReturn(false);
        when(mockCard.isPickable()).thenReturn(false);
        when(mockBoard.getUpperRow()).thenReturn(List.of(mockCard));
        when(mockBoard.getUpperBuildings()).thenReturn(List.of(mockBuildingCard));
        when(mockBuildingCard.canBeBought(playerA)).thenReturn(true);

        assertFalse(gameManager.iPickedCard(playerA));
        verify(mockView1).notifyTurn("A", Move.PICK_FROM_UP);
        verify(mockView2).notifyTurn("A", Move.PICK_FROM_UP);
    }

    @Test
    void iPickedCard_AdditionalUpTileBuff_BuffConsumedAndZeroMovePlayerReordered() throws IOException {
        when(playerA.hasNoMoves()).thenReturn(true);
        when(playerA.getSpecialBuffs()).thenReturn(Set.of(SpecialBuff.ADDITIONAL_UP_TILE));
        when(playerB.hasNoMoves()).thenReturn(true);

        gameManager.iPickedCard(playerA);
        assertTrue(playersOrder.indexOf(playerA) < playersOrder.indexOf(playerB));
    }

    @Test
    void iPickedCard_AdditionalUpTileBuff_BuffConsumedAndAllPlayersReordered() throws IOException {
        when(playerA.hasNoMoves()).thenReturn(true);
        when(playerA.getSpecialBuffs()).thenReturn(Set.of(SpecialBuff.ADDITIONAL_UP_TILE));
        when(playerB.hasNoMoves()).thenReturn(false);
        when(playerB.hasEnoughUpMoves()).thenReturn(true);
        when(mockCard.isPickable()).thenReturn(true);
        when(mockBoard.getUpperRow()).thenReturn(List.of(mockCard));

        // Assert
        gameManager.iPickedCard(playerA);
        assertTrue(playersOrder.indexOf(playerA) > playersOrder.indexOf(playerB));
    }

    // iChangedTurn

    @Test
    void iChangedTurn_UpdatesStateAndNotifiesFirstPlayerWithPickTile() throws IOException {
        when(mockBoard.getTiles()).thenReturn(List.of());
        when(mockBoard.getUpperRow()).thenReturn(List.of());
        when(mockBoard.getLowerRow()).thenReturn(List.of());
        when(mockBoard.getUpperBuildings()).thenReturn(List.of());
        when(mockBoard.getLowerBuildings()).thenReturn(List.of());

        gameManager.iChangedTurn();

        verify(mockView1).notifyTurn("A", Move.PICK_TILE);
        verify(mockView2).notifyTurn("A", Move.PICK_TILE);
    }

    // sendClientEnd

    @Test
    void sendClientEnd_CallsEndOnAllViews() throws IOException {
        when(mockBoard.getTiles()).thenReturn(List.of());
        when(mockBoard.getUpperRow()).thenReturn(List.of());
        when(mockBoard.getLowerRow()).thenReturn(List.of());
        when(mockBoard.getUpperBuildings()).thenReturn(List.of());
        when(mockBoard.getLowerBuildings()).thenReturn(List.of());

        gameManager.sendClientEnd();

        verify(mockView1).askShowRankings();
        verify(mockView2).askShowRankings();
    }
}
