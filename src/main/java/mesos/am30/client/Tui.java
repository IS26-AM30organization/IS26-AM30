package mesos.am30.client;

import mesos.am30.common.*;
import mesos.am30.gameModel.*;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Card;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.Tile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Integer.parseInt;
import static mesos.am30.common.GamePhase.*;

/**
 * This class represents the Terminal Interface.
 * As the connection to the server is acknowledged, it runs a thread that reads the player's inputs.
 * Based on gPhase value (representing the game's phase), only certain commands are allowed.
 */
public class Tui implements IF_GameUI {
	ViewModel vBoard;
	VirtualView vView;
	private int[] tileBoost;

	private final ExecutorService clientExecutor = Executors.newFixedThreadPool(1);
	volatile GamePhase gPhase;
	final Object tuiLock = new Object();
	volatile boolean isMatchRunning = false;

	Scanner actionScanner = new Scanner(System.in);

	public Tui() {
		gPhase = GamePhase.MENU;
	}

	public void setvModel(ViewModel vBoard){
		this.vBoard = vBoard;
	}

	/**
	 * Starts executor service for terminal reading
	 */
	public void startCLient() {
		clientExecutor.submit(this::plInputReader);
	}

	/**
	 * Allows execution of available commands based on gamePhase
	 */
    public void plInputReader() {
        String action = "";

        while (gPhase != END) {
            String plInput = actionScanner.nextLine();
            String[] plAction = plInput.toLowerCase().split("\\s+"); //input is divided in words.

            switch (gPhase) {
                case MENU -> menuCMDs(plAction); //MENU Phase: connecting to a lobby
                case LOBBY -> { //LOBBY Phase: inserting a valid name
                    try {
                        if (!plAction[0].isBlank()) vView.answerNickname(plAction[0]);
						else askNickname();
                    } catch (IOException e) {
                        printMessage("[ERROR]: error on transmitting player's Nickname.");
                    }
                }
				case GAME -> matchCMDs(plAction); //GAME Phase: game phase
				case END_SCREEN -> endScreenCMDs(plAction); //END_SCREEN Phase: db request
                default -> {}
            }
        }
    }

	/**
	 * Invoked by virtualView to prompt the player to insert its name
	 */
    public void askNickname() throws IOException {
            gPhase = GamePhase.LOBBY;
            printMessage("Insert nickname: ");
    }

	/**
	 * Invoked by virtualView, not used by Tui.
	 */
	public void refresh(ViewModel vBoard) {
	}

	/**
	 * Shows on TUI actions made by any player
	 * On first call it starts the terminal executor.
	 */
	public void printMove(String nickname, Move move) {
		boardRender();
		printSysMessage(TColors.CYAN_B + "[System]: Player " + TColors.YELLOW + nickname + TColors.RESET + " has " + move + TColors.RESET);

		if (isMatchRunning) return;
		isMatchRunning = true;
	}

	/**
	 * Asks player whether it wants to see the Leaderboard or not
	 */
	public void askShowRankings() {
		displayScoreboard();

		gPhase = END_SCREEN;
		printMessage(TColors.GREEN_B + "Would you like to see/reload the Leaderboard? [y/n]: " + TColors.RESET);
	}

	/**
	 * Displays Leaderboard after inspecting the DB
	 */
	public void showRankings(Map<String,String> playerMap, List<Map<String, String>> ranking) {
		if (ranking == null || ranking.isEmpty()) {
			printSysMessage("[System]: no leaderboard to display for this game settings\nWould you like to see/reload the Leaderboard? [y/n]: ");
			return;
		}

		System.out.println("--- GLOBAL RANKING ---");
		for (Map<String, String> row : ranking) {
			String rank = row.get("RANK");
			String nickname = row.get("Nickname");
			String score = row.get("Score");

			System.out.println(rank + "° | Player: " + nickname + " | Points: " + score);
		}

		System.out.println("----------------------");

		if (playerMap != null && !playerMap.isEmpty()) {
			String playerRank = playerMap.get("RANK");
			String playerNick = playerMap.get("Nickname");
			String playerScore = playerMap.get("Score");

			System.out.println("Your Rank: " + playerRank + "° | Player: " + playerNick + " | Points: " + playerScore + "\n");
		} else {
			System.out.println("You are not in the ranking for this game mode yet.\n");
		}
	}

	/**
	 * Handles end-game screen
	 */
	public void printEnd() {
		gPhase = END;
		displayScoreboard();

		endGame();
	}

	@Override
	public void setvView(VirtualView view) {
		this.vView=view;
	}

