package mesos.am30.client.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import mesos.am30.client.ViewModel;
import mesos.am30.client.VirtualView;
import mesos.am30.common.ErrorType;
import mesos.am30.common.Move;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Card;
import mesos.am30.gameModel.card.CharacterCard;

import java.io.IOException;
import java.util.*;


/**
 * Controller for the main game table scene.
 * <br/>Manages card rows, buildings, tiles, and player panels.
 * <br/>Delegates tribe and leaderboard views to {@link TribeGui} and {@link LeaderboardGui}.
 */
public class TableGui {
    ViewModel vBoard;
    String nickname;
    VirtualView vView;

    @FXML    private Button upper1;
    @FXML    private Button upper2;
    @FXML    private Button upper3;
    @FXML    private Button upper4;
    @FXML    private Button upper5;
    @FXML    private Button upper6;
    @FXML    private Button upper7;
    @FXML    private Button upper8;
    @FXML    private Button upper9;
    @FXML    private Button lower1;
    @FXML    private Button lower2;
    @FXML    private Button lower3;
    @FXML    private Button lower4;
    @FXML    private Button lower5;
    @FXML    private Button lower6;
    @FXML    private Button lower7;
    @FXML    private Button lower8;
    @FXML    private Button lower9;
    @FXML    private Button upB1;
    @FXML    private Button upB2;
    @FXML    private Button upB3;
    @FXML    private Button upB4;
    @FXML    private Button upB5;
    @FXML    private Button downB1;
    @FXML    private Button downB2;
    @FXML    private Button downB3;
    @FXML    private Button downB4;
    @FXML    private Button downB5;

    @FXML    private Label turnLabel;
    @FXML    private Label errorLabel;
    @FXML    private Label eraLabel;

    @FXML    private HBox tiles;
    @FXML    private VBox rows;
    @FXML    private HBox playersRow;

    @FXML    private VBox player1;
    @FXML    private VBox player2;
    @FXML    private VBox player3;
    @FXML    private VBox player4;
    @FXML    private VBox player5;
    @FXML    private Label name1;
    @FXML    private Label name2;
    @FXML    private Label name3;
    @FXML    private Label name4;
    @FXML    private Label name5;
    @FXML    private Label pt1;
    @FXML    private Label pt2;
    @FXML    private Label pt3;
    @FXML    private Label pt4;
    @FXML    private Label pt5;
    @FXML    private Label food1;
    @FXML    private Label food2;
    @FXML    private Label food3;
    @FXML    private Label food4;
    @FXML    private Label food5;
    @FXML    private Label people1;
    @FXML    private Label people2;
    @FXML    private Label people3;
    @FXML    private Label people4;
    @FXML    private Label people5;
    @FXML    private Label buildnum1;
    @FXML    private Label buildnum2;
    @FXML    private Label buildnum3;
    @FXML    private Label buildnum4;
    @FXML    private Label buildnum5;
    @FXML    private Label build1;
    @FXML    private Label build2;
    @FXML    private Label build3;
    @FXML    private Label build4;
    @FXML    private Label build5;
    @FXML    private Label gathnum1;
    @FXML    private Label gathnum2;
    @FXML    private Label gathnum3;
    @FXML    private Label gathnum4;
    @FXML    private Label gathnum5;
    @FXML    private Label gath1;
    @FXML    private Label gath2;
    @FXML    private Label gath3;
    @FXML    private Label gath4;
    @FXML    private Label gath5;
    @FXML    private Label artnum1;
    @FXML    private Label artnum2;
    @FXML    private Label artnum3;
    @FXML    private Label artnum4;
    @FXML    private Label artnum5;
    @FXML    private Label art1;
    @FXML    private Label art2;
    @FXML    private Label art3;
    @FXML    private Label art4;
    @FXML    private Label art5;
    @FXML    private Label invnum1;
    @FXML    private Label invnum2;
    @FXML    private Label invnum3;
    @FXML    private Label invnum4;
    @FXML    private Label invnum5;
    @FXML    private Label inv1;
    @FXML    private Label inv2;
    @FXML    private Label inv3;
    @FXML    private Label inv4;
    @FXML    private Label inv5;
    @FXML    private Label shamnum1;
    @FXML    private Label shamnum2;
    @FXML    private Label shamnum3;
    @FXML    private Label shamnum4;
    @FXML    private Label shamnum5;
    @FXML    private Label sham1;
    @FXML    private Label sham2;
    @FXML    private Label sham3;
    @FXML    private Label sham4;
    @FXML    private Label sham5;
    @FXML    private Label huntnum1;
    @FXML    private Label huntnum2;
    @FXML    private Label huntnum3;
    @FXML    private Label huntnum4;
    @FXML    private Label huntnum5;
    @FXML    private Label hunt1;
    @FXML    private Label hunt2;
    @FXML    private Label hunt3;
    @FXML    private Label hunt4;
    @FXML    private Label hunt5;
    @FXML    private HBox invlist1;
    @FXML    private HBox invlist2;
    @FXML    private HBox invlist3;
    @FXML    private HBox invlist4;
    @FXML    private HBox invlist5;
    @FXML    private Button exitButton;
    @FXML    private Button leaderboardButton;

