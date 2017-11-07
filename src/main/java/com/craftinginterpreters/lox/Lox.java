package com.craftinginterpreters.lox;

import com.craftinginterpreters.lox.scanner.Scanner;
import com.craftinginterpreters.lox.tokens.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main class if jLox  interpreter.
 */

public class Lox {
    private static volatile boolean hadError = false;
    public static final int EXIT_CODE = 65;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    /*
    * Run Lox file
    */
    public static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) System.exit(EXIT_CODE);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (; ; ) {
            System.out.print("> ");
            run(reader.readLine());
            hadError = false;
        }
    }

    /*
    * Parse text from Lox file
     */
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    /*
    * Handle errors
    */
    static void error(int line, String message) {
        report(line, "", message);
    }

    /*
    *Construct report about the error
    */
    static private void report(int line, String where, String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

}
