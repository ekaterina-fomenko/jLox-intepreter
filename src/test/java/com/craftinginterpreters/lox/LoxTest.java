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
    public void mainTest() throws IOException {
        String[] args = {FILES_PATH + "simpleLoxInput.lox"};
        Lox.main(args);
        byte[] bytes = Files.readAllBytes(Paths.get(FILES_PATH + "simpleLoxOutput.lox"));
        String expected = new String(bytes, Charset.defaultCharset());
        assertEquals(expected, outContent.toString());
    }
}
