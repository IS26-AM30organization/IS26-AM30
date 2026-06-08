package mesos.am30.common;

import java.util.List;
import java.util.Map;

/**
 * Ranking Message between Client-Server.
 * <br>This Class implements a Ranking Message, used for Socket communication between Client and Server.
 * <br>This type of message is expected to be sent only from the Server to the Client.
 *
 *  @author LoreDN - Lorenzo Di Napoli
 *  @version 1.0
 *  @since 1.0
 */
public class RankingMessage extends Message {
    private final Map<String, String> playerRank;
    private final List<Map<String, String>> globalRankings;

    /**
     * Constructor for RankingMessage.
     * <br><strong>Pre:</strong> type != null && playerRank != null && globalRankings != null
     * <br><strong>Post:</strong> this.type = type && this.playerRank = playerRank && this.globalRankings = globalRankings
     *
     * @param type Type of message
     * @param playerRank Rank of the Player
     * @param globalRankings Ranking of all Players
     */
    public RankingMessage(MessageType type, Map<String, String> playerRank, List<Map<String, String>> globalRankings) {
        super(type);
        this.playerRank = playerRank;
        this.globalRankings = globalRankings;
    }

    /**
     * Getter for the attribute "playerRank".
     *
     * @return Player's Rank
     */
    public Map<String, String> getPlayerRank() {
        return playerRank;
    }

    /**
     * Getter for the attribute "globalRankings".
     *
     * @return Global Rankings
     */
    public List<Map<String, String>> getGlobalRankings() {
        return globalRankings;
    }
}
