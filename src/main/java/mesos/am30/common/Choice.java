package mesos.am30.common;

public enum Choice {
    // connection phase
    CREATE_LOBBY,
    GET_AVAILABLE_LOBBIES,
    JOIN_LOBBY,
    NICKNAME,

    // game phase
    CHOOSE_TILE,
    CHOOSE_CHARACTER,
    CHOOSE_BUILDING,
    RANKINGS
}
