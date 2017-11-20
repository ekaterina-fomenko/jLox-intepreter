package com.craftinginterpreters.lox;

import com.craftinginterpreters.lox.interpreter.Interpreter;
import com.craftinginterpreters.lox.interpreter.errors.RuntimeError;
import com.craftinginterpreters.lox.parser.Parser;
import com.craftinginterpreters.lox.scanner.Scanner;
import com.craftinginterpreters.lox.tokens.Token;
import com.craftinginterpreters.lox.tokens.TokenType;

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
    private static volatile boolean hadRuntimeError = false;
    public static final int ERROR_CODE = 65;
    private static final int RUNTIME_ERROR_CODE = 70;
    private static final Interpreter interpreter = new Interpreter();

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
        if (hadError) {
            System.exit(ERROR_CODE);
        }
        if (hadRuntimeError){
            System.exit(RUNTIME_ERROR_CODE);
        }
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
        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        // Stop if there was a syntax error.
        if (hadError) {
            return;
        }
        interpreter.interpret(expression);
    }

    /**
     * Error handling
     *
     * @param line    of error
     * @param message error
     */
    public static void error(int line, String message) {
        report(line, "", message);
    }

    /**
     * Error handling
     *
     * @param token   with error
     * @param message error
     */
    public static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    /*
    *Construct report about the error
    */
    static private void report(int line, String where, String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }

}
