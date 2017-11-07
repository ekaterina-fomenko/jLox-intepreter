package com.craftinginterpreters.lox.scanner;

import com.craftinginterpreters.lox.tokens.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Scanning initial text and give tokens
 */
public class Scanner {
    private final String source;

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        return new ArrayList<Token>();
    }
}
