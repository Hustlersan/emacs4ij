package org.jetbrains.emacs4ij.jelisp.exception;

import org.jetbrains.emacs4ij.jelisp.JelispBundle;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 3/29/12
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class MarkerPointsNowhereException extends LispException {
    public MarkerPointsNowhereException() {
        super(JelispBundle.message("empty.marker.error"));
    }
}
