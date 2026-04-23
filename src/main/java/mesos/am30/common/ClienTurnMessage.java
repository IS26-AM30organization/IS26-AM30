package mesos.am30.common;

/**
 * Client Turn Message between Client-Server.
 * <br>This Class implements a Client Turn Message, used for Socket communication between Client and Server.
 * <br>This type of message is expected to be sent only from the Controller to the Client.
 *
 *  @author LoreDN - Lorenzo Di Napoli
 *  @version 1.0
 *  @since 1.0
 */
public class ClienTurnMessage extends Message {
    private final String nickname;
    private final Move move;

    /**
     * Constructor for ClientTurnMessage.
     * <br><strong>Pre:</strong> type != null && nickname != null && move != null
     * <br><strong>Post:</strong> this.type = type && this.nickname = nickname && this.move = move
     *
     * @param type Type of message
     * @param nickname Nickname of the Player
     * @param move Move expected to be done
     */
    public ClienTurnMessage(MessageType type, String nickname, Move move) {
        super(type);
        this.nickname = nickname;
        this.move = move;
    }

    /**
     * Getter for the attribute "nickname".
     *
     * @return Nickname of the Player
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Getter for the attribute "move".
     *
     * @return Move expected to be done
     */
    public Move getMove() {
        return move;
    }
}
