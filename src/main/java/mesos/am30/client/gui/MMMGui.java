package mesos.am30.client.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import mesos.am30.client.VirtualView;

import java.io.IOException;
import java.util.Map;

public class MMMGui {
    String nickname;
    VirtualView vView;

    @FXML    ScrollPane existingLobbies;
    @FXML    HBox newLobby;
    @FXML    Button playButton;
    @FXML    Label errorLabel;
    @FXML    VBox lobbyList;
    @FXML    TextField newLobbyCode;
    @FXML    ChoiceBox<Integer> newLobbyPlayers;
    @FXML    HBox nicknameBox;
    @FXML    TextField name;

    public void setView (VirtualView vView){
        this.vView = vView;
    }

    @FXML
    public void initialize(){
        existingLobbies.setVisible(false);
        newLobby.setVisible(false);
        existingLobbies.setManaged(false);
        newLobby.setManaged(false);
        newLobbyPlayers.getItems().addAll(2,3,4,5);
        nicknameBox.setVisible(false);
        nicknameBox.setManaged(false);
    }

    @FXML
    public void play(){
        existingLobbies.setVisible(true);
        newLobby.setVisible(true);
        existingLobbies.setManaged(true);
        newLobby.setManaged(true);
        playButton.setDisable(true);
        playButton.setVisible(false);
        playButton.setManaged(false);
        refresh();
    }

    @FXML
    public void refresh(){
        try {
            vView.requestAvailableLobbies();
            Platform.runLater(()->{
                errorLabel.setText("LOADING...");
                errorLabel.setVisible(true);
            });
        } catch (IOException e) {
            Platform.runLater(()->{
            errorLabel.setText("CANNOT RELOAD! CHECK YOUR CONNECTION AND TRY AGAIN");
            errorLabel.setVisible(true);
            });
        }
    }

    public void showLobbies(Map<String, Integer> availableLobbies) {
        Platform.runLater(()->{
            errorLabel.setVisible(false);
            lobbyList.getChildren().clear();
            String cssPath = getClass().getResource("/css/style.css").toExternalForm();
            for(Map.Entry<String, Integer> entry : availableLobbies.entrySet()) {
                Button button = new Button(entry.getKey() + " (" + entry.getValue() + " players)");
                button.setPrefHeight(50.0);
                button.setPrefWidth(300.0);
                button.getStyleClass().add("lobby_button");
                button.getStylesheets().add(cssPath);
                button.setOnAction(event -> {
                    joinLobby(entry.getKey());
                });
                lobbyList.getChildren().add(button);
            }
            });
    }

    private void joinLobby(String lobbyName){
        try {
            vView.joinLobby(lobbyName);
        } catch (IOException e) {
            Platform.runLater(()->{
                errorLabel.setText("CANNOT JOIN! CHECK YOUR CONNECTION AND TRY AGAIN");
                errorLabel.setVisible(true);
            });
        }
    }

    @FXML
    public void createLobby(){
        Platform.runLater(()->{
            if(newLobbyCode.getText().isBlank() || newLobbyPlayers.getValue() == null){
                errorLabel.setText("WRITE A LOBBY CODE AND SELECT THE NUMBER OF PLAYERS");
                errorLabel.setVisible(true);
            } else {
                try {
                    vView.createLobby(newLobbyPlayers.getValue(), newLobbyCode.getText());
                    errorLabel.setText("JOINING...");
                    errorLabel.setVisible(true);
                } catch (IOException e) {
                    errorLabel.setText("CHECK YOUR CONNECTION!");
                    errorLabel.setVisible(true);
                }
            }
        });
    }

    public void askNickname(){
        Platform.runLater(()->{
            existingLobbies.setVisible(false);
            newLobby.setVisible(false);
            existingLobbies.setManaged(false);
            newLobby.setManaged(false);
            nicknameBox.setVisible(true);
            nicknameBox.setManaged(true);
        });
    }

    @FXML
    public void answerNickname(){
        Platform.runLater(()->{
            try {
                nickname = name.getText();
                vView.answerNickname(nickname);

            } catch (IOException e) {
                errorLabel.setText("CHECK YOUR CONNECTION!");
                errorLabel.setVisible(true);
            }
        });
    }

    public String getNickname(){
        return nickname;
    }
}
