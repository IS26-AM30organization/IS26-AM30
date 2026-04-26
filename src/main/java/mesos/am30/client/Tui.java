package mesos.am30.client;

import mesos.am30.GameModel.BuildingCard;
import mesos.am30.GameModel.Card;
import mesos.am30.GameModel.CharacterCard;
import mesos.am30.GameModel.Tile;
import mesos.am30.common.ErrorType;
import mesos.am30.common.GamePhase;
import mesos.am30.common.Move;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Integer.parseInt;
import static mesos.am30.common.GamePhase.END;

public class Tui implements IF_GameUI{
	ViewModel vBoard;
	VirtualView vView;

	private final ExecutorService clientExecutor =  Executors.newFixedThreadPool(1);
	volatile GamePhase gPhase;
	final Object tuiLock = new Object();
	boolean isMatchRunning = false;

	Scanner actionScanner = new Scanner(System.in);

	public Tui(){
        gPhase = GamePhase.MENU;
	}

	/**Starts executor service for terminal reading
	 */
	public void startCLient() {
		clientExecutor.submit(() -> plInputReader());
	}

	/**Allows execution of available commands based on gamePhase
	 */
	public void plInputReader() {
		String action = "";

		while (gPhase != END) {
			String plInput = actionScanner.nextLine();
			String[] plAction = plInput.toLowerCase().split("\\s+");

			switch (gPhase) {
                case GAME -> matchCMDs(plAction);
				default -> printMessage("The current Phase is non existent");
			}
		}
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

	/**Invoked by views, once called, it updates the TUI
	 * On first call it starts the terminal executor.
	 */
	public void refresh(ViewModel vBoard) {
		this.vBoard = vBoard; //check if needed

		boardRender();
	}

	/**Shows on TUI actions made by any player
	 */
	public void printMove(String nickname, Move move) {
		printMessage("[System]: Player " + nickname + " has " + move);

		if (isMatchRunning) return;
		isMatchRunning = true;
		gPhase = GamePhase.GAME;
		printMessage("[System]: game is starting - type -h or -help to show available commands.");
		startCLient();
	}

	/**Handles end-game screen
	 */
	public void printEnd() {
		printMessage("Game is ended - Thank you for playing\n Press 'enter' to close.");
		gPhase = END;

		clientExecutor.shutdown();
		System.exit(0);
	}

	/**Shows on TUI any errors sent by the server/view model for incorrect player's actions
	 *if the wrong nickname or playersNumber was inserted, client gets prompted again
	 */
	public void printError(ErrorType errorMessage) {
		printMessage("[Error]: " + errorMessage);
		if (errorMessage == ErrorType.WRONG_NICKNAME || errorMessage == ErrorType.ALREADY_EXISTING_LOBBY) promptPlayerNickname();
		else if (errorMessage == ErrorType.WRONG_PLAYERS_NUMBER) promptPlayerNumber();
		else if (errorMessage == ErrorType.FULL_LOBBY) printEnd();
	}

	//PRIVATE METHODS

	private void promptPlayerNickname() {
		clientExecutor.submit(() -> {
			try {
				if (vView != null) vView.askNickname();
			} catch (IOException e) {
				printMessage("Error with server.");
			}
		}
		);
	}

	private void promptPlayerNumber() {
		clientExecutor.submit(() -> {
					try {
						if (vView != null) vView.askPlayersNumber();
					} catch (IOException e) {
						printMessage("Error with server.");
					}
				}
		);
	}

	/**Returns the wanted Tile, if existing
	*@throws IndexOutOfBoundsException if number inserted is incorrect
	*/
	private Tile wantedTile(int tileIndex) throws IndexOutOfBoundsException {
		return vBoard.getTiles().get(tileIndex);
	}

	/**Returns the wanted Card, if existing
	*@throws IndexOutOfBoundsException if number inserted is incorrect
	*/
	private CharacterCard wantedCCard(Move move, int cIndex) throws IndexOutOfBoundsException {
		switch (move) {
            case PICK_FROM_UP -> {
				Card selectedCard = vBoard.getUpperRow().get(cIndex);
				if (!vBoard.getUpperRow().get(cIndex).isPickable()) throw new IndexOutOfBoundsException("Card is not a Character.");
				return (CharacterCard) selectedCard;
            }
            case PICK_FROM_DOWN -> {
				Card selectedCard = vBoard.getLowerRow().get(cIndex);
				if (!vBoard.getLowerRow().get(cIndex).isPickable()) throw new IndexOutOfBoundsException("Card is not a Character.");
				return (CharacterCard) selectedCard;
            }
			default -> {
				return null;
			}
        }
	}
	/**Returns the wanted BuildingCard, if existing
	*@throws IndexOutOfBoundsException if number inserted is incorrect
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

	//AVAILABLE COMMANDS

	/**
	 * Defines which are the correct commands for game phase
	 * @param plAction contains terminal player's input
	 */
	private void matchCMDs(String[] plAction) {
		String action = " ";
		int cIndex = 0;

		action = plAction[0];
		int cmdSize = plAction.length;

		if(cmdSize>0 && cmdSize<=3) {
			switch (cmdSize) {
				case 2 -> cIndex = parseInt(plAction[1]);
                case 3 -> {
                    action += " " + plAction[1];
                    cIndex = parseInt(plAction[2]);
                }
                default -> printMessage("Invalid Command.");
			}
			try {
				switch (action) {
					case "-h", "-help" -> showAvailableCommands();
                    case "tile" -> vView.checkTile(wantedTile(cIndex));
                    case "draw up" -> vView.checkCharacterCard(wantedCCard(Move.PICK_FROM_UP, cIndex));
                    case "draw down" -> vView.checkCharacterCard(wantedCCard(Move.PICK_FROM_DOWN, cIndex));
					case "buy up" -> vView.checkBuildingCard(wantedBuild(Move.PICK_FROM_UP, cIndex));
                    case "buy down" -> vView.checkBuildingCard(wantedBuild(Move.PICK_FROM_DOWN, cIndex));
                    default -> printMessage("Invalid Command.");
				}
			} catch (NumberFormatException | IndexOutOfBoundsException e) {
				printMessage("Invalid number.");
			} catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else printMessage("Invalid Command.");
	}

	/**Method to print on TUI - Holds a lock on tuiLock to prevent other methods to change
	*TUI graphic simultaneously
	 * ALL messages should be printed using this method, otherwise remind yourself to synchronize with tuiLock
	*/
	private void printMessage(String message) {
		synchronized (tuiLock) {
			System.out.println(message);
			System.out.print("cmd > ");
			System.out.flush();
		}
	}

	/**clears terminal and calls methods to print cards for current state
	*/
	private void boardRender() {
		synchronized (tuiLock) {
			clearTUI();
			printCards();
		}
	}

	/**
	 * Method clears the TUI
	 * takes a lock on tuiLock
	 */
	private void clearTUI() {
		synchronized (tuiLock) {
			System.out.print("\033[H\033[2J");
			System.out.flush();
		}
	}

	/**prints cards on TUI
	*/
	private void printCards() {
		synchronized (tuiLock){
			printMessage("Cards\nCards\ncards");
		}
	}


}








