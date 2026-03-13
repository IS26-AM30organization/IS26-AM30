package mesos.am30.GameModel;

import java.util.HashSet;
import java.util.Set;

public class DoubleInventions implements IF_Event {
    private final Set<Integer> inventions;

    public DoubleInventions() {
        this.inventions = new HashSet<>(10);
    }

    public Set<Integer> getInventions() {
        return inventions;
    }

    @Override
    public void handleEvent(Player player) {}
}
