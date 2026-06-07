package mesos.am30.gameModel.board;

import mesos.am30.client.IF_GameView;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

/**
 * Tests that all handleEvents do not crash on execution
 */
@ExtendWith(MockitoExtension.class)
class BoardHandleEventTest {

    private Board board;
    private List<Player> players;

    @BeforeEach
    void setUp() throws IOException {
        players = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            players.add(new Player("Player" + i));
        }

        List<IF_GameView> views = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            views.add(mock(IF_GameView.class));
        }

        board = new Board(players, views);
        board.prepare();
    }

    /**
     * Cycles on Decks and executes handleEvent for each card
     * Test passes if no handle event crashes
     */
    @Test
    void allEventCards_handleEvents() {
        for (List<Card> deck : board.getDecks()) {
            for (Card card : deck) {
                if (!card.isPickable()) {
                    assertDoesNotThrow( () -> card.discard(board), "Error on: " + card);
                }
            }
        }
    }

    @Test
    void allBuildingCards_handleEvent() {
        Player player = players.getFirst();

        for (List<BuildingCard> deck : board.getBuildingDecks()) {
            for (BuildingCard card : deck) {
                assertDoesNotThrow( () -> card.getEvent().handleEvent(player), "Error on: " + card);
            }
        }
    }

    /** Checks player's interaction with handle event for Event cards.
     */
    @Test
    void allEventCards_handleEvent_allPlayers() {
        for (List<Card> deck : board.getDecks()) {
            for (Card card : deck) {
                if (!card.isPickable()) {
                    for (Player player : players) {
                        assertDoesNotThrow( () -> card.discard(board), "Error on: "  + card + " on player: " + player.getNickname()
                        );
                    }
                }
            }
        }
    }

    /** Checks player's interaction with handle event for Building cards.
     */
    @Test
    void allBuildingCards_handleEvent_allPlayers() {
        for (List<BuildingCard> deck : board.getBuildingDecks()) {
            for (BuildingCard card : deck) {
                for (Player player : players) {
                    assertDoesNotThrow( () -> card.getEvent().handleEvent(player), "Error on: " + card + " on player: " + player.getNickname());
                }
            }
        }
    }
}
