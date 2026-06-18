package mesos.am30.common.enumerations;

/**
 * Enumeration for the Message Socket communications.
 * <br/>This enumeration is used in order to identify a given type of Socket Message.
 */
public enum MessageType {

    // ----- connection phase

    /// Connection to the Server established successfully.
    CONFIRM_CONNECTION,

    /// Show the Lobbies.
    SHOW_LOBBIES,

    /// Connection to the Lobby established successfully.
    CONFIRM_LOBBY_JOINED,

    /// Selection of the Player's Nickname.
    NICKNAME,

    // ----- game phase

    /// Notification about the Game progression.
    NOTIFY,

    /// Type of choice taken by the Player.
    CHOOSE,

    /// Error made by the Client.
    ERROR,

    /// Update of the Game Model.
    UPDATE,

    /// Request to query the DB for the Rankings.
    RANKINGS,

    /// Response to the DB query for the Rankings.
    SHOW_RANKINGS,

    ///Ping message to check the Connection.
    PING,

    /// End of the Game.
    END
}