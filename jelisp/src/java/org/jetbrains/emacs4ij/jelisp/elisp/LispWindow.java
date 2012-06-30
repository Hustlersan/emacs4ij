package org.jetbrains.emacs4ij.jelisp.elisp;

import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 12/17/11
 * Time: 12:33 PM
 * To change this template use File | Settings | File Templates.
 */
public interface LispWindow extends LispObject {
    Editor getEditor();
    LispFrame getFrame();
    LispBuffer getBuffer();
    void setActive();
    boolean isVisible();
    void close();
    void open (@NotNull Editor editor);
    void closeTab();

    /**
     *
     * @return the position in displayed buffer, at which the visible part of buffer starts and NULL if none
     */
    @Nullable
    Integer getDisplayStart();
}
