package org.jetbrains.emacs4ij.jelisp.elisp;

import junit.framework.Assert;
import org.jetbrains.emacs4ij.jelisp.Environment;
import org.jetbrains.emacs4ij.jelisp.Parser;
import org.jetbrains.emacs4ij.jelisp.exception.LispException;
import org.jetbrains.emacs4ij.jelisp.exception.WrongTypeArgument;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 9/26/11
 * Time: 4:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuiltinsCoreTest {
    private Environment environment;

    @Before
    public void setUp() throws Exception {
        Environment.ourEmacsPath = "/usr/share/emacs/23.2";
        Environment global = new Environment();
        environment = new Environment(global);
    }

    private LObject evaluateString (String lispCode) throws LispException {
        Parser parser = new Parser();
        return parser.parseLine(lispCode).evaluate(environment);
    }

    @Test
    public void testPlusInteger() throws LispException {
        LObject lispObject = evaluateString("(+ 2 2)");
        Assert.assertEquals(new LispInteger(4), lispObject);
    }

    @Test
    public void testPlusFloat () {
        LObject lispObject = evaluateString("(+ 2 2.0)");
        Assert.assertEquals(new LispFloat(4), lispObject);
    }

    @Test
    public void testPlusSimple () {
        LispNumber n = BuiltinsCore.plus(new LispInteger(5), new LispFloat(6.6));
        Assert.assertEquals(new LispFloat(11.6), n);
    }

    @Test
    public void testMultiplySimple () {
        LispNumber n = BuiltinsCore.multiply(new LispInteger(5), new LispFloat(2.0));
        Assert.assertEquals(new LispFloat(10), n);
    }

    @Test
    public void testPlusEmpty () {
        LObject lispObject = evaluateString("(+)");
        Assert.assertEquals(new LispInteger(0), lispObject);

    }

    @Test
    public void testMultiply() throws Exception {
        LObject LObject = evaluateString("(* 2 2)");
        Assert.assertEquals(new LispInteger(4), LObject);
    }

    @Test
    public void testSetVar() throws LispException {
        LObject value = evaluateString("(set 'var (+ 2 3))");
        Assert.assertEquals("set return value assertion", new LispInteger(5), value);
        LObject lispObject = evaluateString("var");
        Assert.assertEquals(new LispInteger(5), lispObject);
    }

    @Test
    public void testSetBindings() {
        LObject lispObject = evaluateString("(set 'one 1)");
        Assert.assertEquals(new LispInteger(1), lispObject);
        lispObject = evaluateString("(set 'two 'one)");
        Assert.assertEquals(new LispSymbol("one"), lispObject);
        lispObject = evaluateString("(set two 2)");
        Assert.assertEquals(new LispInteger(2), lispObject);
        lispObject = evaluateString("one");
        Assert.assertEquals(new LispInteger(2), lispObject);
        lispObject = evaluateString("(let ((one 1)) (set 'one 3) one)");
        Assert.assertEquals(new LispInteger(3), lispObject);
        lispObject = evaluateString("one");
        Assert.assertEquals(new LispInteger(2), lispObject);
    }

    @Test (expected = WrongTypeArgument.class)
    public void testSetSymbols() {
        LObject lispObject = evaluateString("(set 'x 1)");
        Assert.assertEquals(new LispInteger(1), lispObject);
        lispObject = evaluateString("(set 'y 'x)");
        Assert.assertEquals(new LispSymbol("x"), lispObject);
        lispObject = evaluateString("y");
        LispSymbol x = new LispSymbol("x", new LispInteger(1));
        Assert.assertEquals(x, lispObject);

        lispObject = evaluateString("(symbol-value y)");
        Assert.assertEquals(new LispInteger(1), lispObject);
        lispObject = evaluateString("(symbol-value 'y)");
        Assert.assertEquals(new LispSymbol("x"), lispObject);
        //must throw WrongTypeArgument
        evaluateString("(symbol-value x)");
    }

    @Test
    public void testEq() {
        LObject lispObject = evaluateString("(eq 5 5)");
        Assert.assertEquals(LispSymbol.ourT, lispObject);
        lispObject = evaluateString("(eq 'foo 'foo)");
        Assert.assertEquals(LispSymbol.ourT, lispObject);
        lispObject = evaluateString("(eq \"qwa\" \"qwa\")");
        Assert.assertEquals(LispSymbol.ourNil, lispObject);
        lispObject = evaluateString("(eq \"\" \"\")");
        Assert.assertEquals(LispSymbol.ourT, lispObject);
        lispObject = evaluateString("(eq '(1 (2 (3))) '(1 (2 (3))))");
        Assert.assertEquals(LispSymbol.ourNil, lispObject);
        evaluateString("(setq foo '(1 (2 (3))))");
        lispObject = evaluateString("(eq foo foo)");
        Assert.assertEquals(LispSymbol.ourT, lispObject);
        lispObject = evaluateString("(eq foo '(1 (2 (3))))");
        Assert.assertEquals(LispSymbol.ourNil, lispObject);
        //todo: (eq [(1 2) 3] [(1 2) 3]) ⇒ nil
        //todo: (eq (make-symbol "foo") 'foo) ⇒ nil
    }

    @Test
    public void testEqual() {
        LObject lispObject = evaluateString("(equal 5 5)");
        Assert.assertEquals(LispSymbol.ourT, lispObject);
        lispObject = evaluateString("(equal 'foo 'foo)");
        Assert.assertEquals(LispSymbol.ourT, lispObject);
        lispObject = evaluateString("(equal \"qwa\" \"qwa\")");
        Assert.assertEquals(LispSymbol.ourT, lispObject);
        lispObject = evaluateString("(equal \"qwa\" \"QWA\")");
        Assert.assertEquals(LispSymbol.ourNil, lispObject);
        lispObject = evaluateString("(equal \"\" \"\")");
        Assert.assertEquals(LispSymbol.ourT, lispObject);
        lispObject = evaluateString("(equal '(1 (2 (3))) '(1 (2 (3))))");
        Assert.assertEquals(LispSymbol.ourT, lispObject);
        evaluateString("(setq foo '(1 (2 (3))))");
        lispObject = evaluateString("(equal foo foo)");
        Assert.assertEquals(LispSymbol.ourT, lispObject);
        lispObject = evaluateString("(equal foo '(1 (2 (3))))");
        Assert.assertEquals(LispSymbol.ourT, lispObject);
        //todo: (equal [(1 2) 3] [(1 2) 3]) ⇒ t
    }

    @Test
    public void testNull () {
        LObject lispObject = evaluateString("(null 5)");
        Assert.assertEquals(LispSymbol.ourNil, lispObject);
        lispObject = evaluateString("(null nil)");
        Assert.assertEquals(LispSymbol.ourT, lispObject);
    }

    @Test
    public void testLispNot() throws Exception {
        LObject lispObject = evaluateString("(not 5)");
        Assert.assertEquals(LispSymbol.ourNil, lispObject);
        lispObject = evaluateString("(not nil)");
        Assert.assertEquals(LispSymbol.ourT, lispObject);
    }

}
