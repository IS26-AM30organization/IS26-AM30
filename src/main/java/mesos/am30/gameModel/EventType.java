package mesos.am30.gameModel;

/**
 * Enumeration for the Events Type.
 * <br/>This enumeration is used in order to identify the type of Event in a Building/Event Card, in order to handle it in the right way.
 */
public enum EventType {

    /// Events which have to be handled only when drawn.
    ONETIME,

    /// Events which have to be handled at each Round (like some Buildings).
    ROUND,

    /// Events which have to be handled only at the end of the Game.
    FINAL,

    /// Event Hunt.
    HUNT,

    /// Event Cave Paintings.
    CAVE_PAINTINGS,

    /// Event Shamanic Ritual (and correlated Buildings).
    SHAMANIC_RITUAL,

    /// Event Sustenance, always handled as last.
    SUSTENANCE
}
