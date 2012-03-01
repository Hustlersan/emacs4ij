package org.jetbrains.emacs4ij.jelisp.elisp;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 3/1/12
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class CharTableUtil {
    // these magic numbers are hardcoded in chartab.c, line 31
    public static final int[] CHARTABLE_SIZE_BIT = {6, 4, 5, 7};
    
    public static final int MIN_CHARTABLE_SIZE = 65; //?

    public static final int MAX_N_EXTRA_SLOTS = 10;

    public static int[] CHARTABLE_BITS = {CHARTABLE_SIZE_BIT[1] + CHARTABLE_SIZE_BIT[2] + CHARTABLE_SIZE_BIT[3],
                                          CHARTABLE_SIZE_BIT[2] + CHARTABLE_SIZE_BIT[3],
                                          CHARTABLE_SIZE_BIT[3],
                                          0};

    public static int charTableChar (int i) {
        return 1 << CHARTABLE_BITS[i];
    }

    public static int index (int c, int depth, int minChar) {
        return ((char)c - (char)minChar) >> CHARTABLE_BITS[depth];
    }



}
