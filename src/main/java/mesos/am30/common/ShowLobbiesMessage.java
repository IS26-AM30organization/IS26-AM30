package mesos.am30.common;

import java.util.Map;

/**
 * Show Lobbies from Server to Client.
 * <br/>This Class implements a Show Lobbies Message, used for Socket communication between Client and Server.
 * <br/>This type of message is expected to be sent only from the Server to the Client.
 */
public class ShowLobbiesMessage extends Message {
    private final Map<String, Integer> availableLobbies;

    /**
     * Constructor for ShowLobbiesMessage.
     * <br/><strong>Pre:</strong> type != null && availableLobbies != null
     * <br/><strong>Post:</strong> this.type = type && this.availableLobbies = availableLobbies
     *
     * @param type              Type of message.
     * @param availableLobbies  List of lobbies.
     */
    public ShowLobbiesMessage(MessageType type, Map<String, Integer> availableLobbies) {
        super(type);
        this.availableLobbies = availableLobbies;
    }

    /**
     * Getter for the attribute "availableLobbies".
     *
     * @return Available Lobbies.
     */
    public Map<String, Integer> getAvailableLobbies() {
        return availableLobbies;
    }
}
