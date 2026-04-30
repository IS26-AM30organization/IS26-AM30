package mesos.am30.GameModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Tile implements Serializable {
    private Player currentPlayer;
    private final Integer upArrows;
    private final Integer downArrows;
    private final Integer food;

    public Tile(Integer upArrows, Integer downArrows, Integer food) {
        this.upArrows = upArrows;
        this.downArrows = downArrows;
        this.food = food;
    }

    public Optional<Player> getCurrentPlayer() {
        return Optional.ofNullable(this.currentPlayer);
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void clearCurrentPlayer() {this.currentPlayer = null;}

    public Integer getUpArrows() {
        return (upArrows == null) ? 0 : upArrows;
    }

    public Integer getDownArrows() {
        return (downArrows == null) ? 0 : downArrows;
    }

    public Integer getFood() {
        return food;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return Objects.equals(currentPlayer, tile.currentPlayer) && Objects.equals(upArrows, tile.upArrows) && Objects.equals(downArrows, tile.downArrows) && Objects.equals(food, tile.food);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPlayer, upArrows, downArrows, food);
    }

    public void displayTile() {
        List<String> str = new ArrayList<>();

        if (upArrows != null) str.add("\033[34m" + "UP: " + "\033[0m" + upArrows);
        if (downArrows != null) str.add("\033[31m" + "DOWN: "+ "\033[0m" + downArrows);
        if (currentPlayer != null) str.add("\033[32m" + "PL: " + currentPlayer.getNickname() + "\033[0m");

        System.out.print(String.join(" ", str) + "      ");
    }

    public void createRow(StringBuilder ln1, StringBuilder ln2, StringBuilder ln3) {
        String r = "";
        if (food != null) r = "\033[31m" + "Up: " + "\033[0m" + food;
        if (upArrows != null) r = "\033[32m" + "Up: " + "\033[0m" + upArrows;
        String i = "";
        if (downArrows != null) i = "\033[31m" + "Down: " + "\033[0m" + downArrows;
        String pp = "";
        if (currentPlayer != null) pp = "\033[33m" + "Player: " + "\033[0m" + currentPlayer.getNickname();

        //Ansi characters are a problem for length size, must be ignored
        String ansiRegex = "\u001B\\[[;\\d]*m";

        int real1 = r.replaceAll(ansiRegex, "").length();
        int real2 = i.replaceAll(ansiRegex, "").length();
        int real3 = pp.replaceAll(ansiRegex, "").length();

        int maxWidth = Math.max(real1, Math.max(real2, real3)) + 3;

        ln1.append(r);
        for (int x = real1; x < maxWidth; x++) ln1.append(" ");

        ln2.append(i);
        for (int x = real2; x < maxWidth; x++) ln2.append(" ");

        ln3.append(pp);
        for (int x = real3; x < maxWidth; x++) ln3.append(" ");
    }

}
