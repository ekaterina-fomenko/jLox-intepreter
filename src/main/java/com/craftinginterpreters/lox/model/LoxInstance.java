package com.craftinginterpreters.lox.model;

/**
 * For creating new instances of classes
 */
public class LoxInstance {
    private LoxClass klass;

    LoxInstance(LoxClass klass) {
        this.klass = klass;
    }
    @Override
    public String toString() {
        return klass.name + " instance";
    }
}
