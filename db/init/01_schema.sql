# Games Table creation
CREATE TABLE IF NOT EXISTS GAMES (
    # attributes
    GameID          int UNSIGNED    NOT NULL AUTO_INCREMENT,
    PlayedAt        datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PlayersNumber   tinyint         NOT NULL CHECK (PlayersNumber >= 2 AND PlayersNumber <= 5),

    # constraints
    CONSTRAINT pk_GAMES PRIMARY KEY (GameID)
);

# Game Results Table creation
CREATE TABLE IF NOT EXISTS RESULTS (
    # attributes
    GameID      int UNSIGNED    NOT NULL,
    Nickname    varchar(64)     NOT NULL,
    Score       int             NOT NULL,

    # constraints
    CONSTRAINT pk_RESULTS PRIMARY KEY (GameID, Nickname),
    CONSTRAINT FK_RESULTS_GAMES FOREIGN KEY (GameID) REFERENCES GAMES(GameID) ON DELETE CASCADE
);