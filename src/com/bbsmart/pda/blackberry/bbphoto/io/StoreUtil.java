package com.bbsmart.pda.blackberry.bbphoto.io;

import java.util.Hashtable;

/**
 * Convenience methods for interacting with the store and persistent objects
 * 
 */
public final class StoreUtil {
    public static boolean unwrapBool(Hashtable hash, String name) {
        return ((Boolean) hash.get(name)).booleanValue();
    }

    public static int unwrapInt(Hashtable hash, String name) {
        return ((Integer) hash.get(name)).intValue();
    }
}
