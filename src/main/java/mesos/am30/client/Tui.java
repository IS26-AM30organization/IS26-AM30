package mesos.am30.client;

import mesos.am30.gameModel.*;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Card;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.Tile;
import mesos.am30.common.ErrorType;
import mesos.am30.common.GamePhase;
import mesos.am30.common.Move;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Integer.parseInt;
import static mesos.am30.common.GamePhase.*;

/**
 * This class represents the Terminal Interface
 */
public class Tui implements IF_GameUI {
	ViewModel vBoard;
	VirtualView vView;

	private final ExecutorService clientExecutor = Executors.newFixedThreadPool(1);
	volatile GamePhase gPhase;
	final Object tuiLock = new Object();
	boolean isMatchRunning = false;

	Scanner actionScanner = new Scanner(System.in);

	public Tui() {
		gPhase = GamePhase.MENU;
	}

	/**
	 * Starts executor service for terminal reading
	 */
	public void startCLient() {
		clientExecutor.submit(() -> plInputReader());
	}

	/**
	 * Allows execution of available commands based on gamePhase
	 */
	public void plInputReader() {
		String action = "";

		while (gPhase != END) {
			String plInput = actionScanner.nextLine();
			String[] plAction = plInput.toLowerCase().split("\\s+");

			if (Objects.requireNonNull(gPhase) == GamePhase.GAME) {
				matchCMDs(plAction);
			} else {
				printMessage("The current Phase is non existent");
			}
		}
		printEnd();
	}

	/**
	 * @return nickname inserted after server's invoice
	 */
	public String askNickname() {
		synchronized (tuiLock) {
			System.out.print("Inserisci nickname > ");
			System.out.flush();
		}
		return actionScanner.nextLine();
	}

	/**
	 * @return playerNumber inserted after server's invoice
	 */
	@Override
	public int askPlayersNumber() {
		synchronized (tuiLock) {
			System.out.print("Inserisci playerNum > ");
			System.out.flush();
		}
		try {
			return Integer.parseInt(actionScanner.nextLine());
		} catch (NumberFormatException e) {
			printMessage("[ERROR]: invalid");
			return 0;
		}
	}

	/**
	 * Invoked by virtualView, used to set vBoard
	 */
	public void refresh(ViewModel vBoard) {
		this.vBoard = vBoard; //check if needed
		//boardRender();
	}

	/**
	 * Shows on TUI actions made by any player
	 * On first call it starts the terminal executor.
	 */
	public void printMove(String nickname, Move move) {
		boardRender();
		printMessage("\033[1;36m" + "[System]: Player " + "\033[1;33m" + nickname + "\033[0m" + " has " + move + "\033[0m");

		if (isMatchRunning) return;
		isMatchRunning = true;
		gPhase = GamePhase.GAME;
		printMessage("[System]: game is starting: type -h or -help to show available commands.");
		startCLient();
	}

	/**
	 * Handles end-game screen
	 */
	public void printEnd() {
		printMessage("Game is ended - Thank you for playing\n Press 'enter' to close.");
		gPhase = END;

		clientExecutor.shutdown();
		System.exit(0);
	}

	/**
	 * Shows on TUI any errors sent by the server/view model for incorrect player's actions
	 * if the wrong nickname or playersNumber was inserted, client gets prompted again
	 */
	public void printError(ErrorType errorMessage) {
		printMessage("\033[1;31m" + "[Error]: " + errorMessage + "\033[0m");
		if (errorMessage == ErrorType.WRONG_NICKNAME || errorMessage == ErrorType.ALREADY_EXISTING_LOBBY)
			promptPlayerNickname();
		else if (errorMessage == ErrorType.WRONG_PLAYERS_NUMBER) promptPlayerNumber();
		else if (errorMessage == ErrorType.FULL_LOBBY) printEnd();
	}

	//PRIVATE LOGIC METHODS

	/**
	 * if server replies that playerNickname was entered incorrectly, this method re-promts the user to insert it
	 */
    void promptPlayerNickname() {
		clientExecutor.submit(() -> {
					try {
						if (vView != null) vView.askNickname();
					} catch (IOException e) {
						printMessage("Error with server.");
					}
				}
		);
	}

	/**
	 * if server replies that playerNum was entered incorrectly, this method re-promts the user to insert it
	 */
    void promptPlayerNumber() {
		clientExecutor.submit(() -> {
					try {
						if (vView != null) vView.askPlayersNumber();
					} catch (IOException e) {
						printMessage("Error with server.");
					}
				}
		);
	}

	/**
	 * Returns the wanted Tile, if existing
	 * @throws IndexOutOfBoundsException if number inserted is incorrect
	 */
	private Tile wantedTile(int tileIndex) throws IndexOutOfBoundsException {
		return vBoard.getTiles().get(tileIndex);
	}

	/**
	 * Returns the wanted Card, if existing
	 * @throws IndexOutOfBoundsException if number inserted is incorrect
	 */
	private CharacterCard wantedCCard(Move move, int cIndex) throws IndexOutOfBoundsException {
		switch (move) {
			case PICK_FROM_UP -> {
				Card selectedCard = vBoard.getUpperRow().get(cIndex);
				if (!vBoard.getUpperRow().get(cIndex).isPickable())
					throw new IndexOutOfBoundsException("Card is not a Character.");
				return (CharacterCard) selectedCard;
			}
			case PICK_FROM_DOWN -> {
				Card selectedCard = vBoard.getLowerRow().get(cIndex);
				if (!vBoard.getLowerRow().get(cIndex).isPickable())
					throw new IndexOutOfBoundsException("Card is not a Character.");
				return (CharacterCard) selectedCard;
			}
			default -> {
				return null;
			}
		}
	}

