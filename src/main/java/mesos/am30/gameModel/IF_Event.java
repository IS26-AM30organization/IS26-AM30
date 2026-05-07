package mesos.am30.gameModel;

import java.io.Serializable;

public interface IF_Event extends Serializable {
    /**
     * handles current event, and updates player's stats accordingly.
     * @param player
     */
    void handleEvent(Player player);

    /**
     * Appends to corresponding StringBuilder parameters to display in TUI.
     * @param str1 any parameter
     * @param str2 any parameter
     * @param str3 any parameter
     */
    default void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {return;};
}
