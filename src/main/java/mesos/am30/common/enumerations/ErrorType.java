package mesos.am30.common.enumerations;

/**
 * Enumeration for the Error communications.
 * <br/>This enumeration is used in order to notify a given Error made by the Client; it is used by the communication
 * protocols in order to catch the correct meaning of a Message.
 */
public enum ErrorType {

    // ----- connection phase

    /// The IP address is not valid.
    WRONG_IP,

    /// Lobby with given code already exists.
    ALREADY_EXISTING_LOBBY,

    /// Lobby with given code does not exist.
    NOT_EXISTING_LOBBY,

    /// The number of Players is not valid.
    WRONG_PLAYERS_NUMBER,

    /// The lobby code is not valid / already in use.
    INVALID_LOBBY_CODE,

    /// The nickname is not valid / already in use.
    WRONG_NICKNAME,

    /// Lobby with given code is already full.
    FULL_LOBBY,

    // ----- game phase

    /// Not your turn to move.
    NOT_YOUR_TURN,

    /// The Tile is not valid / already chosen.
    WRONG_TILE,

    /// The Card is not valid / already chosen.
    WRONG_CARD,

    /// Not enough food to buy a Building.
    NOT_ENOUGH_FOOD,

    /// The Game has ended to a Client disconnection.
    END_FOR_DISCONNECTION,

    /// Your connection has crashed.
    CONNECTION_CRASHED,

    /// There was an error while querying the DB.
    DB_ERROR
}
