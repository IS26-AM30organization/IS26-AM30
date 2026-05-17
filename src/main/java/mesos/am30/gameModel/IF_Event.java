package mesos.am30.gameModel;

import java.io.Serializable;

/**
 * Events' Interface.
 * <br/>This interface follows the "<strong>Strategy</strong>" Design Pattern, adn it used in order to define the polymorphic methods needed in order
 * to handle/display correctly all the Events.
 */
public interface IF_Event extends Serializable {

    /**
     * Handle the specific Event.
     * <br/><strong>Pre:</strong> player != null
     *
     * @param player Player on which the Event needs to be handled on.
     */
    void handleEvent(Player player);

    /**
     * Add Event's info to the StringBuilders for the Terminal.
     * <br/>This method works by adding the Event's info to the StringBuilders, in order to display it properly on the Terminal.
     * <br/><strong>Pre:</strong> str1 != null && str2 != null && str3 != null
     *
     * @param str1 Line containing the identifier of the Event.
     * @param str2 Line containing the main attribute of the Event.
     * @param str3 Line containing extra attributes of the Event.
     */
    void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3);

    /**
     * Get specific information about the Event.
     *
     * @param info Stringbuilder receiving the information.
     *
     * @return Stringbuilder with newly appended information.
     */
    String getInfo(StringBuilder info);

    /**
     * Get the identifier for the Event Art to display on the GUI.
     *
     * @return Event Art identifier.
     */
    String getArt();
}