	/**
	 * Shows on TUI any errors sent by the server/view model for incorrect player's actions
	 * if the wrong nickname was inserted, client gets prompted again
	 */
    public void printError(ErrorType errorMessage) {
        printMessage(TColors.RED_B + "[Error]: " + errorMessage + TColors.RESET);

        switch (errorMessage) {
            case WRONG_PLAYERS_NUMBER -> printMessage("Type: create #plNum, to create a lobby.");
            case WRONG_NICKNAME -> {
                try {
                    askNickname();
                } catch (IOException e) {
                    printMessage("[Error]: error on setting player's nickname");
                }
            }
        }
    }

	/**
	 * Shows available lobbies
	 * @param availableLobbies map of lobby codes to their current number of players
	 */
	@Override
	public void showLobbies(Map<String, Integer> availableLobbies) {
		if (availableLobbies.isEmpty()) {
			printMessage("[ERROR]: No available lobbies.");
			return;
		}
		StringBuilder sb = new StringBuilder("[System]: Available lobbies:\n");
		availableLobbies.forEach((code, players) ->
				sb.append("  Code: ").append(code).append(" | Players: ").append(players).append("\n"));
		printSysMessage(sb.toString());
	}

	/**
	 * Received to acknowledge connection to the server
	 */
	@Override
	public void confirmConnection() {
		printSysMessage(TColors.GREEN_B + "[System]: Connected! Type -h or -help to show available commands at anytime." + TColors.RESET);
		gPhase = GamePhase.MENU;
		startCLient();
	}

	/**
	 * Received to acknowledge connection to lobby
	 */
	@Override
	public void confirmLobbyJoined() {
		printSysMessage("[System]: Lobby joined! \n[System]: Waiting for other players... \nOnce the match starts, type -h or -help to show all available commands.");
		gPhase = GamePhase.GAME;
	}

	//PRIVATE LOGIC METHODS

	/**
	 * Returns the wanted Tile, if existing
	 * @throws IndexOutOfBoundsException if number inserted is incorrect
	 */
	private Tile wantedTile(int tileIndex) throws IndexOutOfBoundsException {
		return vBoard.getTiles().get(tileIndex);
	}

	/**
	 * @param card any Card from deck (not Building)
	 * @return wanted Character Card
	 */
	private CharacterCard wantedCCard(Card card) {
		if (!card.isPickable())
			throw new IndexOutOfBoundsException("Card is not a Character.");
		return (CharacterCard) card;
	}

	/**
	 * Returns the wanted Card, if existing
	 * @throws IOException if move is unknown
	 */
	private Card wantedCard(Move move,int cIndex) throws IOException {
		switch (move) {
			case PICK_FROM_UP -> {
				return vBoard.getUpperRow().get(cIndex);
			}
			case PICK_FROM_DOWN -> {
				return vBoard.getLowerRow().get(cIndex);
			}
			default -> {
				 throw new IOException("Unknown Move");
			}
		}
	}

	/**
	 * Returns the wanted BuildingCard, if existing
	 * @param move player's move,
	 * @param cIndex card position
	 * @throws IndexOutOfBoundsException if number inserted is incorrect
	 */
	private BuildingCard wantedBuild(Move move, int cIndex) throws IndexOutOfBoundsException, IOException {
		switch (move) {
			case PICK_FROM_UP -> {
				return vBoard.getUpperBuildings().get(cIndex);
			}
			case PICK_FROM_DOWN -> {
				return vBoard.getLowerBuildings().get(cIndex);
			}
			default -> {
				throw new IOException("Unknown Move");
			}
		}
	}
    /**
     * Defines which are the correct commands for the ongoing game phase: MENU
     * @param plAction contains terminal player's input
     */
    private void menuCMDs(String[] plAction) {
        String action = " ";
        int numOne = -1;
        String numTwo = "";

        action = plAction[0];
        int cmdSize = plAction.length;

        if (cmdSize <= 3) {
            try {
                switch (cmdSize) {
                    case 1 -> {}
                    case 2 -> {
                        numTwo = plAction[1];
                        if (Objects.equals(action, "create")) {
                            numOne = parseInt(plAction[1]);
                            numTwo = vView.getLobbyCode();
                        }
                    }
                    case 3 -> {
                        numOne = parseInt(plAction[1]);
                        numTwo = plAction[2];
                    }
                    default -> printMessage("Invalid Command, type -h or -help to show all available commands.");
                }

                switch (action) {
                    case "-h", "-help" -> showMenuCommands();
                    case "join" -> vView.joinLobby(numTwo); //join(lobbyCode);
                    case "create" -> vView.createLobby(numOne, numTwo); //(numPlayers, lobbyCode);
                    case "list" -> {
                        try {
                            vView.requestAvailableLobbies();
                        } catch (IOException e) {
                            printMessage("[ERROR]: Cannot reach server.");
                        }
                    }
                    default -> printMessage("Invalid command. type -h or -help to show all available commands.");
                }
            } catch(NumberFormatException | IndexOutOfBoundsException e){
                printMessage("Invalid number: outOfBound/WrongRow/Event was picked");
            } catch(IOException e){
                throw new RuntimeException(e);
            }
        }else printMessage("Invalid Command, type -h or -help to show all available commands.");

    }

