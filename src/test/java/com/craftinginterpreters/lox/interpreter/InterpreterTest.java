package com.craftinginterpreters.lox.interpreter;

import com.craftinginterpreters.lox.interpreter.errors.RuntimeError;
import com.craftinginterpreters.lox.tokens.Token;
import com.craftinginterpreters.lox.tokens.TokenType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InterpreterTest {
    private Interpreter interpreter;

    @Before
    public void setUp() {
        interpreter = new Interpreter();
    }

    @Test
    public void stringPlusNumberTest() {
        Expr.Binary expression = new Expr.Binary(
                new Expr.Literal("word"),
                new Token(TokenType.PLUS, "+", null, 1),
                new Expr.Literal(45.67));

        Object result = interpreter.visitBinaryExpr(expression);
        String expectedResult = "word45.67";
        assertTrue(result instanceof String);
        assertEquals(expectedResult, result);
    }

    @Test
    public void numberPlusStringTest() {
        Expr.Binary expression = new Expr.Binary(
                new Expr.Literal(1.2),
                new Token(TokenType.PLUS, "+", null, 1),
                new Expr.Literal("45.67"));

        Object result = interpreter.visitBinaryExpr(expression);
        String expectedResult = "1.245.67";
        assertTrue(result instanceof String);
        assertEquals(expectedResult, result);
    }

    @Test(expected = RuntimeError.class)
    public void divisionTest() {
        Expr.Binary expression = new Expr.Binary(
                new Expr.Literal(5.0),
                new Token(TokenType.SLASH, "/", null, 1),
                new Expr.Literal(0.0));

        interpreter.visitBinaryExpr(expression);
    }
}
