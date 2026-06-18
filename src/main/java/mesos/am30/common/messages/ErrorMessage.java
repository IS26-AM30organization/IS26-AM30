package mesos.am30.common.messages;

import mesos.am30.common.enumerations.ErrorType;
import mesos.am30.common.enumerations.MessageType;

/**
 * Error Message between Client-Server.
 * <br/>This Class implements an Error Message, used for Socket communication between Client and Server.
 * <br/>This type of message is expected to be sent only from the Controller to the Client.
 */
public class ErrorMessage extends Message {
    private final ErrorType errorType;

    /**
     * Constructor for ErrorMessage.
     * <br/><strong>Pre:</strong> type != null &amp;&amp; errorType != null
     * <br/><strong>Post:</strong> this.type = type &amp;&amp; this.errorType = errorType
     *
     * @param type      Type of message.
     * @param errorType Type of error.
     */
    public ErrorMessage(MessageType type, ErrorType errorType) {
        super(type);
        this.errorType = errorType;
    }

    /**
     * Getter for the attribute "errorType".
     *
     * @return Type of error.
     */
    public ErrorType getError() {
        return errorType;
    }
}
