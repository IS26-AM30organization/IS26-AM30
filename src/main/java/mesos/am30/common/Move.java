package mesos.am30.common;

/**
 * Enumeration for the Move communications.
 * <br/>This enumeration is used in order to notify the next valid Move for the Client; it is used by the communication
 * protocols in order to catch the correct meaning of a Message.
 */
public enum Move {

    /// The Player has to choose a Tile.
    PICK_TILE,

    /// The Player has to choose a Card from the Upper Rows.
    PICK_FROM_UP,

    /// The Player has to choose a Card from the Lower Rows.
    PICK_FROM_DOWN,

    /// The Player has to choose a Card from any Row.
    PICK_ANY_CARD
}
