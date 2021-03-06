package org.jetbrains.emacs4ij.jelisp.exception;

import org.jetbrains.emacs4ij.jelisp.JelispBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Ekaterina.Polishchuk
 * Date: 8/5/11
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class DoubleBufferException extends RuntimeException {
    public DoubleBufferException (String name) {
        super(JelispBundle.message("double.buffer", name));
    }
}
