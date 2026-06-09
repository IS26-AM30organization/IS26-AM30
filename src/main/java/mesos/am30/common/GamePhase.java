package mesos.am30.common;

/**
 * This class defines which are the allowed game phases within a game.
 */
public enum GamePhase {
    MENU,               //to connect or create a lobby
    LOBBY,              //inserting a valid name
    GAME,               //match phase
    END_SCREEN,         //database interaction
    END                 //game is ended
}
