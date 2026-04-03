package mesos.am30.GameModel;

public class CharacterCard extends Card {
    private final Parameter role;
    private final Integer value;
    private final Integer prestigePoints;

    public CharacterCard(int era, Parameter role, Integer value, Integer prestigePoints) {
        super(era);
        this.role = role;
        this.value = value;
        this.prestigePoints = prestigePoints;
    }

    public Parameter getRole() {
        return role;
    }

    public Integer getValue() {
        return value;
    }

    public Integer getPrestigePoints() {
        return prestigePoints;
    }
}

