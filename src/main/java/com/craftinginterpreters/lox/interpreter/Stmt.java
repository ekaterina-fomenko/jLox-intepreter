package com.craftinginterpreters.lox.interpreter;

import com.craftinginterpreters.lox.tokens.Token;

import java.util.List;

/**
 * Generated by GenerateAst.java class
 */

public abstract class Stmt {
    interface Visitor<R> {
        public R visitExpressionStmt(Expression stmt);

        public R visitPrintStmt(Print stmt);

        public R visitVarStmt(Var stmt);

        public R visitBlockStmt(Block stmt);

        public R visitIfStmt(If stmt);

        public R visitWhileStmt(While stmt);

        public R visitFunctionStmt(Function stmt);
    }

    /**
     * exprStmt  → expression ";" ;
     */
    public static class Expression extends Stmt {
        public Expression(Expr expression) {
            this.expression = expression;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        final Expr expression;
    }

    /**
     * printStmt → "print" expression ";" ;
     */
    public static class Print extends Stmt {
        public Print(Expr expression) {
            this.expression = expression;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        final Expr expression;
    }

    /**
     * varDecl → "var" IDENTIFIER ( "=" expression )? ";" ;
     */
    public static class Var extends Stmt {
        public Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }

        final Token name;
        final Expr initializer;
    }

    /**
     * block     → "{" declaration* "}" ;
     */
    public static class Block extends Stmt {
        public Block(List<Stmt> statements) {
            this.statements = statements;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

        final List<Stmt> statements;
    }

    /**
     * ifStmt    → "if" "(" expression ")" statement ( "else" statement )? ;
     */
    public static class If extends Stmt {
        public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        final Expr condition;
        final Stmt thenBranch;
        final Stmt elseBranch;
    }

    public static class While extends Stmt {
        public While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }

        final Expr condition;
        final Stmt body;
    }

    public static class Function extends Stmt {
        public Function(Token name, List<Token> parameters, List<Stmt> body) {
            this.name = name;
            this.parameters = parameters;
            this.body = body;
        }

        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }

        public final Token name;
        public final List<Token> parameters;
        public final List<Stmt> body;
    }

    abstract <R> R accept(Visitor<R> visitor);
}
