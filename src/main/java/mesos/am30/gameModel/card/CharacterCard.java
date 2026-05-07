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
    public void displayCard() {
        List<String> str = new ArrayList<>();

        str.add("[" + role + "]");
        if (value != 0) str.add("ITEM: " + value);
        if (prestigePoints != 0) str.add("PP: " + prestigePoints);

        System.out.print(String.join(" ", str) + "\n");
    }
    @Override
    public void createRow(StringBuilder rowRoles, StringBuilder rowValue, StringBuilder rowPP) {
        String r = "[" + role + "]";
        String i = "";
        if (value != 0) i = "ITEM: " + value;
        String pp = "";
        if (prestigePoints != 0) pp = "PP: " + prestigePoints;

        //need to take longest word
        int maxWidth = r.length();
        if (i.length() > maxWidth) {
            maxWidth = i.length();
        }
        if (pp.length() > maxWidth) {
            maxWidth = pp.length();
        }
        maxWidth += 3;

        rowRoles.append(r);
        for (int x = r.length(); x < maxWidth; x++) rowRoles.append(" ");

        rowValue.append(i);
        for (int x = i.length(); x < maxWidth; x++) rowValue.append(" ");

        rowPP.append(pp);
        for (int x = pp.length(); x < maxWidth; x++) rowPP.append(" ");
    }


}