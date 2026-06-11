package mesos.am30.common;

/**
 * Enumeration for the Game Model update communications.
 * <br/>This enumeration is used in order to notify the Client about a Game Model update; it is used by the communication
 * protocols in order to catch the correct meaning of a Message.
 */
public enum ViewParameter {

    /// The Player's Tribe has been updated.
    PLAYERS,

    /// The Tiles have been updated.
    TILES,

    /// The Upper Row has been updated.
    UPPER_ROW,

    /// The Upper Buildings Row has been updated.
    UPPER_BUILDINGS,

    /// The Lower Row has been updated.
    LOWER_ROW,

    /// The Lower Buildings Row has been updated.
    LOWER_BUILDINGS
}
