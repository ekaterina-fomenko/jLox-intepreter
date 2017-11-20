package com.craftinginterpreters.lox.interpreter;

import com.craftinginterpreters.lox.Expr;

/**
 * Interpret expressions after parsing
 */
public class Interpreter implements Expr.Visitor<Object> {

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case MINUS:
                return -(Double) right;
            case BANG:
                return !isTruthy(right);
        }

        // Unreachable.
        return null;
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (Boolean) object;
        return true;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case GREATER:
                return (Double) left > (Double) right;
            case GREATER_EQUAL:
                return (Double) left >= (Double) right;
            case LESS:
                return (Double) left < (Double) right;
            case LESS_EQUAL:
                return (Double) left <= (Double) right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case MINUS:
                return (Double) left - (Double) right;
            case SLASH:
                return (Double) left / (Double) right;
            case STAR:
                return (Double) left * (Double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (Double) left + (Double) right;
                }

                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
        }

        // Unreachable
        return null;
    }

    private boolean isEqual(Object a, Object b) {
        // nil is only equal to nil.
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }
}
