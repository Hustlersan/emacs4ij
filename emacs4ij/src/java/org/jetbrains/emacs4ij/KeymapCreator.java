package org.jetbrains.emacs4ij;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.emacs4ij.jelisp.elisp.LispKeymap;
import org.jetbrains.emacs4ij.jelisp.elisp.LispKeymapFactory;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 3/15/12
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class KeymapCreator implements LispKeymapFactory {
    public KeymapCreator () {}
    
    @Override
    public LispKeymap createKeymap(@Nullable String name) {
        return createKeymap(name, null);
    }

    @Override
    public LispKeymap createKeymap(@Nullable String name, @Nullable LispKeymap parent) {
        return new IdeaKeymap(name, parent);
    }
}
