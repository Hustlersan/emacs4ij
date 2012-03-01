package org.jetbrains.emacs4ij.jelisp.subroutine;

import org.jetbrains.emacs4ij.jelisp.Environment;
import org.jetbrains.emacs4ij.jelisp.elisp.*;
import org.jetbrains.emacs4ij.jelisp.exception.ArgumentOutOfRange;
import org.jetbrains.emacs4ij.jelisp.exception.WrongTypeArgumentException;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 3/1/12
 * Time: 9:13 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BuiltinsCharTable {
    private BuiltinsCharTable() {}

    @Subroutine("make-char-table")
    public static LispCharTable makeCharTable (LispSymbol purpose, @Optional LObject init) {
        return new LispCharTable(purpose, init);
    }
    
    @Subroutine("char-table-p")
    public static LispSymbol charTableP(LObject object) {
        return LispSymbol.bool(object instanceof LispCharTable);
    }
    
    @Subroutine("char-table-subtype")
    public static LispSymbol charTableSubtype(LispCharTable charTable) {
        return charTable.getSubtype();
    }

    @Subroutine("char-table-parent")
    public static LObject charTableParent (LispCharTable table) {
        return table.getParent();
    }
    
    @Subroutine("set-char-table-parent")
    public static LObject setCharTableParent (LispCharTable table, LispObject newParent) {
        if (newParent instanceof LispCharTable || newParent.equals(LispSymbol.ourNil)) {
            table.setParent(newParent);
            return newParent;
        }
        throw new WrongTypeArgumentException("char-table-p", newParent.toString());
    }
    
    @Subroutine("char-table-extra-slot") 
    public static LObject charTableExtraSlot (LispCharTable table, LispInteger n) {
        if (n.getData() < 0 || n.getData() > 9)
            throw new ArgumentOutOfRange(table, n);
        return table.getExtraSlot(n.getData());
    }

    @Subroutine("set-char-table-extra-slot")
    public static LObject setCharTableExtraSlot (LispCharTable table, LispInteger n, LObject value) {
        if (n.getData() < 0 || n.getData() > 9)
            throw new ArgumentOutOfRange(table, n);
        table.setExtraSlot(n.getData(), value);
        return value;
    }

    private static LispInteger checkChar(LObject object) {
        if (!BuiltinPredicates.isCharacter(object))
            throw new WrongTypeArgumentException("characterp", object.toString());
        return (LispInteger)object;
    }
    
    @Subroutine("char-table-range")
    public static LObject charTableRange (Environment environment, LispCharTable table, LObject range) {
        if (range.equals(LispSymbol.ourNil)) {
            return table.getDefault();
        } else if (range.equals(LispSymbol.ourT)) {
            //todo: return all values
        } else if (range instanceof LispInteger) { //i.e. char
            //todo: return char's value
        } else if (range instanceof LispList) {
            LispInteger from = checkChar(((LispList) range).car());
            LispInteger to   = checkChar(((LispList) range).cdr());
            //todo: return range (inclusive from & to) with value
        } else {
            BuiltinsCore.error(environment, "Invalid RANGE argument to `set-char-table-range'");
        }
        return null;
    }
    
    @Subroutine("set-char-table-range")
    public static LObject setCharTableRange (Environment environment, LispCharTable table, LObject range, LObject value) {
        if (range.equals(LispSymbol.ourNil)) {
            table.setDefault(value);
        } else if (range.equals(LispSymbol.ourT)) {
            //todo: affect all values            
        } else if (range instanceof LispInteger) { //i.e. char
            //todo: set char's value
        } else if (range instanceof LispList) {
            LispInteger from = checkChar(((LispList) range).car());
            LispInteger to = checkChar(((LispList) range).cdr());
            table.setRange(from.getData(), to.getData(), value);
        } else {
            BuiltinsCore.error(environment, "Invalid RANGE argument to `set-char-table-range'");
        }
        return value;
    }
}


