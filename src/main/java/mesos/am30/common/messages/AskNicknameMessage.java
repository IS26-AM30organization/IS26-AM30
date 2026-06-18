package mesos.am30.common.messages;

import mesos.am30.common.enumerations.MessageType;

/**
 * Ask Nickname Message between Client-Server.
 * <br/>This Class implements an Ask Nickname Message, used for Socket communication between Client and Server.
 * <br/>This type of message is expected to be sent only from the Server to the Client.
 */
public class AskNicknameMessage extends Message {
    private final String lobbyCode;

    /**
     * Constructor for AskNicknameMessage.
     * <br/><strong>Pre:</strong> type != null &amp;&amp; lobbyCode != null
     * <br/><strong>Post:</strong> this.type = type &amp;&amp; this.lobbyCode = lobbyCode
     *
     * @param type      Type of message.
     * @param lobbyCode Code of the lobby.
     */
    public AskNicknameMessage(MessageType type, String lobbyCode) {
        super(type);
        this.lobbyCode = lobbyCode;
    }

    /**
     * Getter for the attribute "lobbyCode".
     *
     * @return Code of the lobby.
     */
    public String getLobbyCode() {
        return lobbyCode;
    }
}
