package com.craftinginterpreters.lox.tools;

import com.craftinginterpreters.lox.Lox;
import com.craftinginterpreters.lox.interpreter.Expr;
import com.craftinginterpreters.lox.interpreter.Interpreter;
import com.craftinginterpreters.lox.interpreter.Stmt;
import com.craftinginterpreters.lox.tokens.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Is used for variable resolution
 */
public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    private void resolve(Expr initializer) {
        //Todo: write entry
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        return null;
    }

    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        return null;
    }

    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        return null;
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!scopes.isEmpty() &&
                scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
            Lox.error(expr.name,
                    "Cannot read local variable in its own initializer.");
        }

        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitCallExpr(Expr.Call expr) {
        return null;
    }

    @Override
    public Void visitFunctionExpr(Expr.Function expr) {
        return null;
    }

    public void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
        scopes.pop();
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) {
            return;
        }

        Map<String, Boolean> scope = scopes.peek();
        scope.put(name.lexeme, false);
    }

    private void define(Token name) {
        if (scopes.isEmpty()) {
            return;
        }
        scopes.peek().put(name.lexeme, true);
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }

        // Not found. Assume it is global.
    }
}
