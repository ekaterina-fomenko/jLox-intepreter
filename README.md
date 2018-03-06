# JLox interpreter

Interpreter for Lox language https://github.com/munificent/craftinginterpreters.

These are learning tasks from the book http://www.craftinginterpreters.com/.

**Compile and run**:

Requires: Maven and Java (1.8)

1) *Compile:* mvn install

2) *Run:*
* mvn exec:java  -> then type in console what you want to intepreterted(using Lox language) -> press Enter

*Or*

* mvn exec:java -Dexec.args=[path to your file]

You can find expamples in test/resources

**In interpreter were implemented** :

* tokens and lexing
* abstract syntax trees
* recursive descent parsing
* prefix and infix expressions
* runtime representation of objects
* interpreting code using the Visitor pattern
* lexical scope
* environment chains for storing variables
* control flow
* functions with parameters
* closures
* static variable resolution and error detection
* classes
* constructors
* fields
* methods
* inheritance
