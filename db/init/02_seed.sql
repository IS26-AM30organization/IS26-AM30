-- Two 2-player games and one 3-player game, useful for testing queries
INSERT INTO GAMES (PlayersNumber) VALUES (2), (2), (3);

INSERT INTO RESULTS (GameID, Nickname, Score) VALUES
    (1, 'Alice',   150),
    (1, 'Bob',     90),
    (2, 'Alice',   200),
    (2, 'Charlie', 210),
    (3, 'Alice',   300),
    (3, 'Bob',     280),
    (3, 'Charlie', 310);