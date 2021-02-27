package com.gladen.beat;

/*
 * COPYRIGHT Gladen Software 2020
 */

import java.io.*;
import java.awt.GraphicsEnvironment;

import javax.swing.SwingUtilities;

public class Main{
    public static PrintStream err = new PrintStream(System.err);
    public static PrintStream out;
    static DummyPrefs prefs;

    public static void main (String [] args) {
        prefs = DummyPrefs.getPrefs();
        System.setErr(new PrintStream(new NullOutputStream()));
        Console console = System.console();
        if(console == null && !GraphicsEnvironment.isHeadless()){
            CommandWindow commandWindow = new CommandWindow();
            out = commandWindow.out;
            SwingUtilities.invokeLater(() -> commandWindow.setVisible(true));
        } else{
            out = System.out;
        }
        Login.main(new String[0]);
    }

    private static class NullOutputStream extends OutputStream {
        public NullOutputStream() {
        }

        @Override
        public void write(int b){}
        @Override
        public void write(byte[] b){}
        @Override
        public void write(byte[] b, int off, int len){}
    }
}