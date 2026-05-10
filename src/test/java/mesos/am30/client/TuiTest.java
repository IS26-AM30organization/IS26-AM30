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
    void askNickname() {
        String input = "Alice\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        tui = new Tui(); //tui needs to be re-created as to read new Sys.in

        String name = tui.askNickname();

        assertEquals("Alice", name);
        assertTrue(streamOut.toString().contains("Inserisci nickname >"));
    }

    @Test
    void askPlayersNumber() {
        String in = "3\n";
        System.setIn(new ByteArrayInputStream(in.getBytes()));
        tui.actionScanner = new Scanner(System.in);

        int num = tui.askPlayersNumber();

        assertEquals(3, num);
        assertTrue(streamOut.toString().contains("Inserisci playerNum >"));
    }

    @Test
    void askPlayersInvalid() {
        String input = "abc\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        tui.actionScanner = new Scanner(System.in);

        int num = tui.askPlayersNumber();

        assertEquals(0, num);
        assertTrue(streamOut.toString().contains("[ERROR]: invalid"));
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
    void printErrorNum() throws Exception {
        VirtualView vViewMock = mock(VirtualView.class);

        //Reflection to inject a fake view
        Field vViewField = Tui.class.getDeclaredField("vView");
        vViewField.setAccessible(true);
        vViewField.set(tui, vViewMock);

        Tui spyTui = spy(tui);
        spyTui.printError(ErrorType.WRONG_PLAYERS_NUMBER);

        verify(spyTui).promptPlayerNumber();
        verify(vViewMock).askPlayersNumber();
    }

    @Test
    void printErrorName() throws Exception {
        VirtualView vViewMock = mock(VirtualView.class);

        //Reflection to inject a fake view
        Field vViewField = Tui.class.getDeclaredField("vView");
        vViewField.setAccessible(true);
        vViewField.set(tui, vViewMock);

        Tui spyTui = spy(tui);
        spyTui.printError(ErrorType.WRONG_NICKNAME);

        verify(spyTui).promptPlayerNickname();
        verify(vViewMock).askNickname();
    }
}
