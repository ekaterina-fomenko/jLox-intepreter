package com.craftinginterpreters.lox.model;

import com.craftinginterpreters.lox.interpreter.errors.RuntimeError;
import com.craftinginterpreters.lox.tokens.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * This class helps to store bindings that associate variables
 */
public class Environment {
    /**
     * The scope of variable
     */
    public final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    /**
     * This constructor for the local scope's env-t
     */
    public Environment() {
        enclosing = null;
    }

    /**
     * This constructor for the global scope's env-t
     *
     * @param enclosing
     */
    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }
        if (enclosing != null) {
            return enclosing.get(name);
        }
        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }

    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");

    }

    public Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }
}