    private List<Button> uppers;
    private List<Button> lowers;
    private List<Button> upBs;
    private List<Button> downBs;
    public static HashMap<Player, String> colors;
    private VBox[] players;
    private Label[] pNames;
    private Label[] pPts;
    private Label[] pFoods;
    private Label[] pPeoples;
    private Label[] pBuildnums;
    private Label[] pBuilds;
    private Label[] pGathnums;
    private Label[] pGaths;
    private Label[] pArtnums;
    private Label[] pArts;
    private Label[] pInvnums;
    private Label[] pInvs;
    private Label[] pShamnums;
    private Label[] pShams;
    private Label[] pHuntnums;
    private Label[] pHunts;
    private HBox[] pInvlists;

    private TribeGui tribeController;
    private LeaderboardGui leaderboardController;

    private Map<String, String> cachedPlayerRank;
    private List<Map<String, String>> cachedGlobalRankings;

    @FXML   VBox infoBox;
    @FXML   StackPane infoPreview;
    @FXML   Label infoLabel;

    /** Initializes UI component arrays and loads tribe and leaderboard sub-controllers. */
    @FXML
    public void initialize() throws IOException {
        uppers = new ArrayList<>(List.of(upper1,upper2,upper3,upper4,upper5,upper6,upper7,upper8,upper9));
        lowers = new ArrayList<>(List.of(lower1,lower2,lower3,lower4,lower5,lower6,lower7,lower8,lower9));
        upBs = new ArrayList<>(List.of(upB1,upB2,upB3,upB4,upB5));
        downBs = new ArrayList<>(List.of(downB1,downB2,downB3,downB4,downB5));
        players = new VBox[]{player1, player2, player3, player4, player5};
        pNames = new Label[]{name1,name2,name3,name4,name5};
        pPts = new Label[]{pt1,pt2,pt3,pt4,pt5};
        pFoods = new Label[]{food1,food2,food3,food4,food5};
        pPeoples = new Label[]{people1,people2,people3,people4,people5};
        pBuildnums = new Label[]{buildnum1,buildnum2,buildnum3,buildnum4,buildnum5};
        pBuilds = new Label[]{build1,build2,build3,build4,build5};
        pGathnums = new Label[]{gathnum1,gathnum2,gathnum3,gathnum4,gathnum5};
        pGaths = new Label[]{gath1,gath2,gath3,gath4,gath5};
        pArtnums = new Label[]{artnum1,artnum2,artnum3,artnum4,artnum5};
        pArts = new Label[]{art1,art2,art3,art4,art5};
        pInvnums = new Label[]{invnum1,invnum2,invnum3,invnum4,invnum5};
        pInvs = new Label[]{inv1,inv2,inv3,inv4,inv5};
        pShamnums = new Label[]{shamnum1,shamnum2,shamnum3,shamnum4,shamnum5};
        pShams = new Label[]{sham1,sham2,sham3,sham4,sham5};
        pHuntnums = new Label[]{huntnum1,huntnum2,huntnum3,huntnum4,huntnum5};
        pHunts = new Label[]{hunt1,hunt2,hunt3,hunt4,hunt5};
        pInvlists = new HBox[]{invlist1,invlist2,invlist3,invlist4, invlist5};

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tribe.fxml"));
        loader.load();
        tribeController = loader.getController();

        loader = new FXMLLoader(getClass().getResource("/fxml/leaderboard.fxml"));
        loader.load();
        leaderboardController = loader.getController();

        errorLabel.setVisible(false);

        exitButton.setManaged(false);

        infoPreview.setVisible(false);
    }

    /**
     * Sets the view model for the game table.
     * <br/><strong>Pre:</strong> vBoard != null
     *
     * @param vBoard The view model to set.
     */
    public void setBoard(ViewModel vBoard) {
        this.vBoard = vBoard;
    }

    /**
     * Sets the virtual view used to send actions to the server.
     * <br/><strong>Pre:</strong> view != null
     *
     * @param view The virtual view to set.
     */
    public void setView(VirtualView view){
        this.vView = view;
    }

