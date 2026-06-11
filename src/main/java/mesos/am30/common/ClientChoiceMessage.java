package mesos.am30.common;

/**
 * Client Choice Message between Client-Server.
 * <br/>This Class implements a Client Choice Message, used for Socket communication between Client and Server.
 * <br/>This type of message is expected to be sent only from the Client to the Server.
 */
public class ClientChoiceMessage extends Message {
    private final Choice choice;
    private final String identifier;
    private final Object parameter;

    /**
     * Constructor for ClientChoiceMessage.
     * <br/><strong>Pre:</strong> type != null && choice != null && identifier != null && parameter != null
     * <br/><strong>Post:</strong> this.type = type && this.choice = choice && this.identifier = identifier && this.parameter = parameter
     *
     * @param type          Type of message.
     * @param choice        Type of choice.
     * @param identifier    Identifier of the Lobby/Player.
     * @param parameter     Chosen parameter.
     */
    public ClientChoiceMessage(MessageType type, Choice choice, String identifier, Object parameter) {
        super(type);
        this.choice = choice;
        this.identifier = identifier;
        this.parameter = parameter;
    }

    /**
     * Getter for the attribute "choice".
     *
     * @return Type of choice.
     */
    public Choice getChoice() {
        return choice;
    }

    /**
     * Getter for the attribute "identifier".
     *
     * @return Identifier of the Lobby/Player.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Getter for the attribute "parameter".
     *
     * @return Chosen parameter.
     */
    public Object getParameter() {
        return parameter;
    }
}
