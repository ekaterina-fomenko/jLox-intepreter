package com.craftinginterpreters.lox;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class LoxTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    public static final String FILES_PATH = "src/test/resources/";

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }

    @Test
    public void mainSimpleTest() throws IOException {
        run("simpleLoxInput.lox", "simpleLoxOutput.lox");
    }

    @Test
    public void mainScopeTest() throws IOException {
        run("scopeInput.lox", "scopeOutput.lox");
    }

    @Test
    public void mainLogicalOperationsTest() throws IOException {
        run("logicOperationsInput.lox", "logicOperationsOutput.lox");
    }

    @Test
    public void mainLoopsTest() throws IOException {
        run("loopsInput.lox", "loopsOutput.lox");
    }

    @Test
    public void mainFunctionsTest() throws IOException {
        run("functionsInput.lox", "functionsOutput.lox");
    }

    private void run(String fileNameInput, String fileNameOutput) throws IOException {
        String[] args = {FILES_PATH + fileNameInput};
        Lox.main(args);
        byte[] bytes = Files.readAllBytes(Paths.get(FILES_PATH + fileNameOutput));
        String expected = new String(bytes, Charset.defaultCharset());
        assertEquals(expected, outContent.toString());
    }
}
