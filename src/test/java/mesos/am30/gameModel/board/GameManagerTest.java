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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameManagerTest {

    @Mock private Board mockBoard;
    @Mock private IF_GameView mockView1;
    @Mock private IF_GameView mockView2;
    @Mock private Card mockPickableCard;
    @Mock private Card mockNonPickableCard;
    @Mock private BuildingCard mockBuildingCard;

    private Player playerA;
    private Player playerB;
    private List<Player> playersOrder;
    private List<Player> players;
    private GameManager gameManager;

    @BeforeEach
    void setUp() {
        playerA = new Player("A");
        playerB = new Player("B");
        playersOrder = new ArrayList<>(List.of(playerA, playerB));
        players = new ArrayList<>(List.of(playerA, playerB));

        when(mockBoard.getPlayersOrder()).thenReturn(playersOrder);
        gameManager = new GameManager(mockBoard, players, List.of(mockView1, mockView2));
    }

    // anyCharacterLeft

    @Test
    void anyCharacterLeft_WithPickableCard_ReturnsTrue() {
        when(mockPickableCard.isPickable()).thenReturn(true);
        assertTrue(gameManager.anyCharacterLeft(List.of(mockPickableCard)));
    }

    @Test
    void anyCharacterLeft_AllNonPickable_ReturnsFalse() {
        when(mockNonPickableCard.isPickable()).thenReturn(false);
        assertFalse(gameManager.anyCharacterLeft(List.of(mockNonPickableCard)));
    }

    @Test
    void anyCharacterLeft_EmptyList_ReturnsFalse() {
        assertFalse(gameManager.anyCharacterLeft(List.of()));
    }

    // anyChoosableCard

    @Test
    void anyChoosableCard_UpMovesAndPickableUpperRow_ReturnsTrueWithoutZeroingMoves() {
        playerA.setMoves(1, 0);
        when(mockPickableCard.isPickable()).thenReturn(true);
        when(mockBoard.getUpperRow()).thenReturn(List.of(mockPickableCard));

        assertTrue(gameManager.anyChoosableCard(playerA));
        assertTrue(playerA.hasEnoughUpMoves());
    }

    @Test
    void anyChoosableCard_DownMovesAndPickableLowerRow_ReturnsTrue() {
        playerA.setMoves(0, 1);
        when(mockPickableCard.isPickable()).thenReturn(true);
        when(mockBoard.getLowerRow()).thenReturn(List.of(mockPickableCard));

        assertTrue(gameManager.anyChoosableCard(playerA));
        assertTrue(playerA.hasEnoughDownMoves());
    }

    @Test
    void anyChoosableCard_NoPickableCards_ReturnsFalseAndZeroesMoves() {
        playerA.setMoves(1, 1);
        when(mockNonPickableCard.isPickable()).thenReturn(false);
        when(mockBoard.getUpperRow()).thenReturn(List.of(mockNonPickableCard));
        when(mockBoard.getLowerRow()).thenReturn(List.of(mockNonPickableCard));
        when(mockBoard.getUpperBuildings()).thenReturn(List.of());
        when(mockBoard.getLowerBuildings()).thenReturn(List.of());

        assertFalse(gameManager.anyChoosableCard(playerA));
        assertTrue(playerA.hasNoMoves());
    }

    @Test
    void anyChoosableCard_UpMovesAndOnlyBuildingsAvailable_ReturnsTrue() {
        playerA.setMoves(1, 0);
        when(mockNonPickableCard.isPickable()).thenReturn(false);
        when(mockBoard.getUpperRow()).thenReturn(List.of(mockNonPickableCard));
        when(mockBoard.getUpperBuildings()).thenReturn(List.of(mockBuildingCard));
        when(mockBuildingCard.canBeBought(playerA)).thenReturn(true);

        assertTrue(gameManager.anyChoosableCard(playerA));
    }

    // whereDoIPickCards

    @Test
    void whereDoIPickCards_NoMoves_ReturnsPickTile() {
        playerA.setMoves(0, 0);
        assertEquals(Move.PICK_TILE, gameManager.whereDoIPickCards(playerA));
    }

    @Test
    void whereDoIPickCards_BothMoves_ReturnsPickAnyCard() {
        playerA.setMoves(1, 1);
        assertEquals(Move.PICK_ANY_CARD, gameManager.whereDoIPickCards(playerA));
    }

    @Test
    void whereDoIPickCards_OnlyUpMoves_ReturnsPickFromUp() {
        playerA.setMoves(1, 0);
        assertEquals(Move.PICK_FROM_UP, gameManager.whereDoIPickCards(playerA));
    }

    @Test
    void whereDoIPickCards_OnlyDownMoves_ReturnsPickFromDown() {
        playerA.setMoves(0, 1);
        assertEquals(Move.PICK_FROM_DOWN, gameManager.whereDoIPickCards(playerA));
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
        verify(mockView1).notifyTurn("B", Move.PICK_TILE);
        verify(mockView2).notifyTurn("B", Move.PICK_TILE);
    }

    @Test
    void iPickedTile_LastPlayer_CallsScanTilesAndNotifiesForCards() throws IOException {
        playersOrder.clear();
        playersOrder.add(playerA);
        playerA.setMoves(1, 0);
        when(mockBoard.getTiles()).thenReturn(List.of());
        doAnswer(_ -> { playersOrder.add(playerA); return null; }).when(mockBoard).scanTiles();

        gameManager.iPickedTile(playerA);

        verify(mockBoard).scanTiles();
        verify(mockView1).notifyTurn(eq("A"), any(Move.class));
    }

    // iPickedCard

    @Test
    void iPickedCard_AllPlayersNoMoves_ReturnsTrue() throws IOException {
        playerA.setMoves(0, 0);
        playerB.setMoves(0, 0);

        assertTrue(gameManager.iPickedCard(playerA));
    }

    @Test
    void iPickedCard_CurrentPlayerUsedLastMove_NotifiesNext() throws IOException {
        playerA.setMoves(0, 0);
        playerB.setMoves(1, 0);
        when(mockPickableCard.isPickable()).thenReturn(true);
        when(mockBoard.getUpperRow()).thenReturn(List.of(mockPickableCard));

        boolean result = gameManager.iPickedCard(playerA);

        assertFalse(result);
        verify(mockView1).notifyTurn("B", Move.PICK_FROM_UP);
        verify(mockView2).notifyTurn("B", Move.PICK_FROM_UP);
    }

    @Test
    void iPickedCard_PlayerStillHasMoves_SamePlayerNotifiedAgain() throws IOException {
        playerA.setMoves(1, 0);
        playerB.setMoves(0, 0);
        when(mockPickableCard.isPickable()).thenReturn(true);
        when(mockBoard.getUpperRow()).thenReturn(List.of(mockPickableCard));

        boolean result = gameManager.iPickedCard(playerA);

        assertFalse(result);
        verify(mockView1).notifyTurn("A", Move.PICK_FROM_UP);
        verify(mockView2).notifyTurn("A", Move.PICK_FROM_UP);
    }

    @Test
    void iPickedCard_SkipsPlayerWithNoChoosableCards_NotifiesNext() throws IOException {
        playerA.setMoves(1, 0);
        playerB.setMoves(0, 1);
        when(mockNonPickableCard.isPickable()).thenReturn(false);
        when(mockPickableCard.isPickable()).thenReturn(true);
        when(mockBoard.getUpperRow()).thenReturn(List.of(mockNonPickableCard));
        when(mockBoard.getLowerRow()).thenReturn(List.of(mockPickableCard));
        when(mockBoard.getUpperBuildings()).thenReturn(List.of());

        boolean result = gameManager.iPickedCard(playerA);

        assertFalse(result);
        verify(mockView1).notifyTurn("B", Move.PICK_FROM_DOWN);
        verify(mockView2).notifyTurn("B", Move.PICK_FROM_DOWN);
    }

    @Test
    void iPickedCard_PlayerWithOnlyBuildingsAvailable_NotSkipped() throws IOException {
        playerA.setMoves(1, 0);
        playerB.setMoves(0, 0);
        when(mockNonPickableCard.isPickable()).thenReturn(false);
        when(mockBoard.getUpperRow()).thenReturn(List.of(mockNonPickableCard));
        when(mockBoard.getUpperBuildings()).thenReturn(List.of(mockBuildingCard));
        when(mockBuildingCard.canBeBought(playerA)).thenReturn(true);

        assertFalse(gameManager.iPickedCard(playerA));
    }

    @Test
    void iPickedCard_AdditionalUpTileBuff_BuffConsumedAndZeroMovePlayerReordered() throws IOException {
        Player playerC = new Player("C");
        playersOrder.add(playerC);
        playerA.setMoves(1, 0);
        playerA.updateStats(SpecialBuff.ADDITIONAL_UP_TILE);
        playerB.setMoves(1, 0);
        when(mockNonPickableCard.isPickable()).thenReturn(false);
        when(mockPickableCard.isPickable()).thenReturn(true);
        when(mockBoard.getUpperRow())
                .thenReturn(List.of(mockNonPickableCard))
                .thenReturn(List.of(mockPickableCard));
        when(mockBoard.getUpperBuildings()).thenReturn(List.of());

        gameManager.iPickedCard(playerA);

        assertFalse(playerA.getSpecialBuffs().contains(SpecialBuff.ADDITIONAL_UP_TILE));
        assertTrue(playersOrder.indexOf(playerC) > playersOrder.indexOf(playerA));
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

        verify(mockView1).end();
        verify(mockView2).end();
    }
}
