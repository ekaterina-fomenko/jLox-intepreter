package com.craftinginterpreters.lox;

import com.craftinginterpreters.lox.interpreter.errors.RuntimeError;
import com.craftinginterpreters.lox.tokens.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * This class helps to store bindings that associate variables
 */
public class Environment {
    private final Map<String, Object> values = new HashMap<>();

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }
}
