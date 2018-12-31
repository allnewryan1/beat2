package com.gladen.beat;

/**
 * COPYRIGHT Gladen Software 2018
 */

import java.io.*;
import java.awt.GraphicsEnvironment;
import java.net.URISyntaxException;

import javax.swing.SwingUtilities;

public class Main{
    public static PrintStream err = new PrintStream(System.err);
    public static PrintStream out;
    static DummyPrefs prefs;

    public static void main (String [] args) throws IOException, InterruptedException, URISyntaxException{
        prefs = new DummyPrefs();
        System.setErr(new PrintStream(new NullOutputStream()));
        Console console = System.console();
        if(console == null && !GraphicsEnvironment.isHeadless()){
            CommandWindow commandWindow = new CommandWindow();
            out = commandWindow.out;
            //System.setErr(out);
            SwingUtilities.invokeLater(() -> {
                commandWindow.setVisible(true);
            });
            Login.main(new String[0]);
        } else{
            out = System.out;
            Login.main(new String[0]);
        }
    }

    private static class NullOutputStream extends OutputStream {
        public NullOutputStream() {
        }

        @Override
        public void write(int b){
            return;
            }
        @Override
        public void write(byte[] b){
            return;
            }
        @Override
        public void write(byte[] b, int off, int len){
            return;
        }
    }
}