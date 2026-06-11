package mesos.am30.common;

/**
 * Enumeration for the Game Phases.
 * <br/>This enumeration is used by the TUI in order to identify a given Game Phase (and so the valid commands).
 */
public enum GamePhase {

    /// Menu for connecting to / creating a Lobby.
    MENU,

    /// Waiting for a valid nickname.
    LOBBY,

    /// Playing the Game.
    GAME,

    /// End screen and DB interaction.
    END_SCREEN,

    /// End of the Game.
    END
}
