package mesos.am30.common.enumerations;

/**
 * Enumeration for the Client Choice communications.
 * <br/>This enumeration is used in order to identify a given Choice made by the Client; it is used by the communication
 * protocols in order to catch the correct meaning of a Message.
 */
public enum Choice {

    // ----- connection phase

    /// Create a Lobby.
    CREATE_LOBBY,

    /// Get all the available Lobbies.
    GET_AVAILABLE_LOBBIES,

    /// Join an available Lobby.
    JOIN_LOBBY,

    /// Set the Player's nickname.
    NICKNAME,

    // ----- game phase

    /// Choose a Tile.
    CHOOSE_TILE,

    /// Choose a Character Card.
    CHOOSE_CHARACTER,

    /// Choose a Building Card.
    CHOOSE_BUILDING,

    /// Notification about Rankings.
    RANKINGS
}
