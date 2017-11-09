package com.craftinginterpreters.lox.tokens;

/**
 * Describe Tokens in which text should be separated
 */
public class Token {
    public final TokenType type;
    public final String lexeme;
    public final Object literal;
    public final int line;

    /**
     * Create token
     * @param type describe TokenType
     * @param lexeme describe token as a word
     * @param literal
     * @param line describe line number
     */
    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
