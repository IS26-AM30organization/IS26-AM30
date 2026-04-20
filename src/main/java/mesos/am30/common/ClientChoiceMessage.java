package mesos.am30.common;

/**
 * Client Choice Message between Client-Server.
 * <br>This Class implements a Client Choice Message, used for Socket communication between Client and Server.
 * <br>This type of message is expected to be sent only from the Client to the Server.
 *
 *  @author LoreDN - Lorenzo Di Napoli
 *  @version 1.0
 *  @since 1.0
 */
public class ClientChoiceMessage extends Message {
    private final Choice choice;
    private final String nickname;
    private final Object parameter;

    /**
     * Constructor for ClientChoiceMessage.
     * <br><strong>Pre:</strong> type != null && choice != null && nickname != null && parameter != null
     * <br><strong>Post:</strong> this.type = type && this.choice = choice && this.nickname = nickname && this.parameter = parameter
     *
     * @param type Type of message
     * @param choice Type of choice
     * @param nickname Nickname of the Player
     * @param parameter Chosen parameter
     */
    public ClientChoiceMessage(MessageType type, Choice choice, String nickname, Object parameter) {
        super(type);
        this.choice = choice;
        this.nickname = nickname;
        this.parameter = parameter;
    }

    /**
     * Getter for the attribute "choice".
     *
     * @return Type of choice
     */
    public Choice getChoice() {
        return choice;
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
     * Getter for the attribute "parameter".
     *
     * @return Chosen parameter
     */
    public Object getParameter() {
        return parameter;
    }
}
