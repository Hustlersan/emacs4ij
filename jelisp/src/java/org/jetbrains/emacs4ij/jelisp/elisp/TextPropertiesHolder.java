package org.jetbrains.emacs4ij.jelisp.elisp;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.emacs4ij.jelisp.exception.ArgumentOutOfRange;
import org.jetbrains.emacs4ij.jelisp.subroutine.Core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kate
 * Date: 5/11/12
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class TextPropertiesHolder {
    protected List<TextPropertiesInterval> myIntervals;

    public TextPropertiesHolder () {
        myIntervals = new ArrayList<>();
    }

    public boolean noTextProperties() {
        return myIntervals.isEmpty();
    }

    protected abstract int size();

    protected void setTextProperties (List<TextPropertiesInterval> intervals) {
        myIntervals = intervals;
    }

    /**
     * @param givenStart = left bound of interval
     * @param givenEnd = right bound of interval
     * @param propertyList to work with
     * @return true if any properties were added or changed, false otherwise
     */
    public boolean actOnTextProperties(int givenStart, int givenEnd, LispList propertyList, TextPropertiesInterval.Action action) {
        Range range;
        try {
            int start = this instanceof LispBuffer ? givenStart - 1 : givenStart;
            int end   = this instanceof LispBuffer ? givenEnd - 1 : givenEnd;
            range = new Range(start, end, 0, size());
        } catch (ArgumentOutOfRange e) {
            throw new ArgumentOutOfRange(givenStart, givenEnd);
        }
        if (range.isEmpty())
            return false;
        if (noTextProperties()) {
            switch (action) {
                case ADD:
                    myIntervals.add(new TextPropertiesInterval(range.getStart(), range.getEnd(), 0, size(), propertyList));
                    return true;
                case SET:
                    if (propertyList.equals(LispList.list(LispSymbol.ourNil, LispSymbol.ourNil)))
                        return false;
                    myIntervals.add(new TextPropertiesInterval(range.getStart(), range.getEnd(), 0, size(), propertyList));
                    return true;
                case REMOVE:case REMOVE_LIST:
                    return false;
            }
        }
        List<TextPropertiesInterval> additionalIntervals = new ArrayList<>();
        boolean changed = false;
        for (Iterator<TextPropertiesInterval> iterator = myIntervals.iterator(); iterator.hasNext(); ) {
            TextPropertiesInterval interval = iterator.next();
            if (!interval.getRange().contains(range.getStart())) {
                continue;
            }
            int oldEnd = interval.getRange().getEnd();
            changed = interval.extractIntervalAndPerformAction(range, propertyList, action, additionalIntervals);
            if (interval.hasNoProperties())
                iterator.remove();
            if (oldEnd < range.getEnd()) {
                range.setStart(oldEnd);
                continue;
            }
            break;
        }
        myIntervals.addAll(additionalIntervals);
        Collections.sort(myIntervals);
        return changed;
    }

    protected List<TextPropertiesInterval> getIntervals(){
        return myIntervals;
    }

    protected String intervalsString() {
        StringBuilder sb = new StringBuilder();
        for (TextPropertiesInterval interval: myIntervals) {
            sb.append(interval.toString()).append(" ");
        }
        return sb.toString().trim();
    }

    protected List<TextPropertiesInterval> getTextPropertiesInRange (int start, int end) {
        if (start < 0 || end > size())
            throw new ArgumentOutOfRange(start, end);
        List<TextPropertiesInterval> list = new ArrayList<>();
        for (TextPropertiesInterval interval : myIntervals) {
            if (interval.getRange().getEnd() <= start) {
                continue;
            }
            if (interval.getRange().getStart() >= end) {
                break;
            }
            list.add(interval.extract(start, end));
        }
        Collections.sort(list);
        return list;
    }

    @Nullable
    private TextPropertiesInterval getTextPropertiesIntervalFor (int position) {
        if (position == size())
            return null;
        List<TextPropertiesInterval> properties = getTextPropertiesInRange(position, position);
        assert properties.size() <= 1 : "Multiple text properties intervals for position";
        if (properties.isEmpty())
            return null;
        return properties.get(0);
    }

    public LispObject getTextPropertiesAt (int position) {
        TextPropertiesInterval interval = getTextPropertiesIntervalFor(position);
        return interval == null
                ? LispSymbol.ourNil
                : interval.getPropertyList();
    }

    public LispObject getTextPropertyAt (int position, LispObject property) {
        TextPropertiesInterval interval = getTextPropertiesIntervalFor(position);
        return interval == null
                ? LispSymbol.ourNil
                : Core.thisOrNil(interval.getProperties().get(property));
    }

}