    /**
     * Updates all UI components to reflect the current game state.
     * <br/><strong>Pre:</strong> viewModel != null
     *
     * @param viewModel The updated view model.
     */
    public void refresh(ViewModel viewModel) {
        vBoard = viewModel;

        //setting upperRow
        for (int i = 0; i<9; i++){
            if (i>=vBoard.getUpperRow().size()){
                uppers.get(i).setVisible(false);
                uppers.get(i).setManaged(false);
            }
            else{
                uppers.get(i).setVisible(true);
                uppers.get(i).setManaged(true);
                Card card = vBoard.getUpperRow().get(i);
                ((ImageView) ((StackPane) uppers.get(i).getGraphic()).getChildren().get(0)).setImage(ImageLoader.loadArt(card));
                if (card.isPickable()) {
                    ((StackPane) uppers.get(i).getGraphic()).getChildren().get(1).setVisible(true);
                    ((ImageView) ((StackPane) uppers.get(i).getGraphic()).getChildren().get(1)).setImage(ImageLoader.loadFrame(card));
                } else
                    ((StackPane) uppers.get(i).getGraphic()).getChildren().get(1).setVisible(false);
            }
        }

        //setting lowerRow
        for (int i = 0; i<9; i++){
            if (i>=vBoard.getLowerRow().size()){
                lowers.get(i).setVisible(false);
                lowers.get(i).setManaged(false);
            }
            else{
                lowers.get(i).setVisible(true);
                lowers.get(i).setManaged(true);
                Card card = vBoard.getLowerRow().get(i);
                ((ImageView) ((StackPane) lowers.get(i).getGraphic()).getChildren().get(0)).setImage(ImageLoader.loadArt(card));
                if (card.isPickable()) {
                    ((StackPane) lowers.get(i).getGraphic()).getChildren().get(1).setVisible(true);
                    ((ImageView) ((StackPane) lowers.get(i).getGraphic()).getChildren().get(1)).setImage(ImageLoader.loadFrame(card));
                } else
                    ((StackPane) lowers.get(i).getGraphic()).getChildren().get(1).setVisible(false);
            }
        }

        //setting upperBuildings
        for (int i = 0; i<5; i++){
            if (i >=vBoard.getUpperBuildings().size()){
                upBs.get(i).setVisible(false);
                upBs.get(i).setManaged(false);
            }
            else {
                upBs.get(i).setVisible(true);
                upBs.get(i).setManaged(true);
                BuildingCard card = vBoard.getUpperBuildings().get(i);
                ((ImageView) ((StackPane) upBs.get(i).getGraphic()).getChildren().get(0)).setImage(ImageLoader.loadArt(card));
                ((StackPane) upBs.get(i).getGraphic()).getChildren().get(1).setVisible(false);
            }
        }

        //setting lowerBuildings
        for (int i = 0; i<5; i++){
            if (i >=vBoard.getLowerBuildings().size()){
                downBs.get(i).setVisible(false);
                downBs.get(i).setManaged(false);
            }
            else {
                downBs.get(i).setVisible(true);
                downBs.get(i).setManaged(true);
                BuildingCard card = vBoard.getLowerBuildings().get(i);
                ((ImageView) ((StackPane) downBs.get(i).getGraphic()).getChildren().get(0)).setImage(ImageLoader.loadArt(card));
                ((StackPane) downBs.get(i).getGraphic()).getChildren().get(1).setVisible(false);
            }
        }

        //setting tiles
        for (int i = 0; i<vBoard.getTiles().size(); i++){
            Optional<Player> p = vBoard.getTiles().get(i).getCurrentPlayer();
            if (p.isPresent() && colors.get(p.get())!=null){
                tiles.getChildren().get(i).setStyle("-fx-background-color: #" +colors.get(p.get()));
            } else
                tiles.getChildren().get(i).setStyle("");
        }

        //setting era
        if (!vBoard.getUpperRow().isEmpty())
            eraLabel.setText("ERA " +
                    (switch (Math.max(vBoard.getUpperRow().getLast().getEra(), vBoard.getUpperRow().get(vBoard.getUpperRow().size() - 2).getEra())) {
                        case 1 -> "I";
                        case 2 -> "II";
                        case 3 -> "III";
                        default -> "0";
                    }));

        //setting players
        for (int i =0; i < vBoard.getPlayers().size(); i++){
            Player p = vBoard.getPlayers().get(i);
            pPts[i].setText(""+p.getParameters().get(Parameter.PRESTIGE_POINTS));
            pFoods[i].setText(""+p.getParameters().get(Parameter.FOOD));
            pPeoples[i].setText(""+p.getTribe().values().stream().flatMap(List::stream).toList().size());
            pBuildnums[i].setText(p.getTribe().get(Parameter.BUILDER).size()+"x");
            pBuilds[i].setText(""+p.getParameters().get(Parameter.BUILDER));
            pGathnums[i].setText(p.getTribe().get(Parameter.GATHERER).size()+"x");
            pGaths[i].setText(""+p.getParameters().get(Parameter.GATHERER));
            pArtnums[i].setText(p.getTribe().get(Parameter.ARTIST).size()+"x");
            pArts[i].setText(""+p.getParameters().get(Parameter.ARTIST));
            pInvnums[i].setText(p.getTribe().get(Parameter.INVENTOR).size()+"x");
            pInvs[i].setText(""+p.getParameters().get(Parameter.INVENTOR));
            pShamnums[i].setText(p.getTribe().get(Parameter.SHAMAN).size()+"x");
            pShams[i].setText(""+p.getParameters().get(Parameter.SHAMAN));
            pHuntnums[i].setText(p.getTribe().get(Parameter.HUNTER).size()+"x");
            pHunts[i].setText(""+p.getParameters().get(Parameter.HUNTER));
            for (int bern = 1; bern <= 10; bern++){
                if (p.getInventions().contains(bern))
                    pInvlists[i].getChildren().get(bern-1).setOpacity(1);
                else
                    pInvlists[i].getChildren().get(bern-1).setOpacity(0.2);
            }
        }

        for (int i = 0; i<5; i++){
            if(i>=vBoard.getPlayers().size()){
                players[i].setVisible(false);
                players[i].setManaged(false);
            }
            else {
                players[i].setVisible(true);
                players[i].setManaged(true);
                pNames[i].setText(vBoard.getPlayers().get(i).getNickname().toUpperCase() +
                        (vBoard.getPlayers().get(i).getNickname().equals(nickname) ?
                                " (YOU)" : ""));
            }
        }

        colors = new HashMap<>();
        List<String> colorsToPickFrom = new ArrayList<>(List.of("F15C3E","00A1C1","FFCD28","410B2C","EDEDEC"));
        int i = 0;
        for(Player p : vBoard.getPlayers()){
            colors.put(p, colorsToPickFrom.get(i));
            i++;
        }
    }

