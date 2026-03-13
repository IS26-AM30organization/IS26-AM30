package mesos.am30.GameModel;

import java.util.Optional;

public class StatsBoost implements IF_Event {
    private Parameter role;
    private final EventType type;
    private final Integer food;
    private final Integer prestigePoints;

    public StatsBoost(int food, int prestigePoints, Parameter role, EventType type) {}

    public Parameter getRole() {
        return role;
    }

    public EventType getType() {
        return type;
    }

    public Optional<Integer> getFood() {}

    public Optional<Integer> getPrestigePoints() {}

    @Override
    public void handleEvent(Player player) {}
}
