package mesos.am30.gameModel;

/**
 * Enumeration for the Game Parameters.
 * <br/>This enumeration is used in order to identify both the role of a Character Card, both the value of each parameter for each Player
 * (food, prestige point, inventions...).
 * <br/>In order to identify the parameters given by each role, have been used the same enumeration values.
 */
public enum Parameter {

    /// Role Inventor / Parameter for the Players' inventions.
    INVENTOR,

    /// Role Builder / Parameter for the Players' Buildings discount.
    BUILDER,

    /// Role Gatherer / Parameter for the Players' food discount during Sustenance.
    GATHERER,

    /// Role Artist.
    ARTIST,

    /// Role Shaman / Parameter for the Players' stars.
    SHAMAN,

    /// Role Hunter.
    HUNTER,

    /// Parameter for the Players' total food.
    FOOD,

    /// Parameter for the Players' total prestige points.
    PRESTIGE_POINTS
}