    /**
     * Sets the local player's nickname.
     * <br/><strong>Pre:</strong> nickname != null
     *
     * @param nickname The player's nickname.
     */
    public void setName(String nickname){
        this.nickname = nickname;
    }

    /**
     * Updates the turn label to show which player must make which move.
     * <br/><strong>Pre:</strong> nickname != null
     * <br/><strong>Pre:</strong> move != null
     *
     * @param nickname The acting player's nickname.
     * @param move The move type required.
     */
    public void printMove(String nickname, Move move){
        Platform.runLater(() -> {
            if (nickname.equals(this.nickname)) {
                turnLabel.setText("YOUR TURN TO " + getMove(move, true) + "!");
            } else
                turnLabel.setText(nickname.toUpperCase() + " HAS TO " + getMove(move, false));
        });
    }

    private String getMove(Move move, boolean me){
        Platform.runLater(() -> {
            for (Button card : uppers){ card.setDisable(true); card.setOpacity(0.8);}
            for (Button card : lowers){ card.setDisable(true); card.setOpacity(0.8);}
            for (Button card : upBs){ card.setDisable(true); card.setOpacity(0.8);}
            for (Button card : downBs){ card.setDisable(true); card.setOpacity(0.8);}
            tiles.setDisable(true); tiles.setOpacity(0.9);
            switch (move){
                case Move.PICK_FROM_DOWN -> {
                    for (Button card : lowers){
                        if(lowers.indexOf(card)>=vBoard.getLowerRow().size() || vBoard.getLowerRow().get(lowers.indexOf(card)).isPickable())
                        card.setDisable(!me); else card.setDisable(false);
                        if(me) card.setOpacity(1);}
                    for (Button card : downBs){ card.setDisable(!me); if(me) card.setOpacity(1);}
                }
                case Move.PICK_FROM_UP -> {
                    for (Button card : uppers){
                        if(uppers.indexOf(card)>=vBoard.getUpperRow().size() ||vBoard.getUpperRow().get(uppers.indexOf(card)).isPickable())
                        card.setDisable(!me); else card.setDisable(false); if(me) card.setOpacity(1);}
                    for (Button card : upBs){ card.setDisable(!me); if(me) card.setOpacity(1);}
                }
                case Move.PICK_ANY_CARD ->  {
                    for (Button card : uppers){
                        if(uppers.indexOf(card)>=vBoard.getUpperRow().size() || vBoard.getUpperRow().get(uppers.indexOf(card)).isPickable())
                        card.setDisable(!me); else card.setDisable(false); if(me) card.setOpacity(1);}
                    for (Button card : lowers){
                        if(lowers.indexOf(card)>=vBoard.getLowerRow().size() || vBoard.getLowerRow().get(lowers.indexOf(card)).isPickable())
                        card.setDisable(!me); else card.setDisable(false); if(me) card.setOpacity(1);}
                    for (Button card : upBs){ card.setDisable(!me); if(me) card.setOpacity(1);}
                    for (Button card : downBs){ card.setDisable(!me); if(me) card.setOpacity(1);}
                }
                case Move.PICK_TILE ->  {
                    tiles.setDisable(!me); tiles.setOpacity(1);
                }
            }
        });
        return switch (move) {
            case Move.PICK_FROM_DOWN -> "PICK FROM DOWN";
            case Move.PICK_FROM_UP -> "PICK FROM UP";
            case Move.PICK_ANY_CARD -> "PICK ANY CARD";
            case Move.PICK_TILE -> "CHOOSE A TILE";
        };
    }

