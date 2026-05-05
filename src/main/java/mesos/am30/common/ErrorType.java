package mesos.am30.common;

public enum ErrorType {
    // connection phase
    WRONG_IP,
    ALREADY_EXISTING_LOBBY,
    NOT_EXISTING_LOBBY,
    WRONG_PLAYERS_NUMBER,
    WRONG_NICKNAME,
    FULL_LOBBY,

    // game phase
    NOT_YOUR_TURN,
    WRONG_TILE,
    WRONG_CARD,
    NOT_ENOUGH_FOOD,
    END_FOR_DISCONNECTION,
    CONNECTION_CRASHED
}
