package mesos.am30.gameModel;

import java.io.Serializable;

public interface IF_Event extends Serializable {
    void handleEvent(Player player);
}
