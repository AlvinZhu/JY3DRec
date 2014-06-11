package com.alvin.utilities;

/**
 *
 */
public final class DebugManager {
    //private static final String SYSTEM_DEBUG_KEY="system.Debug";
    //public static final int debug = setSystemDebug(SYSTEM_DEBUG_KEY);
    //public static final int debug = 0;
    //public static final int debug = 1;
    //public static final int debug = 2;
    public static final int debug = 3;

    private static int setSystemDebug(String key) {
        int debug = 0;
        String sDebug = System.getProperty(key);

        if (null != sDebug) {
            debug = Integer.parseInt(sDebug);
        }

        return debug;
    }

    public static int getSystemDebug() {
        return debug;
    }

    public static void message(String message) {
        if (1 <= debug) System.out.println(message);
    }

    public static void warning(String message) {
        if (2 <= debug) System.out.println(message);
    }

    public static void error(String message) {
        if (3 <= debug) System.err.println(message);
    }

    public static void error(Throwable e) {
        if (3 <= debug) System.err.println(e.getLocalizedMessage());
    }
}
