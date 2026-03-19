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

    public void updateStats(Parameter stat,int sum){
        //using getOrDefault default method of HashMap -> if no value is present, returns defaultValue.
        int currentValue = this.parameters.getOrDefault(stat, 0);

        int updatedValue = currentValue + sum;
        if(updatedValue < 0) updatedValue = 0;
        this.parameters.put(stat, updatedValue);
    }

    public void updateStats(SpecialBuff eventBuff){
        specialBuffs.add(eventBuff);
    }

    public List<CharacterCard> getCharacterType(Parameter characterType){
        return tribe.getOrDefault(characterType, new ArrayList<>());
    }
}
