package org.jetbrains.emacs4ij.jelisp.subroutine;

import org.jetbrains.emacs4ij.jelisp.Environment;
import org.jetbrains.emacs4ij.jelisp.GlobalEnvironment;
import org.jetbrains.emacs4ij.jelisp.elisp.LispInteger;
import org.jetbrains.emacs4ij.jelisp.elisp.LispList;
import org.jetbrains.emacs4ij.jelisp.elisp.LispMarker;
import org.jetbrains.emacs4ij.jelisp.elisp.LispObject;
import org.jetbrains.emacs4ij.jelisp.elisp.LispSymbol;
import org.jetbrains.emacs4ij.jelisp.elisp.MarkerOrInteger;
import org.jetbrains.emacs4ij.jelisp.elisp.Optional;
import org.jetbrains.emacs4ij.jelisp.exception.WrongTypeArgumentException;
import org.jetbrains.emacs4ij.jelisp.platformDependent.LispBuffer;

public abstract class Marker {
  @Subroutine("make-marker")
  public static LispMarker makeMarker () {
    return new LispMarker();
  }

  @Subroutine("point-marker")
  public static LispMarker pointMarker (Environment environment) {
    LispBuffer buffer = environment.getBufferCurrentForEditing();
    return new LispMarker(buffer.point(), buffer);
  }

  @Subroutine ("marker-insertion-type")
  public static LispSymbol markerInsertionType (LispMarker marker) {
    return marker.getInsertionType();
  }

  @Subroutine("set-marker-insertion-type")
  public static LispObject setMarkerInsertionType (LispMarker marker, LispObject type) {
    return marker.setInsertionType(type);
  }

  @Subroutine("point-min-marker")
  public static LispMarker pointMinMarker (Environment environment) {
    LispBuffer buffer = environment.getBufferCurrentForEditing();
    return new LispMarker(buffer.pointMin(), buffer);
  }

  @Subroutine("point-max-marker")
  public static LispMarker pointMaxMarker (Environment environment) {
    LispBuffer buffer = environment.getBufferCurrentForEditing();
    return new LispMarker(buffer.pointMax(), buffer);
  }

  @Subroutine("copy-marker")
  public static LispMarker copyMarker (Environment environment, MarkerOrInteger markerOrInteger, @Optional LispObject insertionType) {
    LispMarker marker = new LispMarker();
    marker.set(markerOrInteger.getPosition(), markerOrInteger.getBuffer(environment));
    if (insertionType != null)
      marker.setInsertionType(insertionType);
    return marker;
  }

  @Subroutine("set-marker")
  public static LispMarker setMarker (LispMarker marker, LispObject markerOrInteger, @Optional LispBuffer buffer) {
    if (Predicate.isNil(buffer))
      buffer = GlobalEnvironment.INSTANCE.getBufferCurrentForEditing();
    if (!markerOrInteger.equals(LispSymbol.NIL) && !(markerOrInteger instanceof MarkerOrInteger))
      throw new WrongTypeArgumentException("integer-or-marker-p", markerOrInteger);
    Integer position = markerOrInteger instanceof MarkerOrInteger
        ? ((MarkerOrInteger) markerOrInteger).getPosition()
        : null;
    marker.set(position, buffer);
    return marker;
  }

  @Subroutine("move-marker")
  public static LispMarker moveMarker (LispMarker marker, MarkerOrInteger markerOrInteger, @Optional LispBuffer buffer) {
    return setMarker(marker, markerOrInteger, buffer);
  }

  @Subroutine("marker-position")
  public static LispObject markerPosition (LispMarker marker) {
    return marker.getPosition() == null ? LispSymbol.NIL : new LispInteger(marker.getPosition());
  }

  @Subroutine("marker-buffer")
  public static LispObject markerBuffer (LispMarker marker) {
    return Core.thisOrNil(marker.getBuffer());
  }

  @Subroutine("buffer-has-markers-at")
  public static LispSymbol bufferHasMarkersAt (Environment environment, LispObject position) {
    if (position instanceof LispInteger)
      return LispSymbol.bool(environment.getBufferCurrentForEditing().hasMarkersAt(((LispInteger) position).getData()));
    return LispSymbol.NIL;
  }

  @Subroutine("mark-marker")
  public static LispMarker markMarker (Environment environment) {
    return environment.getBufferCurrentForEditing().getMark();
  }

  @Subroutine("region-beginning")
  public static LispInteger regionBeginning (Environment environment) {
    LispObject mark = LispList.list(new LispSymbol("mark"), LispSymbol.T).evaluate(environment);
    LispInteger point = new LispInteger(environment.getBufferCurrentForEditing().point());
    if (!mark.equals(LispSymbol.NIL)) {
      if (Arithmetic.less(mark, point).toBoolean())
        return (LispInteger)mark;
    }
    return point;
  }

  @Subroutine("region-end")
  public static LispInteger regionEnd (Environment environment) {
    LispObject mark = LispList.list(new LispSymbol("mark"), LispSymbol.T).evaluate(environment);
    LispInteger point = new LispInteger(environment.getBufferCurrentForEditing().point());
    if (!mark.equals(LispSymbol.NIL)) {
      if (Arithmetic.more(mark, point).toBoolean())
        return (LispInteger)mark;
    }
    return point;
  }
}
