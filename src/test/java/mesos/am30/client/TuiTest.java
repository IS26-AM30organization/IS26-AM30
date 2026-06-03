package mesos.am30.client;

import mesos.am30.common.ErrorType;
import mesos.am30.common.GamePhase;
import mesos.am30.common.Move;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TuiTest {
    private Tui tui;

    @Mock
    private ViewModel vBoard;
    @Mock
    private VirtualView vView;

    private ByteArrayOutputStream streamOut;
    private final InputStream rstIn = System.in;

    @BeforeEach
    void setUp() {
        streamOut = new ByteArrayOutputStream(); //different output stream must be used
        System.setOut(new PrintStream(streamOut));
        tui = new Tui();

        lenient().when(vBoard.getUpperRow()).thenReturn(new ArrayList<>());
        lenient().when(vBoard.getUpperBuildings()).thenReturn(new ArrayList<>());
        lenient().when(vBoard.getLowerRow()).thenReturn(new ArrayList<>());
        lenient().when(vBoard.getLowerBuildings()).thenReturn(new ArrayList<>());
        lenient().when(vBoard.getTiles()).thenReturn(new ArrayList<>());
        lenient().when(vBoard.getPlayers()).thenReturn(new ArrayList<>());

        tui.setvModel(vBoard); //needed to reset tui with vBoard mock
        tui.setvView(vView);
    }

    @AfterEach
    void tearDown() {
        System.setIn(rstIn); //sysstem input is reset
    }

    //HELPER METHODS -> SIMULATES USERS INPUT
    private void simInput(String data) {
        InputStream in = new ByteArrayInputStream(data.getBytes());
        System.setIn(in);
        tui.actionScanner = new java.util.Scanner(in);
    }

    /**
     * Executes plInputReader in a thread and waits for 500ms.
     * @param inputString user input
     * @throws InterruptedException
     */
    private void runReaderWithTimeout(String inputString) throws InterruptedException {
        simInput(inputString);
        Thread t = new Thread(tui::plInputReader);
        t.setDaemon(true);
        t.start();
        t.join(500);
        t.interrupt();
    }

    private String output() {
        return streamOut.toString();
    }



    //askNickname
    @Test
    void askNickname_checks() throws IOException {
        tui.askNickname();
        assertTrue(output().contains("Insert nickname: "));
        assertEquals(GamePhase.LOBBY, tui.gPhase);
    }

    //printMove
    @Test
    void printMove_containsNickname() {
        tui.printMove("Alice", Move.PICK_TILE);
        assertTrue(output().contains("Alice"));
    }

    @Test
    void printMove_containsMove() {
        tui.printMove("Alice", Move.PICK_TILE);
        assertTrue(output().contains("PICK_TILE"));
    }

    @Test
    void printMove_setsMatchRunning() {
        assertFalse(tui.isMatchRunning);
        tui.printMove("Bob", Move.PICK_TILE);
        assertTrue(tui.isMatchRunning);
    }

    @Test
    void printMove_noFlagChangesOnSecondCall() {
        tui.printMove("Bob", Move.PICK_TILE);
        tui.printMove("Bob", Move.PICK_TILE);
        assertTrue(tui.isMatchRunning);
    }

    @Test
    void printMove_containsCardMove() {
        tui.printMove("Carlo", Move.PICK_FROM_UP);
        assertTrue(output().contains("PICK_FROM_UP"));
        assertTrue(output().contains("Carlo"));
    }

    //printError
    @Test
    void printError() {
        tui.printError(ErrorType.NOT_YOUR_TURN);
        assertTrue(output().contains("NOT_YOUR_TURN"));
    }

    @Test
    void printError_wrongName() throws Exception {
        tui.printError(ErrorType.WRONG_NICKNAME);
        assertTrue(output().contains("Insert nickname: "));
        assertEquals(GamePhase.LOBBY, tui.gPhase);
    }

    @Test
    void printError_wrongPlayersNumber_containsCreateHint() {
        tui.printError(ErrorType.WRONG_PLAYERS_NUMBER);
        assertTrue(output().contains("Type: create #plNum, to create a lobby."));
        assertTrue(output().contains("WRONG_PLAYERS_NUMBER"));
    }

    //printEnd
    @Test
    void printEnd_EndScreen() {
        tui.printEnd();
        assertEquals(GamePhase.END_SCREEN, tui.gPhase);
    }

    @Test
    void printEnd_leaderboard() {
        tui.printEnd();
        assertTrue(output().contains("Would you like to see/reload the Leaderboard?"));
    }

    //connection Conformations
    @Test
    void confirmConnection_setsMenuPhase() {
        tui.confirmConnection();
        assertEquals(GamePhase.MENU, tui.gPhase);
        assertTrue(output().contains("Connected!"));
    }


    @Test
    void confirmLobbyJoined_setsGamePhase() {
        tui.confirmLobbyJoined();
        assertEquals(GamePhase.GAME, tui.gPhase);
        assertTrue(output().contains("Lobby joined!"));
    }

    //lobbies
    @Test
    void showLobbies_noLobbies() {
        tui.showLobbies(new HashMap<>());
        assertTrue(output().contains("No available lobbies"));
    }

    @Test
    void showLobbies_withLobby() {
        Map<String, Integer> lobbies = new HashMap<>();
        lobbies.put("LOBBY1", 3);
        tui.showLobbies(lobbies);
        assertTrue(output().contains("LOBBY1"));
        assertTrue(output().contains("3"));
    }

    @Test
    void showLobbies_multipleLobbies() {
        Map<String, Integer> lobbies = new HashMap<>();
        lobbies.put("AAA", 2);
        lobbies.put("BBB", 4);
        tui.showLobbies(lobbies);
        String out = output();
        assertTrue(out.contains("AAA"));
        assertTrue(out.contains("BBB"));
    }

    //MENU PHASE - menuCMDs
    @Test
    void menu_helpCommands() throws InterruptedException {
        tui.gPhase = GamePhase.MENU;
        runReaderWithTimeout("-h");
        assertTrue(output().contains("list"));
        assertTrue(output().contains("create"));
        assertTrue(output().contains("join"));
    }

    @Test
    void menu_listCommand() throws Exception {
        tui.gPhase = GamePhase.MENU;
        runReaderWithTimeout("list");
        verify(vView).requestAvailableLobbies();
    }

    @Test
    void menu_invalidCommand() throws InterruptedException {
        tui.gPhase = GamePhase.MENU;
        runReaderWithTimeout("invalidCommand");
        assertTrue(output().contains("Invalid command"));
    }

    @Test
    void menu_commandIsTooLong() throws InterruptedException {
        tui.gPhase = GamePhase.MENU;
        runReaderWithTimeout("1 2 3 4");
        assertTrue(output().contains("Invalid Command"));
    }

    //MATCH PHASE - matchCMDs
    @Test
    void match_gameNotStarted() throws InterruptedException {
        tui.gPhase = GamePhase.GAME;
        tui.isMatchRunning = false;
        runReaderWithTimeout("-h");
        assertTrue(output().contains("[ERROR]: Game hasn't started yet."));
    }

    @Test
    void match_helpCommand() throws InterruptedException {
        tui.gPhase = GamePhase.GAME;
        tui.isMatchRunning = true;
        runReaderWithTimeout("-h");
        assertTrue(output().contains("tile"));
        assertTrue(output().contains("draw up"));
    }

    @Test
    void match_invalidCommand() throws InterruptedException {
        tui.gPhase = GamePhase.GAME;
        tui.isMatchRunning = true;
        runReaderWithTimeout("unknown");
        assertTrue(output().contains("Invalid Command"));
    }

    @Test
    void match_commandIsTooLong() throws InterruptedException {
        tui.gPhase = GamePhase.GAME;
        tui.isMatchRunning = true;
        runReaderWithTimeout("1 2 3 4");
        assertTrue(output().contains("Invalid Command"));
    }

    @Test
    void match_tileIsOutOfBound() throws InterruptedException {
        tui.gPhase = GamePhase.GAME;
        tui.isMatchRunning = true;
        runReaderWithTimeout("tile 66");
        assertTrue(output().contains("Invalid number"));
    }

    //END PHASE - endCMDs
    @Test
    void endScreen_commandIsTooLong() throws InterruptedException {
        tui.gPhase = GamePhase.END_SCREEN;
        runReaderWithTimeout("y invalidCommand");
        assertTrue(output().contains("Invalid Command"));
    }
}