    /**
     * Configures tile and player visibility then sets the game table as the scene root.
     * <br/><strong>Pre:</strong> scene != null
     * <br/><strong>Pre:</strong> root != null
     *
     * @param scene The main scene.
     * @param root The game table root node.
     */
    public void createTable(Scene scene, Parent root){
        ViewModel tempBoard = vBoard;
        for(int i = 0; i<7; i++){
            //System.out.println(i + "/" + tempBoard.getTiles().size());
            if (i>=tempBoard.getTiles().size()) {
                tiles.getChildren().get(i).setVisible(false);
                tiles.getChildren().get(i).setManaged(false);
            }
            else {
                if (tempBoard.getTiles().get(i).getFood() == null) {
                    ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().getFirst().setVisible(false);
                    ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().getFirst().setManaged(false);
                }
                if (tempBoard.getTiles().get(i).getUpArrows() < 1) {
                    ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(1).setVisible(false);
                    ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(1).setManaged(false);
                }
                if (tempBoard.getTiles().get(i).getUpArrows() < 2) {
                    ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(2).setVisible(false);
                    ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(2).setManaged(false);
                }
                if (tempBoard.getTiles().get(i).getDownArrows() < 1) {
                    ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(3).setVisible(false);
                    ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(3).setManaged(false);
                }
                if (tempBoard.getTiles().get(i).getDownArrows() < 2) {
                    ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(4).setVisible(false);
                    ((HBox) ((Button) tiles.getChildren().get(i)).getGraphic()).getChildren().get(4).setManaged(false);
                }
            }
        }
        for (int i = 0; i<5; i++){
            if(i>=tempBoard.getPlayers().size()){
                players[i].setVisible(false);
                players[i].setManaged(false);
            }
            else {
                pNames[i].setText(tempBoard.getPlayers().get(i).getNickname() +
                        (tempBoard.getPlayers().get(i).getNickname().equals(nickname) ?
                                " (YOU)" : ""));
            }
        }
        scene.setRoot(root);
    }

