package mesos.am30.GameModel;

import java.util.Objects;

public class CharacterCard extends Card {
    private final Parameter role;
    private final Integer value;
    private final Integer prestigePoints;

    public CharacterCard(int era, Parameter role, Integer value, Integer prestigePoints, int id) {
        super(era, id);
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

    @Override
    public boolean isPickacble() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CharacterCard that = (CharacterCard) o;
        return role == that.role && Objects.equals(value, that.value) && Objects.equals(prestigePoints, that.prestigePoints) && this.id == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, value, prestigePoints);
    }

    protected void drawUp(Board board){
        board.drawUp(this);
    }

    protected void drawDown(Board board){
        board.drawDown(this);
    }

    protected void discard(Board board){
        board.discard(this);
    }
}