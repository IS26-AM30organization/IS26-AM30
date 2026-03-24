package mesos.am30.GameModel;

import java.util.List;
import java.util.Optional;
//If schamanic boost of checking player with most stars is supposed to be defined here, it is not!
public class StatsBoost implements IF_Event {
    private final EventType type;
    private final int food;
    private final Integer prestigePoints;
    private Parameter tribeType;
    private int alreadyDiscounted = 0;

    public StatsBoost(int food, int prestigePoints, Parameter role, EventType type) {
        this.food = food;
        this.prestigePoints = prestigePoints;
        this.tribeType= role;
        this.type = type;
    }
    public StatsBoost(int food, Parameter role, EventType type) {
        this(food, 0, role, type);
    }

    public Parameter getRole() {
        return tribeType;
    }
    public EventType getType() {
        return type;
    }

    @Override
    public void handleEvent(Player player) {
        //player's parameters contains 'discounted' statistics; if counted for a previous Event, only additional character must be considered
        int multiplier = player.getCharacterType(tribeType).size() - alreadyDiscounted;
        alreadyDiscounted = alreadyDiscounted + multiplier;

        if (food!=0 && multiplier!=0) player.updateStats(Parameter.FOOD, food*multiplier);
        if (prestigePoints!=0 && multiplier!=0) player.updateStats(Parameter.PRESTIGE_POINTS, prestigePoints*multiplier);

        return;
    }
}