    /** Handles click on upper character card slot 1. */
    @FXML public void up1() {upcCard(1);}
    /** Handles click on upper character card slot 2. */
    @FXML public void up2() {upcCard(2);}
    /** Handles click on upper character card slot 3. */
    @FXML public void up3() {upcCard(3);}
    /** Handles click on upper character card slot 4. */
    @FXML public void up4() {upcCard(4);}
    /** Handles click on upper character card slot 5. */
    @FXML public void up5() {upcCard(5);}
    /** Handles click on upper character card slot 6. */
    @FXML public void up6() {upcCard(6);}
    /** Handles click on upper character card slot 7. */
    @FXML public void up7() {upcCard(7);}
    /** Handles click on upper character card slot 8. */
    @FXML public void up8() {upcCard(8);}
    /** Handles click on upper character card slot 9. */
    @FXML public void up9() {upcCard(9);}
    /** Handles click on lower character card slot 1. */
    @FXML public void down1() {downcCard(1);}
    /** Handles click on lower character card slot 2. */
    @FXML public void down2() {downcCard(2);}
    /** Handles click on lower character card slot 3. */
    @FXML public void down3() {downcCard(3);}
    /** Handles click on lower character card slot 4. */
    @FXML public void down4() {downcCard(4);}
    /** Handles click on lower character card slot 5. */
    @FXML public void down5() {downcCard(5);}
    /** Handles click on lower character card slot 6. */
    @FXML public void down6() {downcCard(6);}
    /** Handles click on lower character card slot 7. */
    @FXML public void down7() {downcCard(7);}
    /** Handles click on lower character card slot 8. */
    @FXML public void down8() {downcCard(8);}
    /** Handles click on lower character card slot 9. */
    @FXML public void down9() {downcCard(9);}
    /** Handles click on upper building card slot 1. */
    @FXML public void upb1() {upbCard(1);}
    /** Handles click on upper building card slot 2. */
    @FXML public void upb2() {upbCard(2);}
    /** Handles click on upper building card slot 3. */
    @FXML public void upb3() {upbCard(3);}
    /** Handles click on upper building card slot 4. */
    @FXML public void upb4() {upbCard(4);}
    /** Handles click on upper building card slot 5. */
    @FXML public void upb5() {upbCard(5);}
    /** Handles click on lower building card slot 1. */
    @FXML public void downb1() {downbCard(1);}
    /** Handles click on lower building card slot 2. */
    @FXML public void downb2() {downbCard(2);}
    /** Handles click on lower building card slot 3. */
    @FXML public void downb3() {downbCard(3);}
    /** Handles click on lower building card slot 4. */
    @FXML public void downb4() {downbCard(4);}
    /** Handles click on lower building card slot 5. */
    @FXML public void downb5() {downbCard(5);}
    /** Handles click on tile slot 1. */
    @FXML public void til1() {tile(1);}
    /** Handles click on tile slot 2. */
    @FXML public void til2() {tile(2);}
    /** Handles click on tile slot 3. */
    @FXML public void til3() {tile(3);}
    /** Handles click on tile slot 4. */
    @FXML public void til4() {tile(4);}
    /** Handles click on tile slot 5. */
    @FXML public void til5() {tile(5);}
    /** Handles click on tile slot 6. */
    @FXML public void til6() {tile(6);}
    /** Handles click on tile slot 7. */
    @FXML public void til7() {tile(7);}

    private void upcCard(int i){
        Platform.runLater(() -> {
            errorLabel.setVisible(false);
            try {
                vView.checkCharacterCard((CharacterCard) vBoard.getUpperRow().get(i - 1));
            } catch (IOException ex) {
                String old = eraLabel.getText();
                eraLabel.setText(old + " (RETRY)");
            }
        });
    }

    private void downcCard(int i){
        Platform.runLater(() -> {
            errorLabel.setVisible(false);
            try {
                vView.checkCharacterCard((CharacterCard) vBoard.getLowerRow().get(i-1));
            } catch (IOException ex) {
                String old = eraLabel.getText();
                eraLabel.setText(old+ " (RETRY)");
            }
        });
    }

    private void upbCard(int i){
        Platform.runLater(() -> {
            errorLabel.setVisible(false);
            try {
                vView.checkBuildingCard(vBoard.getUpperBuildings().get(i - 1));
            } catch (IOException ex) {
                String old = eraLabel.getText();
                eraLabel.setText(old + " (RETRY)");
            }
        });
    }

    private void downbCard(int i){
        Platform.runLater(() -> {
            errorLabel.setVisible(false);
            try {
                vView.checkBuildingCard(vBoard.getLowerBuildings().get(i-1));
            } catch (IOException ex) {
                String old = eraLabel.getText();
                eraLabel.setText(old+ " (RETRY)");
            }
        });
    }

    private void tile(int i){
        Platform.runLater(() -> {
            errorLabel.setVisible(false);
            try {
                vView.checkTile(vBoard.getTiles().get(i-1));
            } catch (IOException ex) {
                String old = eraLabel.getText();
                eraLabel.setText(old+ " (RETRY)");
            }
        });
    }

    /** Shows the tribe detail for player 1. */
    @FXML
    public void showTribe1() { showTribe(0);}

    /** Shows the tribe detail for player 2. */
    @FXML
    public void showTribe2() { showTribe(1);}

    /** Shows the tribe detail for player 3. */
    @FXML
    public void showTribe3() { showTribe(2);}

    /** Shows the tribe detail for player 4. */
    @FXML
    public void showTribe4() { showTribe(3);}

    /** Shows the tribe detail for player 5. */
    @FXML
    public void showTribe5() { showTribe(4);}

    private void showTribe(int i) {tribeController.show(vBoard.getPlayers().get(i), i);}

