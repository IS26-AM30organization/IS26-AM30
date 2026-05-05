package mesos.am30.common;

public enum MessageType {
    // connection phase
    CONFIRM_CONNECTION,
    SHOW_LOBBIES,
    CONFIRM_LOBBY_JOINED,
    NICKNAME,

    // game phase
    NOTIFY,
    CHOOSE,
    ERROR,
    UPDATE,
    PING,
    END
}