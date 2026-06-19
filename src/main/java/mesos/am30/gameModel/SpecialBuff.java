package mesos.am30.gameModel;

/**
 * Enumeration for the Special Buffs.
 * <br/>This enumeration is used in order to address to a Player a type of Special Buff (if gained during the Game).
 */
public enum SpecialBuff {

    /// The Player has an (optional) extra pick from the upper row.
    ADDITIONAL_UP_TILE,

    /// The Player has an extra food unit on the Tile selection.
    ADDITIONAL_FOOD_TILE
}