	/**
	 * Returns the wanted BuildingCard, if existing
	 * @throws IndexOutOfBoundsException if number inserted is incorrect
	 */
	private BuildingCard wantedBuild(Move move, int cIndex) throws IndexOutOfBoundsException {
		switch (move) {
			case PICK_FROM_UP -> {
				return vBoard.getUpperBuildings().get(cIndex);
			}
			case PICK_FROM_DOWN -> {
				return vBoard.getLowerBuildings().get(cIndex);
			}
			default -> {
				return null;
			}
		}
	}

	/**
	 * Defines which are the correct commands for the ongoing game phase
	 * @param plAction contains terminal player's input
	 */
	private void matchCMDs(String[] plAction) {
		String action = " ";
		int cIndex = -1;

		action = plAction[0];
		int cmdSize = plAction.length;

		if (cmdSize > 0 && cmdSize <= 3) {
			try {
				switch (cmdSize) {
					case 1 -> {}
					case 2 -> cIndex = parseInt(plAction[1]);
					case 3 -> {
						action += " " + plAction[1];
						cIndex = parseInt(plAction[2]);
					}
					default -> printMessage("Invalid Command, type -h or -help to show all available commands.");
				}

				switch (action) {
					case "-h", "-help" -> showAvailableCommands();
					case "tile" -> vView.checkTile(wantedTile(cIndex));
					case "draw up" -> vView.checkCharacterCard(wantedCCard(Move.PICK_FROM_UP, cIndex));
					case "draw down" -> vView.checkCharacterCard(wantedCCard(Move.PICK_FROM_DOWN, cIndex));
					case "buy up" -> vView.checkBuildingCard(wantedBuild(Move.PICK_FROM_UP, cIndex));
					case "buy down" -> vView.checkBuildingCard(wantedBuild(Move.PICK_FROM_DOWN, cIndex));
					default -> printMessage("Invalid Command, type -h or -help to show all available commands.");
				}
			} catch (NumberFormatException | IndexOutOfBoundsException e) {
				printMessage("Invalid number: outOfBound/WrongRow/Event was picked");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else printMessage("Invalid Command, type -h or -help to show all available commands.");
	}

	//PRIVATE METHODS USED TO DISPLAY CARDS/MESSAGES

	/**
	 * Method to print on TUI - Holds a lock on tuiLock to prevent other methods to change
	 * TUI graphic simultaneously
	 * ALL messages should be printed using this method, otherwise remind yourself to synchronize with tuiLock
	 */
	private void printMessage(String message) {
		synchronized (tuiLock) {
			System.out.println("\033[1;31m" + message + "\033[0m");
			System.out.print("cmd > ");
			System.out.flush();
		}
	}

	/**
	 * Prints a list of all available commands
	 */
	private void showAvailableCommands() {
		printMessage("""
				tile #num -> chose a Tile 
				draw up #num -> draw a Card 
				draw down #num-> draw a Card 
				buy up #num -> buy a Building 
				buy down #num -> chose a Building 
				-h or -help #num -> to show available commands 
				""");
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
	 * Method clears the TUI
	 */
	private void clearTUI() {
		try {
			for (int i = 0; i < 20; ++i) System.out.println("\n");
		} catch (Exception e) {
			for (int i = 0; i < 20; ++i) System.out.println("\n");
			System.out.print("\033[H\033[2J");
			System.out.flush();
		}
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
		}

		System.out.println("\033[1;32m" + "\n\n--- UPPER-ROW --------------" + "\033[0m");
		displayRows(upRow);

		System.out.println("\033[1;33m" + "--- UPPER-BUILDS -----------" + "\033[0m");
		displayBuilds(upBuild);

		System.out.println("\033[1;34m" + "\n--- TILES ------------------" + "\033[0m");
		displayTiles(tiles);

		System.out.println("\033[1;32m" + "\n--- LOWER-ROW --------------" + "\033[0m");
		displayRows(loRow);

		System.out.println("\033[1;33m" + "--- LOWER-BUILDS -----------" + "\033[0m");
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
			ln1.append("\033[1;35m").append(j).append(".").append("\033[0m");
			ln2.append("  ");
			ln3.append("  ");
			card.createRow(ln1, ln2, ln3);
			j++;
			if (j == maxCardsXRow) {
				displayRows(ln1, ln2, ln3);
				j = 0;
			}
		}
		if (j > 0) displayRows(ln1, ln2, ln3);
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
			ln1.append("\033[1;35m").append(j).append(".").append("\033[0m");
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
			ln1.append("\033[1;35m").append(j).append(".").append("\033[0m");
			ln2.append("\033[1;35m").append(j).append(".").append("\033[0m");
			tile.createRow(ln1, ln2, ln3);
			j++;
		}
		displayRows(ln1, ln2, ln3);
	}

	/**
	 * Displays player's info
	 */
	private void displayPlayer(Player p) {
		System.out.println("\033[1;32m" + "\n--- TRIBE OF " + "\033[1;36m" + p.getNickname() + "\033[1;32m" + " ---" + "\033[0m");
		p.displayTribe();
		p.displayStats();
	}

}








