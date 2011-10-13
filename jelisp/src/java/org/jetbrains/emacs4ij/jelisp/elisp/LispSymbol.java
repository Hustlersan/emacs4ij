package org.jetbrains.emacs4ij.jelisp.elisp;

import org.jetbrains.emacs4ij.jelisp.Environment;
import org.jetbrains.emacs4ij.jelisp.exception.VoidVariableException;

import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ekaterina.Polishchuk
 * Date: 7/11/11
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 *
 * elisp symbol = variable name, function name, constant name, special form name, etc
 */
public class LispSymbol extends LispAtom {
    public static final LispSymbol ourNil = new LispSymbol("nil");
    public static final LispSymbol ourT = new LispSymbol("t");
    public static final LispSymbol ourVoid = new LispSymbol("void");

    private String myName = null;
    private LObject myValue = ourVoid;
    private LispObject myFunction = ourVoid;
    private HashMap<LispSymbol, LispObject> myProperties = new HashMap<LispSymbol, LispObject>();


    public LispSymbol(String myName) {
        this.myName = myName;
        myFunction = ourVoid;
    }

    public static LispSymbol newSubroutine (String myName) {
        LispSymbol subroutine = new LispSymbol(myName);
        subroutine.myFunction = new LispString("#<subr " + myName + ">");
        return subroutine;
    }

    public LispSymbol (String myName, LObject value) {
        this.myName = myName;
        myValue = value;
        myFunction = ourVoid;
    }

    public String getName() {
        return myName;
    }

    public LObject getValue() {
        return myValue;
    }

    public void setValue(LObject myValue) {
        this.myValue = myValue;
    }

    public LispObject getFunction() {
        return myFunction;
    }

    public void castToLambda (Environment environment) {
        myFunction = new Lambda((LispList) myFunction, environment);
    }

    public void setFunction(LispObject myFunction) {
        this.myFunction = myFunction;
    }

    @Override
    public String toString() {
        if (myFunction == null || myFunction.equals(ourVoid))
            return myName;
        if (isSubroutine())
            return "#<subr " + myName + '>';
        return myFunction.toString();
    }

    public boolean isSubroutine () {
        return (myFunction instanceof LispString);
    }

    public boolean isCustom() {
        return ((myFunction instanceof LispList) || (myFunction instanceof Lambda));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LispSymbol that = (LispSymbol) o;

        return !(myName != null ? !myName.equals(that.myName) : that.myName != null);

    }

    @Override
    public int hashCode() {
        int result = myName != null ? myName.hashCode() : 0;
        result = 31 * result + (myValue != null ? myValue.hashCode() : 0);
        result = 31 * result + (myFunction != null ? myFunction.hashCode() : 0);
        result = 31 * result + (myProperties != null ? myProperties.hashCode() : 0);
        return result;
    }

    @Override
    /**
     * takes Environment
     */
    public LObject evaluate(Environment environment) {
        LObject lispObject = environment.find(myName, "getValue");
        if (lispObject == null || lispObject.equals(LispSymbol.ourVoid))
            throw new VoidVariableException(myName);
        return lispObject;
    }

    public LObject evaluateFunction (Environment environment, List<LObject> args) {
        if (isSubroutine())
            return LispSubroutine.evaluate(this, environment, args);

        for (int i = 0, dataSize = args.size(); i < dataSize; i++) {
            args.set(i, args.get(i).evaluate(environment));
        }
        if (!(myFunction instanceof Lambda)) {
            myFunction = new Lambda((LispList)myFunction, environment);
        }
        return ((Lambda)myFunction).evaluate(environment, args);
    }

    public LispObject getPropertyList() {
        LispList pList = new LispList();
        for (LispSymbol key: myProperties.keySet())
            pList.add(new LispList(key, myProperties.get(key)));
        return pList;
    }

    public LispObject getProperty (String pName) {
        return getProperty(new LispSymbol(pName));
    }

    public LispObject getProperty(LispSymbol pName) {
        if (myProperties.containsKey(pName))
            return myProperties.get(pName);
        return LispSymbol.ourNil;
    }

    public void setProperty(LispSymbol key, LispObject value) {
        myProperties.put(key, value);
    }

    public void setProperty(String keyName, LispObject value) {
        myProperties.put(new LispSymbol(keyName), value);
    }

    public LispObject getVariableDocumentation () {
        return getProperty("variable-documentation");
    }

    public void setVariableDocumentation (LispString value) {
        setProperty("variable-documentation", value);
    }
}
