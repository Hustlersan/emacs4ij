package org.jetbrains.emacs4ij.jelisp;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.emacs4ij.jelisp.elisp.*;
import org.jetbrains.emacs4ij.jelisp.exception.DoubleBufferException;
import org.jetbrains.emacs4ij.jelisp.exception.NoBufferException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/**
 * Created by IntelliJ IDEA.
 * User: Ekaterina.Polishchuk
 * Date: 7/11/11
 * Time: 5:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class Environment {
    private HashMap<String, LispSymbol> mySymbols = new HashMap<String, LispSymbol>();
    private ArrayList<LispBuffer> myBuffers = new ArrayList<LispBuffer>();
    private Environment myOuterEnv;

    private boolean selectionManagedBySubroutine = false;

    public static String ourEmacsPath = "";
    private final Environment myGlobalEnvironment;

    /**
     * Constructor for global environment
     */
    public Environment() {
        myGlobalEnvironment = this;
        setGlobal();
    }

    public Environment (@NotNull final Environment outerEnv) {
        myOuterEnv = outerEnv;
        myGlobalEnvironment = outerEnv.getGlobalEnvironment();
    }

    public boolean isSelectionManagedBySubroutine() {
        return selectionManagedBySubroutine;
    }

    public void setSelectionManagedBySubroutine(boolean selectionManagedBySubroutine) {
        this.selectionManagedBySubroutine = selectionManagedBySubroutine;
    }

    public Environment getGlobalEnvironment() {
        return myGlobalEnvironment;
    }

    private void setSubroutines (Class[] subroutineContainers, LispSymbol.FunctionType type) {
        for (Class subroutineContainer: subroutineContainers) {
            Method[] methods = subroutineContainer.getMethods();
            for (Method m: methods) {
                Subroutine annotation = m.getAnnotation(Subroutine.class);
                if (annotation == null)
                    continue;
                String name = annotation.value();
                mySymbols.put(name, new LispSymbol(name, type));
            }
        }
    }

    private void setGlobal() {
        Class[] lispBuiltIns = LispSubroutine.getBuiltInsClasses();
        setSubroutines(lispBuiltIns, LispSymbol.FunctionType.BuiltIn);
        Class[] lispSpecialForms = LispSubroutine.getSpecialFormsClasses();
        setSubroutines(lispSpecialForms, LispSymbol.FunctionType.SpecialForm);
       // mySymbols.put("*scratch*",  new LispSymbol("*scratch*", new LispBuffer("*scratch*")));
    }

    /*private void indexEmacsSources() {
        if (ourEmacsPath.equals("")) {
            ourEmacsPath = "c:\\Users\\ekaterina.polishchuk\\Downloads\\emacs-23.3";
            //TODO:
            //throw new RuntimeException("Emacs path is not set!");
        }
        indexDirectory(ourEmacsPath + "\\lisp");
    }

    private void indexDirectory (String dir) {
        File[] fList = new File(dir).listFiles();
        for (File f: fList) {
            if (f.isDirectory())
                indexDirectory(f.getAbsolutePath());
            else if (f.getName().substring(f.getName().lastIndexOf('.'), f.getName().length()).equals("el")) {

                //TODO: parse and save
            }
        }


    }   */

    public LObject find(String name, String methodName) {
        return find(name, methodName, null);
    }

    public LispSymbol find(String name) {
        return (LispSymbol) find(name, "", null);
    }

    public LObject find(String name, String methodName, Class[] parameterTypes, Object... methodParameters) {
        LispSymbol lispObject = mySymbols.get(name);
        if (lispObject != null)
            return lispObject.invokeMethod(methodName, parameterTypes, methodParameters);
        if (myOuterEnv != null) {
            return myOuterEnv.find(name, methodName, parameterTypes, methodParameters);
        }
        return null;
    }

    public void defineSymbol (LispSymbol symbol) {
        mySymbols.put(symbol.getName(), symbol);
    }

    //============================= buffer processing =====================================

    public void defineBuffer (LispBuffer buffer) {
        if (getIndexByName(buffer.getName()) != -1) {
            throw new DoubleBufferException("double "+buffer.getName());
        }
        myBuffers.add(buffer);
    }

    public LispBuffer getCurrentBuffer () {
        if (myBuffers.size() == 0)
            throw new EnvironmentException("no buffer is currently opened");
        return myBuffers.get(myBuffers.size() - 1);
    }

    private int getIndexByName(String bufferName) {
        for (int i=0; i!= myBuffers.size(); ++i) {
            if (myBuffers.get(i).getName().equals(bufferName))
                return i;
        }
        return -1;
    }

    public void setCurrentBuffer (String bufferName) {
        if (myBuffers.size() == 0)
            throw new EnvironmentException("no buffer is currently opened");
        if (myBuffers.get(myBuffers.size() - 1).getName().equals(bufferName))
            return;
        int newCurrentBufferIndex = getIndexByName(bufferName);
        if (newCurrentBufferIndex == -1)
            throw new EnvironmentException("this buffer is not opened");
        Collections.rotate(myBuffers.subList(newCurrentBufferIndex, myBuffers.size()), -1);
    }

    public LObject getBufferByName(String bufferName) {
        for (int i=0; i!= myBuffers.size(); ++i) {
            if (myBuffers.get(i).getName().equals(bufferName))
                return myBuffers.get(i);
        }
        return LispSymbol.ourNil;
    }

   /* public LObject getBuffer (String bufferName) {
        return getBufferByName(bufferName);
    }                   */

    public LispBuffer getOtherBuffer () {
        if (myBuffers.size() < 2)
            return getCurrentBuffer();
        return myBuffers.get(myBuffers.size() - 2);
    }

    public LispBuffer getOtherBuffer (String bufferName) {
        if (myBuffers.size() == 0)
            throw new RuntimeException("no buffer is currently opened");
        for (int i = myBuffers.size() - 1; i!=-1; --i) {
            if (!myBuffers.get(i).getName().equals(bufferName))
                return myBuffers.get(i);
        }
        throw new NoBufferException(bufferName);
    }

    public int getBuffersSize() {
        return myBuffers.size();
    }

    public void closeBuffer(String bufferName) {
        int toRemove = getIndexByName(bufferName);
        myBuffers.remove(toRemove);
    }

    public void closeCurrentBuffer () {
        myBuffers.remove(getCurrentBuffer());
    }


    public void closeAllBuffers () {
        myBuffers.clear();
    }

    public LispBuffer getBufferByIndex (int index) {
        return myBuffers.get(index);
    }

    public void printBuffers() {
        for (int i=0; i!= myBuffers.size(); ++i) {
            System.out.print(myBuffers.get(i).getName()+"; ");
        }
        System.out.println();
    }

    public String[] getBuffersNames () {
        String[] buffersNames = new String[getBuffersSize()];
        for (int i=0; i!=getBuffersSize(); ++i) {
            buffersNames[i] = myBuffers.get(i).getName();
        }
        return buffersNames;
    }

}
