package mesos.am30.GameModel;


import java.util.*;

public class Player {
    private final Map<Parameter, Integer> parameters;
    private final Map<Parameter, List<CharacterCard>> tribe;
    private final Set<Integer> inventions;
    private final Set<BuildingCard> buildings;

    public Player() {
        this.parameters = new HashMap<>();
        this.tribe = new HashMap<>();
        this.inventions = new HashSet<>(10);
        this.buildings = new HashSet<>();
    }

    public Map<Parameter, Integer> getParameters() {
        return parameters;
    }

    public Map<Parameter, List<CharacterCard>> getTribe() {
        return tribe;
    }

    public Set<Integer> getInventions() {
        return inventions;
    }

    public Set<BuildingCard> getBuildings() {
        return buildings;
    }

    public void updateStats(Parameter stat, int sum) {}
}
