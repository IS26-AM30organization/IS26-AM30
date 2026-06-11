package mesos.am30.common;

import java.io.Serializable;

/**
 * Message between Client-Server.
 * <br/>This Class implements a basic Message, used for Socket communication between Client and Server.
 */
public class Message implements Serializable {
    private final MessageType type;

    /**
     * Constructor for Message.
     * <br/><strong>Pre:</strong> type != null
     * <br/><strong>Post:</strong> this.type = type
     *
     * @param type Type of message.
     */
    public Message(MessageType type) {
        this.type = type;
    }

    /**
     * Getter for the attribute "type".
     *
     * @return Type of message.
     */
    public MessageType getType() {
        return type;
    }
}
