package mesos.am30.common;

/**
 * Error Message between Client-Server.
 * <br>This Class implements an Error Message, used for Socket communication between Client and Server.
 * <br>This type of message is expected to be sent only from the Controller to the Client.
 *
 * @author LoreDN - Lorenzo Di Napoli
 * @version 1.0
 * @since 1.0
 */
public class ErrorMessage extends Message {
    private final ErrorType errorType;

    /**
     * Constructor for ErrorMessage.
     * <br><strong>Pre:</strong> type != null && errorType != null
     * <br><strong>Post:</strong> this.type = type && this.errorType = errorType
     *
     * @param type Type of message
     * @param errorType Type of error
     */
    public ErrorMessage(MessageType type, ErrorType errorType) {
        super(type);
        this.errorType = errorType;
    }

    /**
     * Getter for the attribute "errorType".
     *
     * @return Type of error
     */
    public ErrorType getError() {
        return errorType;
    }
}
