package org.jetbrains.emacs4ij;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.FocusChangeListener;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.emacs4ij.jelisp.Environment;
import org.jetbrains.emacs4ij.jelisp.GlobalEnvironment;
import org.jetbrains.emacs4ij.jelisp.elisp.LispBuffer;
import org.jetbrains.emacs4ij.jelisp.elisp.LispFrame;
import org.jetbrains.emacs4ij.jelisp.elisp.LispObject;
import org.jetbrains.emacs4ij.jelisp.elisp.LispWindow;
import org.jetbrains.emacs4ij.jelisp.exception.NoEditorException;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 12/17/11
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class IdeaWindow implements LispWindow {
    private int myId;
    private LispBuffer myBuffer;
    private final LispFrame myFrame;
    private Editor myEditor = null;

    public IdeaWindow (int id, LispBuffer buffer, LispFrame frame, Editor editor) {
        myId = id;
        myBuffer = buffer;
        myFrame = frame;
        setEditor(editor);
    }

    private void setEditor (@Nullable Editor editor) {
        myEditor = editor;
        if (editor == null)
            return;
        ((EditorEx) myEditor).addFocusListener(new FocusChangeListener() {
            @Override
            public void focusGained(Editor editor) {
                if (myBuffer != null)
                    GlobalEnvironment.INSTANCE.switchToBuffer(myBuffer);
            }

            @Override
            public void focusLost(Editor editor) {
            }
        });
    }

    public Editor getEditor() {
        return myEditor;
    }

    @Override
    public LispFrame getFrame() {
        return myFrame;
    }

    @Override
    public LispBuffer getBuffer() {
        return myBuffer;
    }

    @Override
    public void setActive() {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(IdeaBuffer.getProject());
        VirtualFile[] openedFiles = fileEditorManager.getOpenFiles();
        for (VirtualFile file: openedFiles) {
            FileEditor[] editors = fileEditorManager.getAllEditors(file);
            for (FileEditor fileEditor: editors) {
                if (((TextEditor)fileEditor).getEditor() == myEditor) {
                    Editor opened = fileEditorManager
                            .openTextEditor(new OpenFileDescriptor(IdeaBuffer.getProject(), file), true);
                    assert opened == myEditor;
                    return;
                }
            }
        }
        throw new NoEditorException();
    }

    @Override
    public String toString() {
        return "#<window " + myId + " on " + myBuffer.getName() + '>';
    }

    @Override
    public LispObject evaluate(Environment environment) {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdeaWindow)) return false;

        IdeaWindow that = (IdeaWindow) o;

        if (myId != that.myId) return false;
        if (myBuffer != null ? !myBuffer.equals(that.myBuffer) : that.myBuffer != null) return false;
        if (myEditor != null ? !myEditor.equals(that.myEditor) : that.myEditor != null) return false;
        if (myFrame != null ? !myFrame.equals(that.myFrame) : that.myFrame != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = myId;
        result = 31 * result + (myBuffer != null ? myBuffer.hashCode() : 0);
        result = 31 * result + (myFrame != null ? myFrame.hashCode() : 0);
        result = 31 * result + (myEditor != null ? myEditor.hashCode() : 0);
        return result;
    }

    @Override
    public void close() {
        myEditor = null;
        closeTab();
    }

    @Override
    public boolean isVisible() {
        return myEditor != null;
    }

    @Override
    public void open (@NotNull Editor editor) {
        assert myEditor == null;
        setEditor(editor);
    }

    @Override
    public void closeTab() {
        VirtualFile file = ((IdeaBuffer) myBuffer).getFile();
        if (file == null)
            return;
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(IdeaBuffer.getProject());
        fileEditorManager.closeFile(file);
    }

    @Override
    public Integer getDisplayStart() {
        //todo
        return 1;
    }
}
