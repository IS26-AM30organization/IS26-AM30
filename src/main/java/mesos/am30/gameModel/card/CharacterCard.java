package mesos.am30.gameModel.card;

import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.board.Board;

import java.util.ArrayList;
import java.util.List;
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
    public boolean isPickable() {
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, value, prestigePoints);
    }

    public void drawUp(Board board){
        board.drawUp(this);
    }

    public void drawDown(Board board){
        board.drawDown(this);
    }

    public void discard(Board board){
        board.discard(this);
    }

    @Override
    public void createRow(StringBuilder rowRoles, StringBuilder rowValue, StringBuilder rowPP) {
        String r = role + "";
        StringBuilder i = new StringBuilder();
        if (value != 0) {
            valueToItem(i);
            i.append(value);
        }
        String pp = "";
        if (prestigePoints != 0) pp = "PP: " + prestigePoints;

        //need to take the longest word
        int maxWidth = r.length();
        if (i.length() > maxWidth) {
            maxWidth = i.length();
        }
        if (pp.length() > maxWidth) {
            maxWidth = pp.length();
        }
        maxWidth += 3;

        rowRoles.append(r).append("\uD83D\uDC64");
        for (int x = r.length(); x < maxWidth-1; x++) rowRoles.append(" ");

        rowValue.append(i);
        for (int x = i.length(); x < maxWidth; x++) rowValue.append(" ");

        rowPP.append(pp);
        for (int x = pp.length(); x < maxWidth; x++) rowPP.append(" ");
    }

    /**
     * Based on the card's role, the item must be specified to be displayed on terminal
     */
    private void valueToItem(StringBuilder str1) {
        switch (role) {
            case INVENTOR -> str1.append("inv: ");
            case BUILDER -> str1.append("food: ");
            case GATHERER -> str1.append("food: ");
            case ARTIST -> str1.append("");
            case SHAMAN -> str1.append("starts: ");
            case HUNTER -> str1.append("food");
            default -> str1.append("ITEM: ");
        }
    }
}