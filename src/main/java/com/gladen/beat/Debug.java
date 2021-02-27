package com.gladen.beat;

public class Debug {

    public static void log (String s) {
        if (build.type == build.TYPE.DEBUG) Main.out.println("DEBUG: " + s);
    }
}
