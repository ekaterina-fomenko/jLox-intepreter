package com.craftinginterpreters.lox.interpreter.errors;

import com.craftinginterpreters.lox.tokens.Token;

/**
 * Describe runtime error that was occurred during execution
 */
public class RuntimeError extends RuntimeException {
    final Token token;

    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
