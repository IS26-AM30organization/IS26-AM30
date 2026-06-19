package mesos.am30.common.messages;

import mesos.am30.common.enumerations.MessageType;
import mesos.am30.common.enumerations.ViewParameter;

import java.util.List;

/**
 * Model Update Message between Client-Server.
 * <br/>This Class implements a Model Update Message, used for Socket communication between Client and Server.
 * <br/>This type of message is expected to be sent only from the Controller to the Client.
 */
public class ModelUpdateMessage extends Message {
    private final ViewParameter toUpdate;
    private final List<Object> parameters;

    /**
     * Constructor for ModelUpdateMessage.
     * <br/><strong>Pre:</strong> type != null &amp;&amp; toUpdate != null &amp;&amp; parameters != null
     * <br/><strong>Post:</strong> this.type = type &amp;&amp; this.toUpdate = toUpdate &amp;&amp; this.parameters = parameters
     *
     * @param type          Type of message.
     * @param toUpdate      Field to update.
     * @param parameters    Updated parameters.
     */
    public ModelUpdateMessage(MessageType type, ViewParameter toUpdate, List<Object> parameters) {
        super(type);
        this.toUpdate = toUpdate;
        this.parameters = parameters;
    }

    /**
     * Getter for the attribute "toUpdate".
     *
     * @return Field to update.
     */
    public ViewParameter getToUpdate() {
        return toUpdate;
    }

    /**
     * Getter for the attribute "parameters".
     *
     * @return Updated parameters.
     */
    public List<Object> getParameters() {
        return parameters;
    }
}
