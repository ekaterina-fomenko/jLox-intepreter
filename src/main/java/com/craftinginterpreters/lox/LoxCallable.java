package com.craftinginterpreters.lox;

import com.craftinginterpreters.lox.interpreter.Interpreter;

import java.util.List;

public interface LoxCallable {

    //Number of arguments that function expects
    int arity();

    Object call(Interpreter interpreter, List<Object> arguments);
}
