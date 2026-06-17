package mesos.am30.client;

import mesos.am30.common.*;
import mesos.am30.gameModel.*;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Card;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.Tile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Integer.parseInt;
import static mesos.am30.common.GamePhase.*;

/**
 * Terminal Interface for the View.
 * <br/>This Class works as the TUI for the View, which, as the connection to the Server is acknowledged, runs a thread that reads the player's inputs.
 * <br/>Based the Game's phase, only certain commands are allowed.
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

	/**
	 * Constructor for the TUI.
	 */
	public Tui() {
		gPhase = GamePhase.MENU;
	}

	/**
	 * Starts executor service for terminal reading.
	 */
	public void startClient() {
		clientExecutor.submit(this::plInputReader);
	}

	/**
	 * Allows execution of available commands based on gamePhase.
	 */
    public void plInputReader() {
        while (gPhase != END) {
            String plInput;
			try {
				plInput = actionScanner.nextLine();
			} catch (Exception ignore){break;}
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
	 * @see IF_GameUI TUI implementation of the setVView method.
	 */
	@Override
	public void setVView(VirtualView view) {
		this.vView=view;
	}

	/**
	 * @see IF_GameUI TUI implementation of the setVModel method.
	 */
	@Override
	public void setVModel(ViewModel vBoard){
		this.vBoard = vBoard;
	}

	/**
	 * @see IF_GameUI TUI implementation of the confirmConnection method.
	 */
	@Override
	public void confirmConnection() {
		printSysMessage(TColors.GREEN_B + "[System]: Connected! Type -h or -help to show available commands at anytime." + TColors.RESET);
		gPhase = GamePhase.MENU;
		startClient();
	}

	/**
	 * @see IF_GameUI TUI implementation of the showLobbies method.
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
	 * @see IF_GameUI TUI implementation of the askNickname method.
	 */
	@Override
    public void askNickname() {
            gPhase = GamePhase.LOBBY;
            printMessage("Insert nickname: ");
    }

	/**
	 * @see IF_GameUI TUI implementation of the confirmLobbyJoined method.
	 */
	@Override
	public void confirmLobbyJoined() {
		printSysMessage("[System]: Lobby joined! \n[System]: Waiting for other players... \nOnce the match starts, type -h or -help to show all available commands.");
		gPhase = GamePhase.GAME;
	}

	/**
	 * @see IF_GameUI TUI implementation of the printMove method.
	 */
	@Override
	public void printMove(String nickname, Move move) {
		boardRender();
		printSysMessage(TColors.CYAN_B + "[System]: Player " + TColors.YELLOW + nickname + TColors.RESET + " has " + move + TColors.RESET);

		if (isMatchRunning) return;
		isMatchRunning = true;
	}

	/**
	 * @see IF_GameUI TUI implementation of the printError method.
	 */
	@Override
	public void printError(ErrorType errorMessage) {
		printMessage(TColors.RED_B + "[Error]: " + errorMessage + TColors.RESET);

		switch (errorMessage) {
			case WRONG_PLAYERS_NUMBER -> printMessage("Type: create #plNum, to create a lobby.");
			case WRONG_NICKNAME -> askNickname();
		}
	}

	// already handled in the update
	@Override
	public void refresh(ViewModel vBoard) {}

	/**
	 * @see IF_GameUI TUI implementation of the askShowRankings method.
	 */
	@Override
	public void askShowRankings() {
		displayScoreboard();

		gPhase = END_SCREEN;
		printMessage(TColors.GREEN_B + "Would you like to see/reload the Leaderboard? [y/n]: " + TColors.RESET);
	}

	/**
	 * @see IF_GameUI TUI implementation of the showRankings method.
	 */
	@Override
	public void showRankings(Map<String,String> playerMap, List<Map<String, String>> ranking) {
		if (ranking == null || ranking.isEmpty()) {
			printSysMessage("[System]: no leaderboard to display for this game settings\nWould you like to see/reload the Leaderboard? [y/n]: ");
			return;
		}

		printMessage(TColors.GOLD + "--- GLOBAL RANKING ---" + TColors.RESET);
		for (Map<String, String> row : ranking) {
			String rank = row.get("RANK");
			String nickname = row.get("Nickname");
			String score = row.get("Score");

			printMessage(TColors.CYAN + rank + "° | Player: " + nickname + " | Points: " + score + TColors.RESET);
		}

		System.out.println("----------------------");

		if (playerMap != null && !playerMap.isEmpty()) {
			String playerRank = playerMap.get("RANK");
			String playerNick = playerMap.get("Nickname");
			String playerScore = playerMap.get("Score");

			printMessage(TColors.GREEN + "Your Rank: " + playerRank + "° | Player: " + playerNick + " | Points: " + playerScore + "\n" + TColors.RESET);
		} else {
			printMessage(TColors.MAGENTA_B + "You are not in the ranking for this game mode yet.\n" + TColors.RESET);
		}
	}

	/**
	 * @see IF_GameUI TUI implementation of the printEnd method.
	 */
	@Override
	public void printEnd() {
		gPhase = END;
		displayScoreboard();

		endGame();
	}

	//PRIVATE LOGIC METHODS

	// return the wanted Tile, if existing
	private Tile wantedTile(int tileIndex) throws IndexOutOfBoundsException {
		return vBoard.getTiles().get(tileIndex);
	}

	// return the wanted Card, if existing
	private Card wantedCard(Move move,int cIndex) throws IOException {
		switch (move) {
			case PICK_FROM_UP -> {
				return vBoard.getUpperRow().get(cIndex);
			}
			case PICK_FROM_DOWN -> {
				return vBoard.getLowerRow().get(cIndex);
			}
			default -> throw new IOException("Unknown Move");
		}
	}

	// return the wanted CharacterCard, if pickable
	private CharacterCard wantedCCard(Card card) {
		if (!card.isPickable())
			throw new IndexOutOfBoundsException("Card is not a Character.");
		return (CharacterCard) card;
	}

	// return the wanted BuildingCard, if existing
	private BuildingCard wantedBuild(Move move, int cIndex) throws IndexOutOfBoundsException, IOException {
		switch (move) {
			case PICK_FROM_UP -> {
				return vBoard.getUpperBuildings().get(cIndex);
			}
			case PICK_FROM_DOWN -> {
				return vBoard.getLowerBuildings().get(cIndex);
			}
			default -> throw new IOException("Unknown Move");
		}
	}
    // define which are the correct commands for the ongoing game phase: MENU
    private void menuCMDs(String[] plAction) {
        int numOne = -1;
        String numTwo = "";

        String action = plAction[0];
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

    // define which are the correct commands for the ongoing game phase
	private void matchCMDs(String[] plAction) {
		if (!isMatchRunning) {
			printMessage("[ERROR]: Game hasn't started yet.");
			return;
		}

		int cIndex = -1;

		String action = plAction[0];
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

	// define which are the correct commands for the ongoing game phase - asking for DB
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
					gPhase = END;
				}
				case "n" -> {
					vView.answerShowRankings(false);
					printMessage("Shutting down...");
					gPhase = END;
				}
				default -> printMessage("Invalid Command, either type y or n.");
			}
		} catch (Exception e) {
			printMessage("[ERROR]: No Connection to database found.\nShutting down...");
			gPhase = END;
		}
	}

	//PRIVATE METHODS USED TO DISPLAY CARDS/MESSAGES

	// print a Message on TUI using tuiLock (prevent race conditions)
	private void printMessage(String message) {
		synchronized (tuiLock) {
			System.out.println(TColors.RED + message + TColors.RESET);
			System.out.print("cmd > ");
			System.out.flush();
		}
	}

	// print on TUI - Prints all [System] messages green
	private void printSysMessage(String message) {
		synchronized (tuiLock) {
			System.out.println(TColors.GREEN_B + message + TColors.RESET);
			System.out.print("cmd > ");
			System.out.flush();
		}
	}

	// prints a list of all available commands during a match
	private void showGameCommands() {
		printMessage(TColors.CYAN_B + """
				tile #num -> chose a Tile (t)\s
				draw up #num -> draw a Card (d u)\s
				draw down #num-> draw a Card (d d\s
				buy up #num -> buy a Building (b u)\s
				buy down #num -> chose a Building (b d)\s
				show #plName -> shows tribe of player\s
				info cUp/cDown/bUp/bDown #num -> shows info on selected card (i cu/cd/bu/bd)
				-h or -help #num -> to show available commands\s
			\t""" + TColors.RESET);
	}

	// prints a list of all available commands during lobby creation/join
	private void showMenuCommands() {
		printMessage(TColors.CYAN + """
				list -> shows available lobbies\s
				create #plNum #code -> creates a lobby with chosen parameters
				join #code-> join #code lobby\s
				-h or -help #num -> to show available commands\s
			\t""" + TColors.RESET);
	}

	// clears terminal and calls methods to print cards for current state
	private void boardRender() {
		synchronized (tuiLock) {
			clearTUI();
			printCards();
		}
	}

	// clear the TUI - it outputs blank lines
	private void clearTUI() {
		for (int i = 0; i < 20; ++i) System.out.println("\n");
	}

	// METHODS TO PRINT CARDS ON TERMINAL

	// prints all cards and players on the terminal
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

	// print each line
	private void displayRows(StringBuilder ln1, StringBuilder ln2, StringBuilder ln3) {
		System.out.println(ln1);
		System.out.println(ln2);
		System.out.println(ln3);

		ln1.setLength(0);
		ln2.setLength(0);
		ln3.setLength(0);
	}

	// print Upper and Lower Row cards' info
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

	// display buildings info
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

	// display tiles info
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

	// display player's info
	private void displayPlayer(Player p) {
        System.out.println(TColors.GREEN_B + "\n--- TRIBE OF " + TColors.CYAN_B + p.getNickname() + TColors.GREEN_B + " ---" + TColors.RESET);
		p.displayTribe();
		p.displayStats();
	}

	// display player's info (from Nickname)
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

	// displa Scoreboard of played match
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

	// select food/pp round Tile based on player's number
	private void decideTileBoost() {
		tileBoost = switch(vBoard.getPlayers().size()){
			case 2 -> new int[]{1,-1};
			case 3 -> new int[]{2,0,-1};
			case 4 -> new int[]{2,1,0,-1};
			case 5 -> new int[]{3,1,0,0,-1};
			default -> new int[]{};
		};
	}

	// display food/pp round Tile
	private void displayTileBoost() {
		System.out.println(TColors.YELLOW + "\n\n--- FOOD TILES --------------" + TColors.RESET);
        for (int i : tileBoost) {
            System.out.println(TColors.ORANGE + "Tile: " + i + (i > 0 ? " Food" : (i < 0 ? " pP" : "")) + TColors.RESET);
        }

	}

	// shut down the thread and terminates the client
	private void endGame() {
		printMessage(TColors.GREEN + "Game is ended - Thank you for playing." + TColors.RESET);
		clientExecutor.shutdown();
		System.exit(0);
	}
}