    /**
     * Shows the end-game overlay with final standings and enables the exit button.
     */
    public void printEnd(){
        Platform.runLater(() -> {
            rows.setVisible(false);
            List<Player> finalPlayerList = vBoard.getPlayers().stream().
                    sorted(Comparator.comparing(p -> p.getParameters().get(Parameter.PRESTIGE_POINTS))).toList();
            for(int i = 0; i < vBoard.getPlayers().size(); i++){
                playersRow.getChildren().get(i).setStyle("-fx-translate-y: " + (-100-50*finalPlayerList.indexOf(vBoard.getPlayers().get(i))) + "; -fx-background-color: #FFFFFF; -fx-background-radius: 20; -fx-border-color: #F15C3E; -fx-border-radius: 18; -fx-border-width: 2;");
            }
            eraLabel.setText("GAME ENDED");
            if (finalPlayerList.getLast().getParameters().get(Parameter.PRESTIGE_POINTS).equals(
                    finalPlayerList.get(finalPlayerList.size()-2).getParameters().get(Parameter.PRESTIGE_POINTS))){
                turnLabel.setText("IT'S A TIE!");
            } else
                turnLabel.setText(finalPlayerList.getLast().getNickname().toUpperCase() + " WON!");
            exitButton.setManaged(true);
            exitButton.setVisible(true);
            exitButton.setDisable(false);
        });
    }

    /** Exits the application. */
    @FXML
    public void exit(){
        Platform.exit();
    }

    /**
     * Shows an error message based on the given error type.
     * <br/><strong>Pre:</strong> errorType != null
     *
     * @param errorType The type of error to display.
     */
    public void printError(ErrorType errorType){
        Platform.runLater(()->{
            errorLabel.setVisible(true);
            switch (errorType) {
                case NOT_YOUR_TURN -> errorLabel.setText("WAIT FOR YOUR TURN!");
                case WRONG_TILE -> errorLabel.setText("CANNOT PICK THIS TILE!");
                case WRONG_CARD -> errorLabel.setText("CANNOT DRAW THIS CARD!");
                case NOT_ENOUGH_FOOD -> errorLabel.setText("YOU DON'T HAVE ENOUGH FOOD!");
                case END_FOR_DISCONNECTION -> errorLabel.setText("A PLAYER DISCONNECTED!");
                case CONNECTION_CRASHED -> errorLabel.setText("CONNECTION LOST!");
            }
        });
    }
    
