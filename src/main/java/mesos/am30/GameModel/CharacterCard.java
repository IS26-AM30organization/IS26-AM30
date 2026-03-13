package mesos.am30.GameModel;

import java.util.Optional;

public class CharacterCard extends Card {
    private final int playersMinimum;
    private Parameter role;
    private final Integer value;
    private final Integer prestigePoints;

    public CharacterCard(int era, int playersMinimum, Parameter role, Integer value, Integer prestigePoints) {}

    public int playersMinimum() {
        return playersMinimum;
    }

    public Parameter getRole() {
        return role;
    }

    public Optional<Integer> getValue() {}

    public Optional<Integer> getPrestigePoints() {}
}

