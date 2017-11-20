package com.craftinginterpreters.lox;

import com.craftinginterpreters.lox.interpreter.Expr;
import com.craftinginterpreters.lox.tokens.Token;
import com.craftinginterpreters.lox.tokens.TokenType;
import com.craftinginterpreters.lox.tools.AstPrinter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AstPrinterTest {
    private AstPrinter astPrinter;

    @Before
    public void setup() {
        astPrinter = new AstPrinter();
    }

    @Test
    public void testPrint() {
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(123)),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Literal(45.67)));
        String expectedResult = "(* (- 123) (group 45.67))";
        String actualResult = astPrinter.print(expression);
        assertEquals(expectedResult, actualResult);
    }
}
