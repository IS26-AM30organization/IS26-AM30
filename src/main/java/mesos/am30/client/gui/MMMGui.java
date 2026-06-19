package mesos.am30.client.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import mesos.am30.client.ClientMain;
import mesos.am30.client.view.VirtualView;
import mesos.am30.common.enumerations.ErrorType;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * Controller for the multi-match main menu scene.
 * <br/>Handles server connection, lobby creation and joining, and nickname submission.
 */
public class MMMGui {
    String nickname;
    VirtualView vView;
    int port;

    @FXML    ScrollPane existingLobbies;
    @FXML    HBox newLobby;
    @FXML    Button playButton;
    @FXML    Label errorLabel;
    @FXML    VBox lobbyList;
    @FXML    TextField newLobbyCode;
    @FXML    ChoiceBox<Integer> newLobbyPlayers;
    @FXML    HBox nicknameBox;
    @FXML    TextField name;
    @FXML    AnchorPane loading;
    @FXML    Label connectedText;
    @FXML    Label title;

    /**
     * Sets the virtual view used to send actions to the server.
     * <br/><strong>Pre:</strong> vView != null
     *
     * @param vView The virtual view to set.
     */
    public void setView (VirtualView vView){
        this.vView = vView;
    }

    /**
     * Sets the server port to connect to.
     *
     * @param port The server port number.
     */
    public void setPort (int port){
        this.port = port;
    }

    /**
     * Initializes the scene by hiding lobby panels and nickname input until connection is established.
     */
    @FXML
    public void initialize(){
        existingLobbies.setVisible(false);
        newLobby.setVisible(false);
        existingLobbies.setManaged(false);
        newLobby.setManaged(false);
        newLobbyPlayers.getItems().addAll(2,3,4,5);
        nicknameBox.setVisible(false);
        nicknameBox.setManaged(false);
        connectedText.setVisible(false);
        connectedText.setManaged(false);
    }

    /**
     * Shows a connecting message and delegates the server connection to the virtual view.
     */
    @FXML
    public void play(){
        errorLabel.setText("CONNECTING...");
        errorLabel.setVisible(true);
        try {
            vView.findServer(ClientMain.getIP(), port);
        } catch (IOException exception) {
            Platform.runLater(()-> errorLabel.setText("CHECK YOUR CONNECTION!"));
        }
    }

    /**
     * Shows lobby options after a successful server connection.
     */
    public void confirmConnection() {
        Platform.runLater(()->{
            errorLabel.setVisible(false);
            existingLobbies.setVisible(true);
            newLobby.setVisible(true);
            existingLobbies.setManaged(true);
            newLobby.setManaged(true);
            playButton.setDisable(true);
            playButton.setVisible(false);
            playButton.setManaged(false);
            refresh();
        });
    }

    /**
     * Updates the UI after the player has successfully joined a lobby.
     */
    public void confirmLobbyJoined() {
        Platform.runLater(() ->{
            errorLabel.setVisible(false);
            connectedText.setVisible(true);
            connectedText.setManaged(true);
            title.setText(vView.getLobbyCode());
            nicknameBox.setVisible(false);
            nicknameBox.setManaged(false);
        });
    }

    /**
     * Shows a loading message and requests the available lobby list from the server.
     */
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

    /**
     * Populates the lobby list with the available lobbies.
     * <br/><strong>Pre:</strong> availableLobbies != null
     *
     * @param availableLobbies Map of lobby codes to their current player count.
     */
    public void showLobbies(Map<String, Integer> availableLobbies) {
        Platform.runLater(()->{
            errorLabel.setVisible(false);
            lobbyList.getChildren().clear();
            String cssPath = Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm();
            for(Map.Entry<String, Integer> entry : availableLobbies.entrySet()) {
                Button button = new Button(entry.getKey() + " (" + entry.getValue() + " players)");
                button.setPrefHeight(50.0);
                button.setPrefWidth(400.0);
                button.getStyleClass().add("lobby_button");
                button.getStylesheets().add(cssPath);
                button.setOnAction(_ -> joinLobby(entry.getKey()));
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

    /**
     * Validates the lobby code and player count fields, then delegates lobby creation to the virtual view.
     */
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

    /**
     * Shows the nickname input field after joining a lobby.
     */
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

    /**
     * Reads the nickname field and delegates the nickname submission to the virtual view.
     */
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

    /**
     * Shows the loading overlay while waiting for the game to start.
     */
    public void loading(){
        loading.setVisible(true);
    }

    /**
     * Returns the nickname entered by the player.
     *
     * @return The player's nickname.
     */
    public String getNickname(){
        return nickname;
    }

    /**
     * Shows an error message based on the given error type.
     * <br/><strong>Pre:</strong> errorType != null
     *
     * @param errorType The type of error to display.
     */
    public void printError(ErrorType errorType) {
        Platform.runLater(()->{
            errorLabel.setVisible(true);
            switch (errorType) {
                case WRONG_IP -> errorLabel.setText("CANNOT FIND A SERVER FOR THIS IP!");
                case ALREADY_EXISTING_LOBBY -> errorLabel.setText("THERE'S ALREADY A LOBBY WITH THIS CODE!");
                case NOT_EXISTING_LOBBY -> errorLabel.setText("COULN'T FIND THAT LOBBY! TRY REALOADING");
                case WRONG_PLAYERS_NUMBER -> errorLabel.setText("CHOOSE BETWEEN 2 AND 5 PLAYERS!");
                case INVALID_LOBBY_CODE -> errorLabel.setText("INVALID LOBBY CODE!");
                case WRONG_NICKNAME -> errorLabel.setText("CHOOSE ANOTHER NICKNAME FOR THIS MATCH!");
                case FULL_LOBBY -> errorLabel.setText("THE CHOSEN LOBBY IS ALREADY FULL!");
            }
        });
    }
}