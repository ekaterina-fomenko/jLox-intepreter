package com.craftinginterpreters.lox.scanner;

import com.craftinginterpreters.lox.tokens.Token;
import com.craftinginterpreters.lox.tokens.TokenType;

import java.util.ArrayList;
import java.util.List;

import static com.craftinginterpreters.lox.tokens.TokenType.*;

/**
 * Scanning initial text and give tokens
 */
public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<Token>();
    private int start;
    private int current;
    private int line;


    public Scanner(String source) {
        this.source = source;

        //the first char in current lexeme
        this.start = 0;

        //current char position
        this.current = 0;

        //Current line
        this.line = 1;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken(){
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
        }
    }

    /**
     * Consume the next char in source
     * @return the next char in source file
     */
    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Create new token for current text
     * @param type type of Token
     * @param literal
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
