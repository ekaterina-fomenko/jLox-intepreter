package com.craftinginterpreters.lox.parser;

import com.craftinginterpreters.lox.Lox;
import com.craftinginterpreters.lox.interpreter.Expr;
import com.craftinginterpreters.lox.interpreter.Stmt;
import com.craftinginterpreters.lox.tokens.Token;
import com.craftinginterpreters.lox.tokens.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.craftinginterpreters.lox.tokens.TokenType.*;

/**
 * Parse tokens and construct ast tree using recursive descent parser
 */

public class Parser {
    private static class ParseError extends RuntimeException {
    }

    private final List<Token> tokens;
    private int current;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        current = 0;
    }

    /**
     * Initial method for parsing. It also helps to kick off statements in case of error
     *
     * @return parsed statements
     */
    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<Stmt>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    /**
     * Statement rule: stm -> expression ";"| "print" expression ";" | "{" declaration* "}";
     *
     * @return statement
     */
    private Stmt statement() {
        if (match(PRINT)) {
            return printStatement();
        }
        if (match(LEFT_BRACE)) {
            return new Stmt.Block(block());
        }
        if (match(IF)) {
            return ifStatement();
        }
        if (match(WHILE)) {
            return whileStatement();
        }
        if (match(FOR)) {
            return forStatement();
        }
        if (match(RETURN)) {
            return returnStatement();
        }
        return expressionStatement();
    }

    private Stmt returnStatement() {
        Token keyword = previous();
        Expr value = null;
        if (!check(SEMICOLON)) {
            value = expression();
        }
        consume(SEMICOLON, "Expect ';' after return value.");
        return new Stmt.Return(keyword, value);
    }

    private Stmt ifStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) {
            elseBranch = statement();
        }

        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }
        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }


    /**
     * Expression rule : expression → assignment ;
     */
    private Expr expression() {
        return assignment();
    }

    /**
     * assignment → identifier "=" assignment | logic_or ;
     */
    private Expr assignment() {
        Expr expr = or();

        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            } else if (expr instanceof Expr.Get) {
                Expr.Get get = (Expr.Get) expr;
                return new Expr.Set(get.object, get.name, value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    /**
     * logic_or   → logic_and ( "or" logic_and )* ;
     */
    private Expr or() {
        Expr expr = and();

        while (match(OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    /**
     * logic_and  → equality ( "and" equality )* ;
     */

    private Expr and() {
        Expr expr = equality();

        while (match(AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    /**
     * Declaration rule: declaration → funDecl | varDecl | statement ;
     *
     * @return
     */
    private Stmt declaration() {
        try {
            if (match(VAR)) {
                return varDeclaration();
            }
            if (match(FUN)) {
                return function("function", false);
            }
            if (match(CLASS)) {
                return classDeclaration();
            }
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    /**
     * classDecl   → "class" IDENTIFIER "{" function* "}" ;
     */
    private Stmt classDeclaration() {
        Token name = consume(IDENTIFIER, "Expect class name.");
        Expr superclass = null;
        if (match(LESS)) {
            consume(IDENTIFIER, "Expect superclass name.");
            superclass = new Expr.Variable(previous());
        }

        consume(LEFT_BRACE, "Expect '{' before class body.");

        List<Stmt.Function> methods = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            methods.add(function("method", false));
        }

        consume(RIGHT_BRACE, "Expect '}' after class body.");

        return new Stmt.Class(name, superclass, methods);
    }


    /**
     * function → IDENTIFIER "(" parameters? ")" block ;
     */
    private Stmt.Function function(String kind, boolean isAnonymous) {
        Token name = !isAnonymous ? consume(IDENTIFIER, "Expect " + kind + " name.") : null;
        consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 8) {
                    error(peek(), "Cannot have more than 8 parameters.");
                }

                parameters.add(consume(IDENTIFIER, "Expect parameter name."));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");
        consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Stmt> body = block();
        return new Stmt.Function(name, parameters, body);
    }

    /**
     * varDecl → "var" IDENTIFIER ( "=" expression )? ";" ;
     */
    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    /**
     * whileStmt → "while" "(" expression ")" statement ;
     */
    private Stmt whileStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Stmt body = statement();

        return new Stmt.While(condition, body);
    }

    /**
     * forStmt   → "for" "(" ( varDecl | exprStmt | ";" ) expression? ";" expression? ")" statement ;
     */
    private Stmt forStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");
        Stmt initializer;
        if (match(SEMICOLON)) {
            initializer = null;
        } else if (match(VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }
        Expr condition = null;
        if (!check(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after loop condition.");
        Expr increment = null;
        if (!check(RIGHT_PAREN)) {
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expect ')' after for clauses.");
        Stmt body = statement();

        if (increment != null) {
            body = new Stmt.Block(Arrays.asList(
                    body,
                    new Stmt.Expression(increment)));
        }
        if (condition == null) condition = new Expr.Literal(true);
        body = new Stmt.While(condition, body);
        if (initializer != null) {
            body = new Stmt.Block(Arrays.asList(initializer, body));
        }
        return body;
    }

    /**
     * Equality rule : equality → comparison ( ( "!=" | "==" ) comparison )* ;
     *
     * @return
     */
    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType tokenType) {
        if (isAtEnd()) {
            return false;
        }
        return peek().type == tokenType;
    }

    /**
     * Increase current index token
     *
     * @return
     */
    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    /**
     * Comparison rule : comparison → addition ( ( ">" | ">=" | "<" | "<=" ) addition )* ;
     *
     * @return
     */
    private Expr comparison() {
        Expr expr = addition();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = addition();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Addition rule : addition → multiplication ( ( "-" | "+" ) multiplication )* ;
     *
     * @return
     */
    private Expr addition() {
        Expr expr = multiplication();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = multiplication();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Multiplication rule : multiplication → unary ( ( "/" | "*" ) unary )* ;
     *
     * @return
     */
    private Expr multiplication() {
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Unary rule : unary → ( "!" | "-" ) unary | call ;
     *
     * @return
     */
    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return call();
    }

    /**
     * call  → primary ( "(" arguments? ")" )* ;
     */
    private Expr call() {
        Expr expr = primary();
        //Temporary stub
        while (true) {
            if (match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(DOT)) {
                Token name = consume(IDENTIFIER, "Expect property name after '.'.");
                expr = new Expr.Get(expr, name);
            } else {
                //Temporary stub
                break;
            }
        }
        return expr;
    }

    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 8) {
                    error(peek(), "Cannot have more than 8 arguments.");
                }
                arguments.add(expression());
            } while (match(COMMA));
        }

        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");

        return new Expr.Call(callee, paren, arguments);
    }

    /**
     * Primary rule : primary → IDENTIFIER | NUMBER | STRING | "false" | "true" | "nil" | "(" expression ")" ; |THIS
     */
    private Expr primary() {
        if (match(FALSE)) {
            return new Expr.Literal(false);
        }
        if (match(TRUE)) {
            return new Expr.Literal(true);
        }
        if (match(NIL)) {
            return new Expr.Literal(null);
        }

        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(FUN)) {
            Stmt.Function func = function("anonymous function", true);
            return new Expr.Function(null, func.parameters, func.body);
        }

        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        if (match(THIS)) {
            return new Expr.This(previous());
        }
        throw error(peek(), "Expect expression.");
    }

    /**
     * Check If current token match type, then move to the next token
     *
     * @param type    of token that need to check with current token type
     * @param message of error if doesn't match
     * @return Token
     */
    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }
    }
}
