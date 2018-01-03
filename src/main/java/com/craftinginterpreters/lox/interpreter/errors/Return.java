package com.craftinginterpreters.lox.interpreter.errors;

/**
 * When we execute a return statement, we’ll use an exception to unwind the interpreter past the visit methods of all of the containing statements back to the code that began executing the body
 */
public class Return extends RuntimeException{
    public final Object value;

    public Return(Object value) {
        super(null, null, false, false);
        this.value = value;
    }
}