    /**
     * Defines which are the correct commands for the ongoing game phase
     * @param plAction contains terminal player's input
     */
	private void matchCMDs(String[] plAction) {
		if (!isMatchRunning) {
			printMessage("[ERROR]: Game hasn't started yet.");
			return;
		}

		String action = " ";
		int cIndex = -1;

		action = plAction[0];
		int cmdSize = plAction.length;

		if (cmdSize <= 3) {
			try {
				switch (cmdSize) {
					case 1 -> {}
					case 2 -> {
						if (!action.equals("show"))
							cIndex = parseInt(plAction[1]);
					}
					case 3 -> {
						action += " " + plAction[1];
						cIndex = parseInt(plAction[2]);
					}
					default -> printMessage("Invalid Command, type -h or -help to show all available commands.");
				}

				switch (action) {
					case "-h", "-help" -> showGameCommands();
					case "show" -> displayChosenPlayer(plAction[1]);
					case "tile", "t" -> vView.checkTile(wantedTile(cIndex));
					case "draw up", "d u" -> vView.checkCharacterCard(wantedCCard(wantedCard(Move.PICK_FROM_UP, cIndex)));
					case "draw down", "d d" -> vView.checkCharacterCard(wantedCCard(wantedCard(Move.PICK_FROM_DOWN, cIndex)));
					case "buy up", "b u" -> vView.checkBuildingCard(wantedBuild(Move.PICK_FROM_UP, cIndex));
					case "buy down", "b d" -> vView.checkBuildingCard(wantedBuild(Move.PICK_FROM_DOWN, cIndex));
					case "info cup", "i cu" -> printMessage(TColors.GOLD + (wantedCard(Move.PICK_FROM_UP, cIndex).getCardInfo(new StringBuilder())) + TColors.RESET);
					case "info cdown", "i cd" -> printMessage(TColors.GOLD + wantedCard(Move.PICK_FROM_DOWN, cIndex).getCardInfo(new StringBuilder()) + TColors.RESET);
					case "info bup", "i bu" -> printMessage(TColors.BROWN + wantedBuild(Move.PICK_FROM_UP, cIndex).getCardInfo(new StringBuilder()) + TColors.RESET);
					case "info bdown", "i bd" -> printMessage(TColors.BROWN + wantedBuild(Move.PICK_FROM_DOWN, cIndex).getCardInfo(new StringBuilder()) + TColors.RESET);
					default -> printMessage("Invalid Command, type -h or -help to show all available commands.");
				}
			} catch (NumberFormatException | IndexOutOfBoundsException e) {
				printMessage("Invalid number: outOfBound/WrongRow/Event was picked");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else printMessage("Invalid Command, type -h or -help to show all available commands.");
	}

	/**
	 * Defines which are the correct commands for the ongoing game phase - asking for DB
	 * @param plAction
	 */
	private void endScreenCMDs(String[] plAction) {
		String action = plAction[0];
		int cmdSize = plAction.length;

		if (cmdSize > 1) {
			printMessage("Invalid Command, either type y or n.");
			return;
		}

		try {
			switch (action) {
				case "y" -> {
					vView.answerShowRankings(true);
				}
				case "n" -> {
					vView.answerShowRankings(false);
					printMessage("Shutting down...");
				}
			}
		} catch (Exception e) {
			printMessage("[ERROR]: No Connection to database found.\nShutting down...");
		}
		gPhase = END;
	}

	//PRIVATE METHODS USED TO DISPLAY CARDS/MESSAGES

	/**
	 * Method to print on TUI - Holds a lock on tuiLock to prevent other methods to change
	 * TUI graphic simultaneously
	 * ALL messages should be printed using this method, otherwise remind yourself to synchronize with tuiLock
	 */
	private void printMessage(String message) {
		synchronized (tuiLock) {
			System.out.println(TColors.RED + message + TColors.RESET);
			System.out.print("cmd > ");
			System.out.flush();
		}
	}

	/**
	 * Method to print on TUI - Prints all [System] messages green
	 * @param message string to be printed
	 */
	private void printSysMessage(String message) {
		synchronized (tuiLock) {
			System.out.println(TColors.GREEN_B + message + TColors.RESET);
			System.out.print("cmd > ");
			System.out.flush();
		}
	}

	/**
	 * Prints a list of all available commands during a match
	 */
	private void showGameCommands() {
		printMessage(TColors.CYAN_B + """
				tile #num -> chose a Tile (t)\s
				draw up #num -> draw a Card (d u)\s
				draw down #num-> draw a Card (d d\s
				buy up #num -> buy a Building (b u)\s
				buy down #num -> chose a Building (b d)\s
				show #plName -> shows tribe of player 
				info cUp/cDown/bUp/bDown #num -> shows info on selected card (i cu/cd/bu/bd)
				-h or -help #num -> to show available commands\s
			\t""" + TColors.RESET);
	}

	/**
	 * Prints a list of all available commands during lobby creation/join
	 */
	private void showMenuCommands() {
		printMessage(TColors.CYAN + """
				list -> shows available lobbies\s
				create #plNum #code -> creates a lobby with chosen parameters
				join #code-> join #code lobby\s
				-h or -help #num -> to show available commands\s
			\t""" + TColors.RESET);
	}

	/**
	 * clears terminal and calls methods to print cards for current state
	 */
	private void boardRender() {
		synchronized (tuiLock) {
			clearTUI();
			printCards();
		}
	}

	/**
	 * Method clears the TUI - it outputs blank lines
	 */
	private void clearTUI() {
		for (int i = 0; i < 20; ++i) System.out.println("\n");
	}

	// METHODS TO PRINT CARDS ON TERMINAL

	/**
	 * prints all cards and players on the terminal
	 */
	private void printCards() {
		List<Card> upRow = vBoard.getUpperRow();
		List<BuildingCard> upBuild = vBoard.getUpperBuildings();
		List<Card> loRow = vBoard.getLowerRow();
		List<BuildingCard> loBuild = vBoard.getLowerBuildings();
		List<Tile> tiles = vBoard.getTiles();
		List<Player> players = vBoard.getPlayers();

		for (Player p : players) {
			displayPlayer(p);
			System.out.println("\n");
		}
		System.out.println("\n");

		if (tileBoost == null) decideTileBoost();
		displayTileBoost();

		System.out.println(TColors.GREEN_B + "\n\n--- UPPER-ROW --------------" + TColors.RESET);
		displayRows(upRow);

		System.out.println(TColors.YELLOW_B + "--- UPPER-BUILDS -----------" + TColors.RESET);
		displayBuilds(upBuild);

		System.out.println(TColors.BLUE_B + "\n--- TILES ------------------" + TColors.RESET);
		displayTiles(tiles);
        System.out.println(TColors.BLUE_B + "----------------------------" + TColors.RESET);

		System.out.println(TColors.GREEN_B + "\n--- LOWER-ROW --------------" + TColors.RESET);
		displayRows(loRow);

		System.out.println(TColors.YELLOW_B + "--- LOWER-BUILDS -----------" + TColors.RESET);
		displayBuilds(loBuild);

		Player crntPlayer = vBoard.getCurrentUser();
		if (crntPlayer != null) displayPlayer(crntPlayer);

		System.out.println("\n");
	}

	/**
	 * Used to print each line
	 * @param ln1 @param ln2 @param ln3: each represents a line on the terminal
	 */
	private void displayRows(StringBuilder ln1, StringBuilder ln2, StringBuilder ln3) {
		System.out.println(ln1);
		System.out.println(ln2);
		System.out.println(ln3);

		ln1.setLength(0);
		ln2.setLength(0);
		ln3.setLength(0);
	}

	/**
	 * Used to print Upper and Lower Row cards' info
	 * @param row is either upper or lower row of vBoard
	 */
	private void displayRows(List<Card> row) {
		if (row.isEmpty()) return;

		int j = 0;
		int maxCardsXRow = 6;

		StringBuilder ln1 = new StringBuilder();
		StringBuilder ln2 = new StringBuilder();
		StringBuilder ln3 = new StringBuilder();

		for (Card card : row) {
			ln1.append(TColors.CYAN_B).append("|").append(TColors.RESET);
			ln2.append(TColors.CYAN_B).append("|").append(TColors.RESET);
			ln3.append(TColors.CYAN_B).append("|").append(TColors.RESET);
			ln1.append(TColors.MAGENTA_B).append(j).append(".").append(TColors.RESET);
			ln2.append("  ");
			ln3.append("  ");
			card.createRow(ln1, ln2, ln3);
			j++;
			if (j % maxCardsXRow == 0) {
				displayRows(ln1, ln2, ln3);
			}
		}
		if (j % maxCardsXRow != 0) displayRows(ln1, ln2, ln3);
	}

	/**
	 * Displays buildings info
	 */
	private void displayBuilds(List<BuildingCard> builds) {
		if (builds.isEmpty()) return;

		int j = 0;
		StringBuilder ln1 = new StringBuilder();
		StringBuilder ln2 = new StringBuilder();
		StringBuilder ln3 = new StringBuilder();

		for (Card card : builds) {
			ln1.append(TColors.MAGENTA_B).append(j).append(".").append(TColors.RESET);
			card.createRow(ln1, ln2, ln3);
			j++;
		}
		displayRows(ln1, ln2, ln3);
	}

	/**
	 * Displays tiles info
	 */
	private void displayTiles(List<Tile> tiles) {
		if (tiles.isEmpty()) return;

		int j = 0;
		StringBuilder ln1 = new StringBuilder();
		StringBuilder ln2 = new StringBuilder();
		StringBuilder ln3 = new StringBuilder();

		for (Tile tile : tiles) {
			ln1.append(TColors.MAGENTA_B).append(j).append(".").append(TColors.RESET);
			ln2.append(TColors.MAGENTA_B).append(j).append(".").append(TColors.RESET);
			ln3.append("  ");
			tile.createRow(ln1, ln2, ln3);
			j++;
		}
		displayRows(ln1, ln2, ln3);
	}

	/**
	 * Displays player's info
	 */
	private void displayPlayer(Player p) {
        System.out.println(TColors.GREEN_B + "\n--- TRIBE OF " + TColors.CYAN_B + p.getNickname() + TColors.GREEN_B + " ---" + TColors.RESET);
		p.displayTribe();
		p.displayStats();
	}

	/**
	 * Displays player's info
	 */
	private void displayChosenPlayer(String nickname) {
		synchronized (tuiLock) {
			for (Player p : vBoard.getPlayers()) {
				if (p.getNickname().equals(nickname)) {
					displayPlayer(p);
					System.out.println("\n\ncmd > ");
					System.out.flush();
					return;
				}
			}
		}
		printMessage("\n[ERROR]: player does not exists.");
	}

	/**
	 * Displays Scoreboard of played match
	 */
	private void displayScoreboard() {
		int i = 0;
		StringBuilder score = new StringBuilder();
		List<Player> finalPlayerList = vBoard.getPlayers().stream().
				sorted(Comparator.comparing(p -> p.getParameters().get(Parameter.PRESTIGE_POINTS))).toList();

		score.append("\n").append(TColors.BLUE).append("----- SCOREBOARD -----").append(TColors.RESET);

		for(Player p : finalPlayerList) {
			i++;
			score.append(TColors.GOLD).append("\n").append(i).append("° ").append(p.getNickname()).append(TColors.RESET);
		}

		printMessage(score.toString());
	}

	/**
	 * Selects food/pp round Tile based on player's number
	 */
	private void decideTileBoost() {
		tileBoost = switch(vBoard.getPlayers().size()){
			case 2 -> new int[]{1,-1};
			case 3 -> new int[]{2,0,-1};
			case 4 -> new int[]{2,1,0,-1};
			case 5 -> new int[]{3,1,0,0,-1};
			default -> new int[]{};
		};
	}

	/**
	 * Displays food/pp round Tile
	 */
	private void displayTileBoost() {
		System.out.println(TColors.YELLOW + "\n\n--- FOOD TILES --------------" + TColors.RESET);
		for (int j = 0; j < tileBoost.length; j++) {
            System.out.println(TColors.ORANGE + "Tile: " + tileBoost[j] + (tileBoost[j]>0 ? " Food" : (tileBoost[j]<0 ? " pP" : "")) + TColors.RESET);
        }

	}

	/**
	 * Shuts down the thread and terminates the client.
	 */
	private void endGame() {
		printMessage(TColors.GREEN + "Game is ended - Thank you for playing." + TColors.RESET);
		clientExecutor.shutdown();
		System.exit(0);
	}
}









