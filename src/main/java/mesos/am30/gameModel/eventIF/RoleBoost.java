package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;

public class RoleBoost implements IF_Event {
    private final Parameter role;
    private final int prestigePoints;

    public RoleBoost(int prestigePoints, Parameter role) {
        this.prestigePoints = prestigePoints;
        this.role = role;
    }

    public Parameter getRole() {
        return role;
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }

    @Override
    public void handleEvent(Player player) {}
}