    /** Shows card preview for upper character card slot 1 on right-click. */
    @FXML   void rightUp1(){ rightClick(vBoard.getUpperRow().getFirst()); }
    /** Shows card preview for upper character card slot 2 on right-click. */
    @FXML   void rightUp2(){ rightClick(vBoard.getUpperRow().get(1)); }
    /** Shows card preview for upper character card slot 3 on right-click. */
    @FXML   void rightUp3(){ rightClick(vBoard.getUpperRow().get(2)); }
    /** Shows card preview for upper character card slot 4 on right-click. */
    @FXML   void rightUp4(){ rightClick(vBoard.getUpperRow().get(3)); }
    /** Shows card preview for upper character card slot 5 on right-click. */
    @FXML   void rightUp5(){ rightClick(vBoard.getUpperRow().get(4)); }
    /** Shows card preview for upper character card slot 6 on right-click. */
    @FXML   void rightUp6(){ rightClick(vBoard.getUpperRow().get(5)); }
    /** Shows card preview for upper character card slot 7 on right-click. */
    @FXML   void rightUp7(){ rightClick(vBoard.getUpperRow().get(6)); }
    /** Shows card preview for upper character card slot 8 on right-click. */
    @FXML   void rightUp8(){ rightClick(vBoard.getUpperRow().get(7)); }
    /** Shows card preview for upper character card slot 9 on right-click. */
    @FXML   void rightUp9(){ rightClick(vBoard.getUpperRow().get(8)); }
    /** Shows card preview for upper building slot 1 on right-click. */
    @FXML   void rightUpB1(){ rightClickB(vBoard.getUpperBuildings().getFirst()); }
    /** Shows card preview for upper building slot 2 on right-click. */
    @FXML   void rightUpB2(){ rightClickB(vBoard.getUpperBuildings().get(1)); }
    /** Shows card preview for upper building slot 3 on right-click. */
    @FXML   void rightUpB3(){ rightClickB(vBoard.getUpperBuildings().get(2)); }
    /** Shows card preview for upper building slot 4 on right-click. */
    @FXML   void rightUpB4(){ rightClickB(vBoard.getUpperBuildings().get(3)); }
    /** Shows card preview for upper building slot 5 on right-click. */
    @FXML   void rightUpB5(){ rightClickB(vBoard.getUpperBuildings().get(4)); }
    /** Shows card preview for lower character card slot 1 on right-click. */
    @FXML   void rightDown1() { rightClick(vBoard.getLowerRow().getFirst()); }
    /** Shows card preview for lower character card slot 2 on right-click. */
    @FXML   void rightDown2() { rightClick(vBoard.getLowerRow().get(1)); }
    /** Shows card preview for lower character card slot 3 on right-click. */
    @FXML   void rightDown3() { rightClick(vBoard.getLowerRow().get(2)); }
    /** Shows card preview for lower character card slot 4 on right-click. */
    @FXML   void rightDown4() { rightClick(vBoard.getLowerRow().get(3)); }
    /** Shows card preview for lower character card slot 5 on right-click. */
    @FXML   void rightDown5() { rightClick(vBoard.getLowerRow().get(4)); }
    /** Shows card preview for lower character card slot 6 on right-click. */
    @FXML   void rightDown6() { rightClick(vBoard.getLowerRow().get(5)); }
    /** Shows card preview for lower character card slot 7 on right-click. */
    @FXML   void rightDown7() { rightClick(vBoard.getLowerRow().get(6)); }
    /** Shows card preview for lower character card slot 8 on right-click. */
    @FXML   void rightDown8() { rightClick(vBoard.getLowerRow().get(7)); }
    /** Shows card preview for lower character card slot 9 on right-click. */
    @FXML   void rightDown9() { rightClick(vBoard.getLowerRow().get(8)); }
    /** Shows card preview for lower building slot 1 on right-click. */
    @FXML   void rightDownB1() { rightClickB(vBoard.getLowerBuildings().getFirst()); }
    /** Shows card preview for lower building slot 2 on right-click. */
    @FXML   void rightDownB2() { rightClickB(vBoard.getLowerBuildings().get(1)); }
    /** Shows card preview for lower building slot 3 on right-click. */
    @FXML   void rightDownB3() { rightClickB(vBoard.getLowerBuildings().get(2)); }
    /** Shows card preview for lower building slot 4 on right-click. */
    @FXML   void rightDownB4() { rightClickB(vBoard.getLowerBuildings().get(3)); }
    /** Shows card preview for lower building slot 5 on right-click. */
    @FXML   void rightDownB5() { rightClickB(vBoard.getLowerBuildings().get(4)); }

    private void rightClick (Card card){
        ((ImageView) infoPreview.getChildren().get(0)).setImage(ImageLoader.loadArt(card));
        if (card.isPickable()) ((ImageView) infoPreview.getChildren().get(1)).setImage(ImageLoader.loadFrame(card));
                else infoPreview.getChildren().get(1).setVisible(false);
        infoLabel.setText(card.getCardInfo(new StringBuilder()));
        infoBox.setVisible(true);
    }
    private void rightClickB (BuildingCard card){
        ((ImageView) infoPreview.getChildren().get(0)).setImage(ImageLoader.loadArt(card));
        infoPreview.getChildren().get(1).setVisible(false);
        infoLabel.setText(card.getCardInfo(new StringBuilder()));
        infoBox.setVisible(true);
    }

    /** Hides the card info preview panel. */
    @FXML   void closeInfo(){
        infoBox.setVisible(false);
    }

    /**
     * Shows the leaderboard button and triggers the end-game overlay.
     */
    public void askShowRankings(){
        leaderboardButton.setVisible(true);
        leaderboardButton.setDisable(false);
        leaderboardButton.setManaged(true);
        printEnd();
    }

    /** Requests and displays the leaderboard; uses cached rankings if available. */
    @FXML
    public void leaderboard(){
        if (cachedPlayerRank != null && cachedGlobalRankings != null) {
            leaderboardController.showRankings(cachedPlayerRank, cachedGlobalRankings);
            return;
        }
        try {
            vView.answerShowRankings(true);
        } catch (Exception e) {
            errorLabel.setText("ERROR CONNECTING TO SERVER!");
        }
    }

    /**
     * Caches ranking data and delegates display to LeaderboardGui.
     * <br/><strong>Pre:</strong> playerRank != null
     * <br/><strong>Pre:</strong> globalRankings != null
     *
     * @param playerRank The current player's rank entry.
     * @param globalRankings All players' rankings in order.
     */
    public void showRankings(Map<String, String> playerRank, List<Map<String, String>> globalRankings) {
        cachedPlayerRank = playerRank;
        cachedGlobalRankings = globalRankings;
        leaderboardController.showRankings(playerRank, globalRankings);
    }
}
