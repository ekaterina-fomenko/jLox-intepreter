package com.craftinginterpreters.lox;

import com.craftinginterpreters.lox.tokens.Token;

public abstract class ExprTest {

    public static class Binary extends ExprTest {
        Binary(ExprTest left, Token operator, ExprTest right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        final ExprTest left;
        final Token operator;
        final ExprTest right;
    }

    // Todo:Add Other expressions
}
