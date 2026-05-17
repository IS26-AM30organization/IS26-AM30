package mesos.am30.client;

import mesos.am30.common.ErrorType;
import mesos.am30.common.Move;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TuiTest {
    private Tui tui;
    @Mock
    private ViewModel vBoard;
    private ByteArrayOutputStream streamOut;
    private final InputStream rstIn = System.in;

    @BeforeEach
    void setUp() {
        streamOut = new ByteArrayOutputStream(); //different output stream must be used
        System.setOut(new PrintStream(streamOut));

        tui = new Tui();
        tui.refresh(vBoard); //needed to reset tui with vBoard mock
    }

    @AfterEach
    void tearDown() {
        System.setIn(rstIn); //sysstem input is reset
    }

    @Test
    void askNickname() throws IOException {
        tui.askNickname();
        String output = streamOut.toString();

        assertTrue(output.contains("Insert nickname: "));
    }

    @Test
    void printMove() {
        tui.printMove("Alice", Move.PICK_TILE);
        String output = streamOut.toString();

        assertTrue(output.contains("PICK_TILE"));
        assertTrue(output.contains("Alice"));
    }

    @Test
    void printError() {
        tui.printError(ErrorType.NOT_YOUR_TURN);
        String output = streamOut.toString();

        assertTrue(output.contains("NOT_YOUR_TURN"));
    }

    @Test
    void printErrorName() throws Exception {
        tui.printError(ErrorType.WRONG_NICKNAME);
        String output = streamOut.toString();

        assertTrue(output.contains("Insert nickname: "));
    }

    @Test
    void printErrorPlNum() throws Exception {
        tui.printError(ErrorType.WRONG_PLAYERS_NUMBER);
        String output = streamOut.toString();

        assertTrue(output.contains("Type: create #plNum, to create a lobby."));
    }
}
